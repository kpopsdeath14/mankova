(ns mankova-admin.events.archive-product
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]
            [mankova-admin.events.vendore-code-get :refer [vendore_code_get]]
            [mankova-admin.events.product-get :refer [product_get]]
            )
  )



(defn archive_product_handler [[ok? response]]
  
  (swap! app-state assoc :selected_products_catalog [])
  (swap! app-state assoc :selected_products_product_edit [])
  (vendore_code_get (assoc (:filters_picked @app-state) :actual (case (:products_mode @app-state)
                                                                  "catalog" ["t" "true"]
                                                                  "archive" ["f" "false"])) (:search_string @app-state))
  
  (product_get (assoc (:filters_picked @app-state)
                      :vendor_code [(:current_vendor_code @app-state)]
                      :actual (case (:products_mode @app-state)
                                "catalog" ["t" "true"]
                                "archive" ["f" "false"])
                      )"")
  )


(defn archive_product [actual vendor_code product_ids]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "archive-product")
      :method :post
      :params {:actual actual :vendor_codes vendor_code :product_ids product_ids}
      :handler archive_product_handler
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})}))
  )