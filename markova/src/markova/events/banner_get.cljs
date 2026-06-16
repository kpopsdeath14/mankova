(ns markova.events.banner-get
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]))


(defn banner_get_handler [[ok? response]] 
  (swap! app-state assoc :banners (vec (map (fn [product] (:data product)) response)))
  )


(defn banner_get [filters]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "banner_get")
      :method :post
      :params (assoc filters :telegram_user_id id)
      :handler banner_get_handler
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))