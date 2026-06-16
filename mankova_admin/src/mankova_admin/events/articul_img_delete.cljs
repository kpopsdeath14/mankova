(ns mankova-admin.events.articul-img-delete
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]
            [mankova-admin.events.vendore-code-get :refer [vendore_code_get]]))


(defn images_del_handler [[ok? response]] 
  
  )





(defn images_del [product_ids]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "articul-img-del")
      :method :post
      :params {:product_ids product_ids}
      :handler images_del_handler
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))