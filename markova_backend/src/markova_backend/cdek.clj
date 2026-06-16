(ns markova-backend.cdek
  (:require 
   [clojure.data.json :as json]
   [clj-http.client :as http]
   [clojure.tools.logging :as log]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [markova-backend.datamodule :as dm]
   )
  )

(def api_keys (with-open [r (io/reader "./api_keys.edn")]
                (edn/read (java.io.PushbackReader. r)))
  )

(def cdek-config (atom {:client-id (get-in api_keys [:cdek :client-id])
                        :client-secret (get-in api_keys [:cdek :client-secret])
                        :token nil
                        :token-expires 0
                        }
                       )
  )


(defn get-cdek-token []
  (println "get-cdek-token")
  (let [now (System/currentTimeMillis)
        {:keys [token token-expires]} @cdek-config]
    (if (and token (> token-expires now))
      (do
        (println "Токен есть, с ним все в порядке, он равен:")
        (println token)
        token)
      (let [response (http/post "https://api.cdek.ru/v2/oauth/token"
                                {:form-params {:grant_type "client_credentials"
                                               :client_id (:client-id @cdek-config)
                                               :client_secret (:client-secret @cdek-config)}
                                 :content-type "application/x-www-form-urlencoded"
                                 :socket-timeout 5000
                                 :conn-timeout 5000
                                 :as :json
                                 :throw-exceptions false})
            body (:body response)
            status (:status response)]

        (let [new-token (:access_token body)
              expires-in (* (:expires_in body) 1000)]
          (swap! cdek-config assoc
                 :token new-token
                 :token-expires (+ (System/currentTimeMillis) expires-in))
          new-token)))))


(defn cdek_search_city [req]
  (try
    (let [search-text (get-in req [:params :search_text])
          token (get-cdek-token)]

      (when (< (count search-text) 2)
        {:status 400
         :headers {"Content-Type" "application/json"}
         :body (json/write-str {:code 1 :message "Минимум 2 символа для поиска"})})


      (println "cdek_search_city")

      (println {:size 10
                :name search-text
                :country_codes "RU"})

      (let [response (http/get "https://api.cdek.ru/v2/location/suggest/cities"
                               {:headers {"Authorization" (str "Bearer " token)}
                                :query-params {:name search-text
                                               :country_code "RU"}
                                :socket-timeout 3000
                                :conn-timeout 3000
                                :as :json})] 

        {:status 200
         :headers {"Content-Type" "application/json"}
         :body (json/write-str
                {:code 0
                 :cities (map (fn [city]
                                {:name (:full_name city)
                                 :code (:code city)
                                 :city_uuid (:city_uuid city)})
                              (:body response))})}))

    (catch Exception e
      (log/error "Ошибка при поиске городов:" (ex-message e))
      {:status 500
       :headers {"Content-Type" "application/json"}
       :body (json/write-str
              {:code 2
               :message "Ошибка сервера при поиске городов"})})))



(defn cdek_search_pvz [req]
  (try
    (let [search-text (get-in req [:params :search_text])
          city_code (get-in req [:params :city_code])
          token (get-cdek-token)]

      (when (< (count search-text) 2)
        {:status 400
         :headers {"Content-Type" "application/json"}
         :body (json/write-str {:code 1 :message "Минимум 2 символа для поиска"})})


      (println "cdek_search_pvz")

      (let [response (http/get "https://api.cdek.ru/v2/deliverypoints"
                               {:headers {"Authorization" (str "Bearer " token)}
                                :query-params {:city_code city_code
                                               :country_code "RU"
                                               :name search-text
                                               :is_handout true}
                                :socket-timeout 3000
                                :conn-timeout 3000
                                :as :json})] 

        {:status 200
         :headers {"Content-Type" "application/json"}
         :body (json/write-str
                {:code 0
                 :pvz (map (fn [pvz]
                             {:name (:name pvz)
                              :code (:code pvz)
                              :address (:location pvz)
                              :latitude (:latitude pvz)
                              :longitude (:longitude pvz)
                              :work_time (:work_time pvz)
                              :phone (:phone pvz)})
                           (:body response))})}))

    (catch Exception e
      (log/error "Ошибка при поиске городов:" (ex-message e))
      {:status 500
       :headers {"Content-Type" "application/json"}
       :body (json/write-str
              {:code 2
               :message "Ошибка сервера при поиске городов"})})))



