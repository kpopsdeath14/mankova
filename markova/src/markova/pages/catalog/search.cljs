(ns markova.pages.catalog.search
  (:require
   ["antd" :as antd]
   ["swiper" :as Swiper]
   [markova.db :refer [app-state]]
   ["@ant-design/icons" :as icons]
   [reagent.core :as reagent :refer [as-element]]
   [markova.events.product-get :refer [product_get]]
   )
  )


(defn transform [m]
  (->> m
       (filter (fn [[_ v]] (not-empty v)))
       (mapv (fn [[k v]] {:attribute_name (name k) :attribute_values v}))))



(defn search []
  (let [web-app (.-WebApp js/Telegram)
        
        Input antd/Input
        filters (reagent/cursor app-state [:filters_picked])
        search_value (reagent/cursor app-state [:search_value])
        ]
    
    (reagent/create-class
     {:component-did-mount
      (fn [this]
        (.addEventListener js/window "scroll"
                           (fn []
                             (when-let [input (.getElementById js/document "catalog_search_input")]
                               (.blur input)))))
      
      :component-will-unmount
       (fn [this]
        (.removeEventListener js/window "scroll"
                              (fn []
                                (when-let [input (.getElementById js/document "catalog_search_input")]
                                  (.blur input)))))
      
      :reagent-render
      (fn []
        [:> Input {:defaultValue @search_value
                   :id "catalog_search_input"
                   :size "large"
                   :placeholder "Поиск"
                   :style {:font-family "'orchidea_light', sans-serif"
                           :border "1px solid #000000"
                           :border-radius 10
                           :height 62
                           :margin-bottom 20
                           :font-size 14}
                   :onPressEnter (fn []
                                   (.hideKeyboard web-app))
                   :onChange (fn [event]
                               (let [value (.-value (.-target event))]
                                 (swap! app-state assoc :search_value value)
                                 (product_get @search_value (transform @filters))))
      
                   :onFocus (fn []
                              (swap! app-state assoc :texting? true))
      
                   :onBlur (fn []
                             (swap! app-state assoc :texting? false))}])

      }
     
     ) 
    )
    )
