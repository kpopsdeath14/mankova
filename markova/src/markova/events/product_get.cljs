(ns markova.events.product-get
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]
            )
  )


(defn product_get_handler [[ok? response]] 
  (swap! app-state assoc :products (vec (map (fn [product] (:data product)) response)))
  )


(defn product_get [search_string filters]
  (let [
        user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData
        ]
    (ajax/ajax-request
     {:uri (api_uri_maker "product_get")
      :method :post
      :params {:telegram_user_id id :search_string search_string :filters filters}
      :handler product_get_handler
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})
    )
  )