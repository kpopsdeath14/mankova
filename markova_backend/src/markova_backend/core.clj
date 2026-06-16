(ns markova-backend.core
  (:require
   [org.httpkit.server :as server]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.json :refer [wrap-json-params]]
   [compojure.core :refer :all]
   [clojure.data.json :as json]
   [clj-http.client :as http]
   [ring.middleware.defaults :refer :all]
   [compojure.route :as route]
   [markova-backend.datamodule :as dm] 
   [markova-backend.cdek :as cdek]
   [markova-backend.crm :as crm]
   [clojure.string :as s] 
   [clojure.core.async :as async]
   [cheshire.core :as che]
   [clojure.data.codec.base64 :as base64]
   [markova-backend.tg-auth :as auth]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [clojure.pprint :as pprint]
   )
  (:import [java.util.zip GZIPInputStream]
           [java.io ByteArrayInputStream ByteArrayOutputStream]
           (javax.crypto Mac)
           (javax.crypto.spec SecretKeySpec)
           )
  (:gen-class)
  )



(def api_keys (with-open [r (io/reader "./api_keys.edn")] 
                (edn/read (java.io.PushbackReader. r))
                )
  )


(defn product_get [req]
  (let [req_body (:params req)
        telegram_user_id (:telegram_user_id req_body)
        filters (:filters req_body)
        search_string (:search_string req_body)
        db_res (dm/db_query_sender "" dm/product_product_get_sql {:telegram_user_id telegram_user_id :search_string search_string :filters filters})]
    (println "product_get")
    ;(println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))


(defn filter_get [req]
  (let [req_body (:params req)
        telegram_user_id (:telegram_user_id req_body)
        db_res (dm/db_query_sender "" dm/product_product_attribute_get_filter {:telegram_user_id telegram_user_id})]
    (println "filter_get")
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))


(defn product_get_single [req]
  (let [req_body (:params req)
        telegram_user_id (:telegram_user_id req_body)
        vendor_code (:vendor_code req_body)
        product_color (:product_color req_body)
        db_res (dm/db_query_sender "" dm/product_product_get_single_sql {:telegram_user_id telegram_user_id :vendor_code vendor_code :product_color product_color})]
    (println "product_get_single")
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))




(defn cart_get [req]
  (let [req_body (:params req)
        telegram_user_id (:telegram_user_id req_body)
        db_res (dm/db_query_sender "" dm/user_cart_get_sql {:telegram_user_id telegram_user_id})]
    (println "cart_get")
    (println req)
    (println req_body)
    ;(println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))


(defn cart_get_summary [req]
  (let [req_body (:params req)
        telegram_user_id (:telegram_user_id req_body)
        db_res (dm/db_query_sender "" dm/user_cart_get_cummary_sql {:telegram_user_id telegram_user_id})]
    (println "cart_get_summary")
    ;(println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))


(defn cart_set [req]
  (let [req_body (:params req)
        telegram_user_id (:telegram_user_id req_body)
        quantity (:quantity req_body)
        summ (:summ req_body)
        product_id (:product_id req_body)
        db_res (dm/db_query_sender "" dm/user_cart_set_sql {:telegram_user_id telegram_user_id :quantity quantity :summ summ :product_id product_id})]
    (println "cart_set")
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)})
  )


(defn user_add [req]
  (let [req_body (:params req)
        telegram_user_id (:telegram_user_id req_body)
        db_res (dm/db_query_sender "" dm/user_user_add_sql {:telegram_user_id telegram_user_id})
        ]
    (println "user_add")
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))

(defn user_attribute_get [req]
  (let [req_body (:params req)
        telegram_user_id (:telegram_user_id req_body)
        db_res (dm/db_query_sender "" dm/user_user_attribute_get {:telegram_user_id telegram_user_id})]
    (println "user-get-init")
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))








(defn fetch-stock-data []
  (let [url "https://api.moysklad.ru/api/remap/1.2/report/stock/all"
        headers {"Authorization" "Bearer f87476d7568d1be088ab7c3699d2ab3cb04b6567"
                 "Accept-Encoding" "gzip"}
        temp-file "a.zip"]
    
    (let [response (http/get url {:headers headers
                                  :as :byte-array
                                  :decompress-body false})]
      (io/copy (:body response) (io/file temp-file))
    
      (let [data (with-open [in (io/input-stream temp-file)
                             gz (GZIPInputStream. in)
                             out (ByteArrayOutputStream.)]
                   (io/copy gz out)
                   (.toString out "UTF-8"))
    
            parsed (che/parse-string data true) 
            ]
    
        (io/delete-file temp-file)
    
        {:status :success
         :data parsed}
        )
      )
    )
  )


(defn start-sync! []
  (async/go-loop []
    (let [result (fetch-stock-data)]
      (case (:status result)
        :success (do
                   (println "Data fetched successfully!")
                   (dm/db_query_sender "" dm/product_storage_moysklad_stock_upd_sql (:data result))
                   )
        :error (println "Error:" (:message result))))
    (async/<! (async/timeout (* 60 60 1000)))
    (recur)
    )
  )







(defn fetch-stock-report 
  ([filters] (fetch-stock-report filters "f87476d7568d1be088ab7c3699d2ab3cb04b6567"))
  ([filters token]
   (println "-----------------------------------fetch-stock-report-----------------------------------")
   (println filters)
   (println (->> filters
                 (map (fn [filter-map]
                        (->> filter-map
                             (map (fn [[k v]]
                                    (str "filter=" (name k) "=https://api.moysklad.ru/api/remap/1.2/entity/"
                                         (name k) "/" v)))
                             (s/join "&"))))
                 (s/join "&")
                 )
            )
   (let [base-url "https://api.moysklad.ru/api/remap/1.2/report/stock/all"
         
         query-params (->> filters
                           (map (fn [filter-map]
                                  (->> filter-map
                                       (map (fn [[k v]]
                                              (str "filter=" (name k) "=https://api.moysklad.ru/api/remap/1.2/entity/"
                                                   (name k) "/" v)))
                                       (s/join "&"))))
                           (s/join "&")
                           )
         
         url (if (empty? query-params)
               base-url
               (str base-url "?" query-params))
         
         headers {"Authorization" (str "Bearer " token)
                  "Accept-Encoding" "gzip"}
         
         temp-file (str "moysklad_temp_" (System/currentTimeMillis) ".zip")
         ]

     (let [response (http/get url {:headers headers
                                   :as :byte-array
                                   :decompress-body false})]
       (io/copy (:body response) (io/file temp-file))

       (let [data (with-open [in (io/input-stream temp-file)
                              gz (GZIPInputStream. in)
                              out (ByteArrayOutputStream.)]
                    (io/copy gz out)
                    (.toString out "UTF-8"))

             parsed (che/parse-string data true)

             db_res (dm/db_query_sender "" dm/product_storage_moysklad_stock_upd_sql parsed)] 

         (println parsed)

         (println "\n\n\n\n\n\n\n")

         (println db_res)

         (println "-----------------------------------fetch-stock-report-----------------------------------")

         (io/delete-file temp-file)

         db_res

         )
       )
     )
     )
     )




(defn moysklad_update [req]
  (let [req_body (:params req)
        filters (:filters req_body)
        db_res (fetch-stock-report filters "f87476d7568d1be088ab7c3699d2ab3cb04b6567")
        ]
    (println "moysklad_update")
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}
    )
  )





