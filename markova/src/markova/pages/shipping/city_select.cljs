(ns markova.pages.shipping.city-select
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [clojure.string :as string]
   [markova.events.cdek-search-city :refer [cdek_search_city]]
   [markova.events.cdek-calculate-shipping :refer [cdek_calulate_shipping]]
   [markova.events.cdek-search-pvz :refer [cdek_search_pvz]]
   )
  )




(defn city_select []
  (let [
        web-app (.-WebApp js/Telegram)
        AutoComplete antd/AutoComplete
        
        city_search_text (reagent/cursor app-state [:city_search_text])
        suggestions_city (reagent/cursor app-state [:suggestions_city])
        ]
    (fn []
      [:> AutoComplete
       {:value @city_search_text
        :class-name "custom-dropwown"
        :autoCapitalize "none"
        :options (map (fn [city] {:label (:name city) :value (:name city)}) @suggestions_city)
        :style {:font-family "'orchidea_light', sans-serif"
                :border-radius 10
                :border "1px solid #000000"
                :width "100%"
                :height 42
                :margin-bottom 20}
        
        :onPressEnter (fn []
                        (.hideKeyboard web-app))
        
        :onFocus (fn []
                   (swap! app-state assoc :texting? true)
                   )
        
        :onBlur (fn []
                  (swap! app-state assoc :texting? false)
                  )
        
        :placeholder "Ваш Город"
        
        :onChange (fn [value]
                    (swap! app-state assoc :pvz_search_text nil)
                    (swap! app-state assoc :shipping_pvz nil)
                    (swap! app-state assoc :pvz_cdek_code nil)
                    
                    (swap! app-state assoc :city_search_text value)
                    (if (< 2 (count value))
                      (do
                        (cdek_search_city)
                        )
                      (swap! app-state assoc :suggestions_city [])))
        :onSelect (fn [value options] 
                    (let [city-name value
                          city (first (filter (fn [city] (= (:name city) value)) @suggestions_city)) 
                          city-code (:code city)
                          ] 
                      (swap! app-state assoc :city_search_text city-name)
                      (swap! app-state assoc-in [:shipping_data :city_cdek] (first (filter (fn [city] (= (:name city) value)) @suggestions_city))) 
                      (swap! app-state assoc :city_cdek_code city-code)
                      (swap! app-state assoc :suggestions_city [])
                      (cdek_search_pvz)
                      (cdek_calulate_shipping city-code) 
                      )
                    )
        }
        ]
        )
    )
  )