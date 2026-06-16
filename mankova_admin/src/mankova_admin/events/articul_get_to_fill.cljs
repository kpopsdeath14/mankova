(ns mankova-admin.events.articul-get-to-fill
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]))


(defn articul_get_to_fill_handler [[ok? response]]
  (swap! app-state
         (fn [state]
           (let [options-to-fill (->> (:options_to_fill state)
                                      (map keyword)
                                      set)
                 new-data (-> (first (map :data response))
                              (dissoc :id :vendor_code :actual)) 
                 filtered-data (select-keys new-data options-to-fill)]
             (update state :articul_changes
                     (fn [current-changes]
                       (merge current-changes filtered-data)))
             )
           )
         )
  
  
  (if (some #(= % "price") (:options_to_fill @app-state))
    (swap! app-state assoc-in [:articul_changes :prices :moysklad :price] (get-in (first (map :data response)) [:prices :moysklad :price]))
    )
  
  (if (some #(= % "discount_price") (:options_to_fill @app-state))
    (swap! app-state assoc-in [:articul_changes :prices :discount_price :price] (get-in (first (map :data response)) [:prices :discount_price :price])))
  )


(defn transform_filters [m]
  (->> m
       (filter (fn [[_ v]] (not-empty v)))
       (mapv (fn [[k v]] {:attribute_name (name k) :attribute_values v}))))



(defn articul_get_to_fill [filters] 
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "product-get")
      :method :post
      :params {:filters (transform_filters filters)}
      :handler articul_get_to_fill_handler
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))