(defn order_payments_add [req]
  (let [req_body (:params req)
        telegram_user_id (:telegram_user_id req_body)
        order_data (:order_data req_body) 
        db_res (dm/db_query_sender "" dm/order_payments_add_sql {:telegram_user_id telegram_user_id}) 
        history_add_res (dm/db_query_sender "" dm/exchange_history_add_sql {:service_type "app"
                                                                            :history_id (-> db_res first :payments_add :payment_id)
                                                                            :request (assoc order_data :tariff_code (case (:delivery_type order_data)
                                                                                                                      "cdek" 136
                                                                                                                      "courier" 137))
                                                                            :context {:user_id (-> db_res first :payments_add :user_id)
                                                                                      :payment_id (-> db_res first :payments_add :payment_id)}})
        ] 
    (println "order_payments_add")
    (println db_res) 
    (println history_add_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str {:user_id (-> db_res first :payments_add :user_id)
                               :payment_id (-> db_res first :payments_add :payment_id)
                               :history_id (-> history_add_res first :_r :history_id)}
                              )
     }
    )
    )




(defn webhook_pay [req]
  (println "успешная оплата")
  (let [body (:params req)
        
        cart_complete_res (dm/db_query_sender "" dm/user_cart_set_complete_sql {:payment_id (:InvoiceId body)})

        order_id (:order_id (:cart_set_complete (first cart_complete_res)))

        history_upd_res (dm/db_query_sender "" dm/exchange_history_upd_sql {:service_type "cloud_payments"
                                                                            :history_id (:InvoiceId body)
                                                                            :response (assoc body :Data (json/read-str (:Data body)))
                                                                            :context {:payment_id (:InvoiceId body)
                                                                                      :order_id order_id
                                                                                      }
                                                                            }
                                            )
        
        history_get_cdek_res (dm/db_query_sender "" dm/history_get_cdek_sql {:payment_id (:InvoiceId body)})

        cdek_create_order_res (cdek/cdek_create_order (:_r (first history_get_cdek_res)))

        cdek_history_add_res (dm/db_query_sender "" dm/exchange_history_add_sql {:service_type "cdek"
                                                                                 :history_id order_id
                                                                                 :request (:_r (first history_get_cdek_res))
                                                                                 :response (let [response-body (:body cdek_create_order_res)]
                                                                                             (try
                                                                                               (json/read-str response-body)
                                                                                               (catch Exception e
                                                                                                 (println "Ошибка парсинга JSON:" (.getMessage e))
                                                                                                 response-body))) 
                                                                                 :context {:payment_id (:InvoiceId body)
                                                                                           :order_id order_id}})
        
        
        history_get_crm_res (dm/db_query_sender "" dm/history_get_crm_sql {:payment_id (:InvoiceId body)})
        
        print_res_0 (pprint/pprint history_get_crm_res)

        lplp_0 (println "----------------------------")
        
        crm_create_order_res (crm/crm_create_order (:exchange_history_get_crm (first history_get_crm_res)))

        lplp_1 (println "----------------------------")
        
        print_res_1 (pprint/pprint crm_create_order_res) 

        
        lplp_2 (println "----------------------------")

        crm_history_add_res (dm/db_query_sender "" dm/exchange_history_add_sql {:service_type "crm"
                                                                                :history_id order_id
                                                                                :request (:exchange_history_get_crm (first history_get_crm_res))
                                                                                :response (json/read-str (:body crm_create_order_res))
                                                                                :context {:payment_id (:InvoiceId body)
                                                                                          :order_id order_id}})
        
        print_res_2 (pprint/pprint (str "crm_history_add_res = " crm_history_add_res))
        
        res {:code 0}
        ]
    
    (println "---------------------------")
    

    (pprint/pprint cdek_history_add_res)
    
    
    {
     :status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str res)
     }
    )
  )



