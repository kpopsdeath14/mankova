(ns markova-backend.crm
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.edn :as edn]

            )
  )

(def api_keys (with-open [r (io/reader "./api_keys.edn")]
                (edn/read (java.io.PushbackReader. r))))

(defn format-date-time []
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm:ss")
           (java.util.Date.)))

(def api-key (get-in api_keys [:crm :api-key]))
(def api-url "https://mankova.retailcrm.ru/api/v5/")

(defn crm_create_order [order_data]
  (let [encoded_order (-> order_data
                          json/generate-string
                          (java.net.URLEncoder/encode "UTF-8"))
        body (str "order=" encoded_order "&site=mankova")

        request (http/post (str api-url "orders/create")
                           {:query-params {:apiKey api-key}
                            :content-type "application/x-www-form-urlencoded"
                            :body body
                            :throw-exceptions false
                            })
        ]
    (println "crm_create_order request body = " body)
    request
    )
  )