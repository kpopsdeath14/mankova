(ns mankova-admin.events.price-set
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]
            [mankova-admin.events.product-get :refer [product_get]]
            [mankova-admin.events.articul-get :refer [articul_get]]
            ))


(defn price_set_handler [[ok? response]]
  (if (empty? (:selected_products_product_edit @app-state))
    (product_get (assoc (:filters_picked @app-state)
                        :vendor_code [(:current_vendor_code @app-state)]
                        :actual (case (:products_mode @app-state)
                                  "catalog" ["t" "true"]
                                  "archive" ["f" "false"]))
                 ""
                 )
    
    (articul_get {:id [(:id (:articul_changes @app-state))]}) 
    )
  
  (swap! app-state assoc :selected_products_product_edit [])
  (swap! app-state assoc :current_vendor_changes {})
  )


(defn price_set [prices]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "price-set")
      :method :post
      :params {:prices prices
               }
      :handler price_set_handler
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))