(defn webhook_fail [req]
  (println "оплата провалилась. полностью.")
  (let [body (:params req)
        db_res (dm/db_query_sender "" dm/order_payments_upd_sql body)
        history_upd_res (dm/db_query_sender "" dm/exchange_history_add_sql {:service_type "cloud_payments" 
                                                                            :response (assoc body :Data (json/read-str (:Data body)))})
        res {:code 0}]
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str res)
     }
    )
  )





(defn user_attribute_add [req]
  (let [req_body (:params req)
        attributes (:attributes req_body)
        db_res (dm/db_query_sender "" dm/user_user_attribute_add_sql attributes)
        ]
    (println "user_attribute_add")
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))




(defn banner_get [req]
  (let [req_body (:params req)
        banner_id (:banner_id req_body)
        banner_location (:banner_location req_body)
        banner_name (:banner_name req_body)
        date_start (:date_start req_body)
        date_end (:date_end req_body)
        db_res (dm/db_query_sender "" dm/banner_get_sql {:banner_id banner_id :banner_location banner_location :banner_name banner_name})]
    (println "banner_get")
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))


(defn user_get_init [req]
  (println "user_get_init")
  (let [params (:params req)
        telegram_user_id (:telegram_user_id params)
        db_res (dm/db_query_sender "" dm/user_user_get_init_sql {:telegram_user_id telegram_user_id})]
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))


