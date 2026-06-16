(ns mankova-admin-backend.core
  (:require
   [org.httpkit.server :as server]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.multipart-params :refer [wrap-multipart-params]]
   [ring.middleware.json :refer [wrap-json-params]]
   [compojure.core :refer :all]
   [clj-http.client :as http]
   [clojure.data.json :as json]
   [ring.middleware.defaults :refer :all]
   [cheshire.core :as che]
   [compojure.route :as route]
   [mankova-admin-backend.tg-auth :as auth]
   [mankova-admin-backend.datamodule :as dm]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [clojure.string :as s]
   )
  (:import [java.util.zip GZIPInputStream]
           [java.io ByteArrayInputStream ByteArrayOutputStream]
           (javax.crypto Mac)
           (javax.crypto.spec SecretKeySpec)
           )
  (:gen-class)
  )

(use '[clojure.java.shell :only [sh]])


(def api_keys (with-open [r (io/reader "./api_keys.edn")]
                (edn/read (java.io.PushbackReader. r)))
  )







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

            parsed (che/parse-string data true)]

        (io/delete-file temp-file)

        {:status :success
         :data parsed}))))












(defn file->bytes [file]
  (with-open [xin (io/input-stream file)
              xout (java.io.ByteArrayOutputStream.)]
    (io/copy xin xout)
    (.toByteArray xout)))



(defn vendore_code_get [req]
  (let [req_body (:params req)
        filters (:filters req_body)
        search_string (:search_string req_body)
        db_res (dm/db_query_sender "" dm/product_get_vendor_codes_sql {:filters filters :search_string search_string})]
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))


(defn archive_product [req]
  (let [req_body (:params req)
        actual (:actual req_body)
        vendor_codes (:vendor_codes req_body) 
        product_ids (:product_ids req_body)
        db_res (dm/db_query_sender "" dm/product_to_archive_sql {:actual actual :vendor_codes vendor_codes :product_ids product_ids})]
    (println "archive_product")
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))


(defn filters_get [req]
  (let [req_body (:params req)
        filters (:filters req_body)
        db_res (dm/db_query_sender "" dm/product_attribute_get_filter_sql {:attribute_names filters})]
    (println "filters_get")
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))


(defn product_get [req]
  (let [req_body (:params req)
        filters (:filters req_body)
        search_string (:search_string req_body)
        db_res (dm/db_query_sender "" dm/product_get_one_per_row_sql {:filters filters :search_string search_string})]
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))




(defn attribute_add [req]
  (println "-------------------attribute_add----------------")
  (let [req_body (:params req)
        attributes (:attributes req_body)
        refresh? (:refresh? req_body)
        product_id (:product_id (first attributes))

        processed-attrs (if (some #(= "images" (:attribute_name %)) attributes)
                          (vec (remove #(= "images" (:attribute_name %)) attributes))
                          attributes)

        db_res (dm/db_query_sender "" dm/product_attribute_add_sql processed-attrs)

        images (:attribute_value (first (filter #(= "images" (:attribute_name %)) attributes)))]
    
    (if-let [result (when refresh? (fetch-stock-data))]
      (case (:status result)
        :success (dm/db_query_sender "" dm/product_storage_moysklad_stock_upd_sql (:data result))
        :error (println "Error:" (:message result))))

    (println attributes)

    (if-not (nil? images)
      (let [process-images (when (and product_id (seq images))
                             (let [copy-image (fn [old-path]
                                                (let [old-file (io/file "/home/timofey/mankova/mankova_img/img_raw" old-path)
                                                      db-res (dm/db_query_sender "" dm/product_get_image_name_sql
                                                                                 {:product_id product_id :filename old-path})
                                                      new-name (some-> db-res first :_result :image_name)]
                                                  (when (and (.exists old-file) new-name)
                                                    (let [new-file (io/file "/home/timofey/mankova/mankova_img/img_raw" new-name)]
                                                      (io/copy old-file new-file)
                                                      new-name))))]
                               (doall (keep copy-image images))))

            new-names (vec process-images)

            db_new_res (dm/db_query_sender "" dm/product_attribute_add_sql [{:product_id product_id
                                                                             :attribute_name "images"
                                                                             :attribute_value new-names}])]))
    (println "finished")

    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/write-str db_res)}))




(defn product_add [req]
  (let [req_body (:params req)
        attributes (:attributes req_body) 
        db_res (dm/db_query_sender "" dm/product_add_sql attributes)
        processed-res (map #(update % :_product_id str) db_res)
        ]
    (println "product_add")
    (println processed-res) 
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str processed-res)}))


(defn product_filter_attribute_add [req]
  (let [req_body (:params req)
        filters (:filters req_body)
        set_attribute_name (:set_attribute_name req_body)
        set_attribute_value (:set_attribute_value req_body)
        db_res (dm/db_query_sender "" dm/product_attribute_add_using_filter_sql {:set_attribute_value set_attribute_value :set_attribute_name set_attribute_name :filters filters})] 
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)})
  

  )




(defn picture_upload [req]
  (let [params (:params req)
        product_id (:product_id params)
        file (:tempfile (:file params))
        base-path "/home/timofey/mankova/mankova_img/img_raw"
        db_res (dm/db_query_sender "" dm/product_get_image_name_sql {:product_id product_id :filename (:filename (:file params))})
        filename (->> db_res first :_result :image_name)
        dest-path (str base-path "/" filename)]

    (clojure.java.io/copy file (java.io.File. dest-path))

    (Thread/sleep 5000) 

    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/write-str {:filename filename})
     }
    )
  )



