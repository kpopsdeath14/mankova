(ns mankova-admin.events.product-del
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]
            [mankova-admin.events.product-get :refer [product_get]]
            )
  )


(defn product_del_handler [[ok? response]]
  (product_get (:filters_picked @app-state) (:current_vendor_code @app-state))
  )





(defn product_del [product_ids]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "product-del")
      :method :post
      :params {:product_ids product_ids}
      :handler product_del_handler
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))