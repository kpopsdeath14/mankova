(ns mankova-admin.events.articul-upd-images
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]
            [mankova-admin.events.articul-get :refer [articul_get]]))


(defn attribute_add_handler [[ok? response]] 
  (swap! app-state assoc-in [:articul_changes :images] (:images_edit @app-state))
  )




(defn images_upd [product_id images]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "product-attribute-add")
      :method :post
      :params {:attributes [
                            {:product_id product_id
                             :attribute_name "images"
                             :attribute_value images
                             }
                            ]
               }
      :handler attribute_add_handler
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))