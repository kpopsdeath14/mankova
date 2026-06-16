(ns mankova-admin.events.articul-get
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]))


(defn articul_get_handler [[ok? response]] 
  (swap! app-state assoc :current_articul (first (vec (map (fn [product] (:data product)) response)))) 
  (swap! app-state assoc :articul_changes (:current_articul @app-state))
  )


(defn transform_filters [m]
  (->> m
       (filter (fn [[_ v]] (not-empty v)))
       (mapv (fn [[k v]] {:attribute_name (name k) :attribute_values v}))))



(defn articul_get [filters]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "product-get")
      :method :post
      :params {:filters (transform_filters filters)}
      :handler articul_get_handler
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))