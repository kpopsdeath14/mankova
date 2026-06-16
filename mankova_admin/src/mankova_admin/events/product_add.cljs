(ns mankova-admin.events.product-add
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]
            [reagent.core :as reagent :refer [as-element]]
            [mankova-admin.events.price-set :refer [price_set]]
            [mankova-admin.events.product-attribute-add :refer [attribute_add]]
            )
  )


(defn product_add_handler [[ok? response]]
  (let [
        articul_changes (reagent/cursor app-state [:articul_changes])
        ]
    (swap! app-state assoc :product_new_product_id (:_product_id (first response)))
    
    (price_set [{:product_id (:product_new_product_id @app-state)
                 :price (if (= "" (get-in @articul_changes [:prices :moysklad :price]))
                          nil
                          (get-in @articul_changes [:prices :moysklad :price]))
                 :price_type_name "moysklad"}
                {:product_id (:product_new_product_id @app-state)
                 :price (if (= "" (get-in @articul_changes [:prices :discount_price :price]))
                          nil
                          (get-in @articul_changes [:prices :discount_price :price]))
                 :price_type_name "discount_price"}])
    
    (attribute_add (:product_new_product_id @app-state))
    

    ) 
  )





(defn product_add []
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "product-add")
      :method :post
      :params {}
      :handler product_add_handler
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))