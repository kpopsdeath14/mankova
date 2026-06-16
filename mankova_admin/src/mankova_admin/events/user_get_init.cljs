(ns mankova-admin.events.user-get-init
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]] 
            [mankova-admin.events.vendore-code-get :refer [vendore_code_get]]
            [mankova-admin.events.filters-get :refer [filters_get]]
            ))


(defn user_get_init_handler [[ok? response]]
  (filters_get [])
  (vendore_code_get {:actual [["t" "true"]]}  "")
  (swap! app-state assoc :development (:user_get_init (first response)))
  (swap! app-state assoc :login? false) 
  )


(defn user_get_init [id]
  (let [
        init-data js/Telegram.WebApp.initData
        ]
    (ajax/ajax-request
     {:uri (api_uri_maker "user-get-init")
      :method :post
      :params {:telegram_user_id id}
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :handler user_get_init_handler
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))
