(ns markova.events.user-attribute-get
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]
            )
  )


(defn user_attribute_get_handler [[ok? response]]
  (swap! app-state assoc :user_data (vec (map (fn [product] (:data product)) response))) 
  )


(defn user_attribute_get []
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        init-data js/Telegram.WebApp.initData
        id (.-id user)]
    (ajax/ajax-request
     {:uri (api_uri_maker "user_attribute_get")
      :method :post
      :params {:telegram_user_id id}
      :handler user_attribute_get_handler 
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))