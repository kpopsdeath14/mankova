(ns mankova-admin.events.product-filter-attribute-add
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]
            [mankova-admin.events.articul-get :refer [articul_get]]
            [mankova-admin.events.product-get :refer [product_get]]
            )
  )


(defn product_filter_attribute_add_handler [[ok? response]]
  (product_get (:filters_picked @app-state) (:current_vendor_code @app-state))
  (articul_get {:id [(:id (:current_articul @app-state))]})
  (swap! app-state assoc :selected_products_product_edit [])
  )




(defn product_filter_attribute_add [product_ids attribute_name attribute_value]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "product-filter-attribute-add")
      :method :post
      :params {:filters [{:attribute_name "product_id"
                          :attribute_values product_ids
                          }
                         ]
               :set_attribute_name attribute_name
               :set_attribute_value attribute_value
               }
      :handler product_filter_attribute_add_handler
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))