(defn history_add [req]
  (println "history_add")
  (let [params (:params req)
        telegram_user_id (:telegram_user_id params)
        body (:body params)
        service_type (:service_type params)
        context (:context params)
        
        
        history_add_res (dm/db_query_sender "" dm/exchange_history_add_sql {:service_type service_type
                                                                            :history_id (:payment_id context)
                                                                            :request body
                                                                            :context context
                                                                            }
                                            )
        ]
    (println history_add_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str history_add_res)}))



(defn policies_get [req]
  (let [req_body (:params req)
        telegram_user_id (:telegram_user_id req_body)
        db_res (dm/db_query_sender "" dm/user_policies_get_sql {:telegram_user_id telegram_user_id})]
    (println "policies_get")
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))







(defn send-message [chat-id text]
  (let [url (str "https://api.telegram.org/bot" (:bot-token api_keys) "/sendMessage")
        params {:chat_id chat-id
                :text text}]
    (http/post url {:form-params params
                    :content-type :json})))


(defn telegram-webhook [req]
  (println "WEBHOOK")
  (println req)
  (let [body (:params req)
        message (:message body)]

    (if (= "/start" (:text message))
      (do
        (send-message (:id (:chat message)) "Здравствуйте! 
Здесь собран каталог актуальных коллекций MANKOVA — можно выбрать изделия, посмотреть детали и оформить заказ.

Начните с выбора раздела ниже и соберите свой гардероб вместе с нами 🤍") 
        )
      )

  )
  )





(defroutes api-routes 
  (POST  "/product_get_single"         []  product_get_single) 
  (POST  "/filter_get"                 []  filter_get)
  (POST  "/cart_get"                   []  cart_get)
  (POST  "/cart_get_summary"           []  cart_get_summary)
  (POST  "/cart_set"                   []  cart_set)
  (POST  "/user_add"                   []  user_add)
  (POST  "/user_attribute_get"         []  user_attribute_get)
  (POST  "/user_attribute_add"         []  user_attribute_add)
  (POST  "/moysklad_update"            []  moysklad_update)
  (POST  "/history_add"                []  history_add)
  (POST  "/policies_get"               []  policies_get)

  (POST  "/order_payments_add"         []  order_payments_add)

  (POST  "/cdek_search_city"           []  cdek/cdek_search_city)
  (POST  "/cdek_search_pvz"            []  cdek/cdek_search_pvz)
  (POST  "/cdek_shipping_calculate"    []  cdek/cdek_calculate_shipping)
  (POST  "/banner_get"                 []  banner_get)
  (POST  "/user_get_init"              []  user_get_init) 
  )

(defroutes webhook-routes
  (POST  "/telegram-webhook"   []  telegram-webhook)
  (POST  "/cloudpayments/pay"  []  webhook_pay)
  (POST  "/cloudpayments/fail" []  webhook_fail)
)

(defroutes app-routes
  (context "/api" []
    (-> api-routes
        (auth/wrap-telegram-auth (:bot-token api_keys))
        )
    )
  
  (context "/webhook" [] webhook-routes)

  (route/not-found "There is no route you are looking for")
  )






(def app (-> app-routes 
             wrap-keyword-params
             wrap-params
             wrap-json-params  
             (wrap-cors :access-control-allow-origin [#"https://web.telegram.org"
                                                      #"https://car_vrooom_tuning_bot.telegram.org"
                                                      #"https://.*\.telegram\.org"
                                                      #"https://web.z"
                                                      #"https://mankova.qq-pp.ru"
                                                      ]
                        :access-control-allow-methods [:post :get])))

(defn -main [& args]
  (server/run-server app {:port 3301
                          :max-body 1000000000
                          :max-ws 1000000000
                          :max-line 1000000000
                          :timeout 3600000})
  (start-sync!)
  (println "Server started on port 3301"))