(defn cdek_calculate_shipping [req]
  (println "cdek_calculate_shipping")
  (println req)
  (let [tarrif_code (get-in req [:params :tarrif_code])
        telegram_user_id (get-in req [:params :telegram_user_id])
        city_code (get-in req [:params :city_code])
        weight (get-in req [:params :weight] 500)
        length (get-in req [:params :length] 15)
        width (get-in req [:params :width] 15)
        height (get-in req [:params :height] 25)
        token (get-cdek-token)
        ]
    
    
    (let [response (http/post "https://api.cdek.ru/v2/calculator/tariff"
                              {:headers {"Authorization" (str "Bearer " token)
                                         "Content-Type" "application/json"}
                               :body (json/write-str
                                      {:tariff_code (if (string? tarrif_code)
                                                      (Integer/parseInt tarrif_code)
                                                      tarrif_code)
                                       :from_location {:code 251}
                                       :to_location {:code city_code}
                                       :packages [{:weight weight
                                                   :length length
                                                   :width width
                                                   :height height}]})
                               :socket-timeout 5000
                               :conn-timeout 5000
                               :as :json})
          
          db_res (dm/db_query_sender "" dm/user_delivery_cost_get_sql {:delivery_tariff (str tarrif_code) :telegram_user_id telegram_user_id :delivery_cost (if (string? (:total_sum (:body response)))
                                                                                                                                                              (Integer/parseInt (:total_sum (:body response)))
                                                                                                                                                              (:total_sum (:body response)))
                                                                       }
                                     )
          
          ]
      
      (println "Стоимость доставки в" city_code "=" db_res)


      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/write-str {:res (:delivery_cost (:delivery_cost_get (first db_res)))})}
      
      )
      )
  )









(defn cdek_create_order [request_body]
  (let [
        token (get-cdek-token)
        ;token "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJsb2NhdGlvbjphbGwiLCJvcmRlcjphbGwiLCJwYXltZW50OmFsbCJdLCJleHAiOjE3NTU3MDE2NzUsImF1dGhvcml0aWVzIjpbInNoYXJkLWlkOnJ1LTAxIiwiY2xpZW50LWNpdHk60J3QvtCy0L7RgdC40LHQuNGA0YHQuiwg0J3QvtCy0L7RgdC40LHQuNGA0YHQutCw0Y8g0L7QsdC70LDRgdGC0YwiLCJhY2NvdW50LWxhbmc6cnVzIiwiY29udHJhY3Q60JjQnC3QoNCkLdCT0JvQky0yMiIsImFjY291bnQtdXVpZDplOTI1YmQwZi0wNWE2LTRjNTYtYjczNy00Yjk5YzE0ZjY2OWEiLCJhcGktdmVyc2lvbjoxLjEiLCJjbGllbnQtaWQtZWM1OmVkNzVlY2Y0LTMwZWQtNDE1My1hZmU5LWViODBiYjUxMmYyMiIsImNvbnRyYWN0LWlkOmRlNDJjYjcxLTZjOGMtNGNmNS04MjIyLWNmYjY2MDQ0ZThkZiIsImNsaWVudC1pZC1lYzQ6MTQzNDgyMzEiLCJjb250cmFnZW50LXV1aWQ6ZWQ3NWVjZjQtMzBlZC00MTUzLWFmZTktZWI4MGJiNTEyZjIyIiwic29saWQtYWRkcmVzczpmYWxzZSIsImZ1bGwtbmFtZTrQotC10YHRgtC40YDQvtCy0LDQvdC40LUg0JjQvdGC0LXQs9GA0LDRhtC40Lgg0JjQnCJdLCJqdGkiOiJuMjZudlpob3psZnJ5NTZEVk9XZ3dRNl9IZFEiLCJjbGllbnRfaWQiOiJ3cUd3aVF4MGdnOG1MdGlFS3NVaW5qVlNJQ0NqdFRFUCJ9.p5-Iba_c7Pi98FYTRUNhNR5FvAGy9HQQsnHy_lGJt1vK9_TK7SDRoltiMEDikHZMlLTbGisKkpu1GB9FTcR3snWaYm1C-uAwGN03RFw5uIZayHgLMzENg17LbvuKYuC8gNI9BWQjaFDyZJtIv_Gu3xtmlL_Sj4yH-vj4428JWgMRQ3lLbWXQElH9TW6J_59Z9O0m9oh9nqbdn0wgQZ-hlSbySFIQEtwMV-lIInfOU_GlXt_A0ICXEYJDAzcgcDNRtK1OIk6ZTNNqcnLNhl2eiXVUOPnPcpxQ3KygHl7_g8jrQlVFwndF0LK7jpPh7TVEOV0ztRmETHA1R2jDqAPlWA"
        
        response (http/post "https://api.cdek.ru/v2/orders"
                            {:headers {"Authorization" (str "Bearer " token)
                                       "Content-Type" "application/json"}
                             :body (json/write-str request_body)
                             :throw-exceptions false})
        ]
    response
    )
  )