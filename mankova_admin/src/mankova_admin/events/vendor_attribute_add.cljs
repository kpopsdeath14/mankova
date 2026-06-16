(ns mankova-admin.events.vendor-attribute-add
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]
            [mankova-admin.events.articul-get :refer [articul_get]]
            [mankova-admin.events.product-get :refer [product_get]]
            [mankova-admin.events.filters-get :refer [filters_get]]
            ))


(defn vendor_attribute_add_handler [[ok? response]]
  (filters_get [])
  (product_get (:filters_picked @app-state) (:current_vendor_code @app-state))

  (if (= (:product_edit_attribute_name @app-state) "vendor_code")
    (set! (.-href (.-location js/window)) (str "/#/product-edit/" (js/encodeURIComponent (get-in @app-state [:current_vendor_changes :vendor_code]))))
    ) 
  (swap! app-state assoc :current_vendor_changes {})
  )




(defn vendor_attribute_add [attribute_name attribute_value]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "product-filter-attribute-add")
      :method :post
      :params {:filters [{:attribute_name "vendor_code"
                          :attribute_values [(:current_vendor_code @app-state)]}]
               :set_attribute_name attribute_name
               :set_attribute_value attribute_value}
      :handler vendor_attribute_add_handler
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))