(ns mankova-admin.events.vendore-code-get
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]))


(defn vendore_code_get_handler [[ok? response]]
  (swap! app-state assoc :products (vec (map (fn [product] (:data product)) response)))
  )

(defn transform_filters [m]
  (->> m
       (filter (fn [[_ v]] (not-empty v)))
       (mapv (fn [[k v]] {:attribute_name (name k) :attribute_values v}))))


(defn vendore_code_get [filters search_string]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "vendore-code-get")
      :method :post
      :params {:filters (transform_filters filters) :search_string search_string}
      :handler vendore_code_get_handler
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})
    )
  )