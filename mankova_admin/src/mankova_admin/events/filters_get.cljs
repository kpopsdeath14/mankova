(ns mankova-admin.events.filters-get
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]
            )
  )


(defn filters_get_handler [[ok? response]]
  (swap! app-state assoc :filters (vec (map (fn [product] (:data product)) response))) 
  )


(defn filters_get [filters]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "filters-get")
      :method :post
      :params {:filters filters}
      :handler filters_get_handler
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))