(defn price_set [req]
  (let [req_body (:params req)
        prices (:prices req_body)
        db_res (dm/db_query_sender "" dm/price_set_sql prices)]
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)})
  )


(defn product_del [req]
  (let [req_body (:params req)
        product_ids (:product_ids req_body)
        db_res (dm/db_query_sender "" dm/product_del_sql {:product_ids product_ids})]
    (println "product_del")
    (println db_res) 
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))


(defn vendor_code_del [req]
  (let [req_body (:params req)
        vendor_code (:vendor_code req_body)
        db_res (dm/db_query_sender "" dm/product_del_vendor_code {:vendor_code vendor_code})]
    (println "vendor_code_del")
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))


(defn images_del [req]
  (let [req_body (:params req)
        product_ids (:product_ids req_body)
        base-path "/home/timofey/mankova/mankova_img/img_raw"]

    (println "vendor_code_del")

    (doseq [filename product_ids]
      (let [file (io/file base-path filename)]
        (when (.exists file)
          (io/delete-file file) 
          )
        )
      )

    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str {:ok true})}))


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



(defn banner_add [req]
  (let [req_body (:params req)
        banner_id (:banner_id req_body)
        banner_location (:banner_location req_body)
        banner_name (:banner_name req_body)
        date_start (:date_start req_body)
        date_end (:date_end req_body) 
        banner_images (:banner_images req_body) 
        db_res (dm/db_query_sender "" dm/banner_add_sql {:banner_id banner_id :banner_location banner_location :banner_name banner_name :date_start date_start :date_end date_end :banner_images banner_images})]
    (println "banner_add")
    (println {:banner_id banner_id :banner_location banner_location :banner_name banner_name :date_start date_start :date_end date_end :banner_images banner_images})
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))


(defn banner_del [req]
  (let [req_body (:params req)
        images (:images req_body)
        base-path "/home/timofey/mankova/banners"
        ]

    (println "banner_del")

    (println images)

    (doseq [filename images]
      (let [file (io/file base-path filename)]
        (when (.exists file)
          (io/delete-file file))
        )
      )

    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str {:ok true})}))


(defn banner_picture_upload [req]
  (println "banner_picture_upload")
  (let [params (:params req)
        product_id (:product_id params)
        file (:tempfile (:file params))
        base-path "/home/timofey/mankova/banners"
        filename (:filename (:file params))
        dest-path (str base-path "/" filename)]
    
    (println "загружаю" filename)

    (clojure.java.io/copy file (java.io.File. dest-path))

    (Thread/sleep 5000)

    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/write-str {:filename filename})}))




(defn user_get_init [req]
  (println "user_get_init")
  (let [params (:params req)
        telegram_user_id (:telegram_user_id params)
        db_res (dm/db_query_sender "" dm/user_user_get_init_sql {:telegram_user_id telegram_user_id})]
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))




(defn login [req]
  (let [req_body (:params req)
        api_key (:api_key req_body)
        db_res (dm/db_query_sender "" dm/banner_get_sql {:api_key api_key})]
    (println "login")
    (println db_res)
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json/write-str db_res)}))


(defroutes api-routes
  (POST  "/vendore-code-get"                []  vendore_code_get)
  (POST  "/archive-product"                 []  archive_product)
  (POST  "/filters-get"                     []  filters_get)
  (POST  "/product-get"                     []  product_get)
  (POST  "/product-attribute-add"           []  attribute_add)
  (POST  "/product-add"                     []  product_add)
  (POST  "/product-filter-attribute-add"    []  product_filter_attribute_add)
  (POST  "/picture-upload"                  []  picture_upload)
  (POST  "/price-set"                       []  price_set)
  (POST  "/product-del"                     []  product_del)
  (POST  "/vendor-code-del"                 []  vendor_code_del)
  (POST  "/articul-img-del"                 []  images_del)
  (POST  "/banner-get"                      []  banner_get)
  (POST  "/banner-add"                      []  banner_add)
  (POST  "/banner-del"                      []  banner_del)
  (POST  "/banner-picture-upload"           []  banner_picture_upload)
  (POST  "/user-get-init"                   []  user_get_init)
  )


(defroutes login_route
  (POST  "/login"                           []  login)
  )





(defroutes app-routes
  (context "/api" []
    (-> api-routes
        (auth/wrap-telegram-auth (:bot-token api_keys)))
    ) 
  (route/not-found "There is no route you are looking for"))






(def app (-> app-routes (wrap-defaults api-defaults) wrap-params wrap-multipart-params (wrap-json-params {:keywords? true}) (wrap-cors :access-control-allow-origin [#".*"]
                                                                                                               :access-control-allow-methods [:post :get])))

(defn -main [& args]
  (server/run-server app {:port 3401
                          :max-body 1000000000
                          :max-ws 1000000000
                          :max-line 1000000000
                          :timeout 3600000}
                     )
  (println "Server started on port 3401")
  )
