(ns markova.events.cart-get
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]
            )
  )


(defn cart_get_handler [[ok? response]] 
  (swap! app-state assoc :cart (vec (map (fn [product] (:data product)) response)))
  )


(defn cart_get []
  (let [
        user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData
        ]
    (ajax/ajax-request
     {:uri (api_uri_maker "cart_get")
      :method :post
      :params {:telegram_user_id id}
      :handler cart_get_handler 
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})}
     )
    )
  )