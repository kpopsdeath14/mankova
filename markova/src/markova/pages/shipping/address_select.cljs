(ns markova.pages.shipping.address-select
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [clojure.string :as string]
   )
  )






(defn address_select []
  (let [
        web-app (.-WebApp js/Telegram)

        AutoComplete antd/AutoComplete

        pvz_search_text (reagent/cursor app-state [:pvz_search_text])
        suggestions_pvz (reagent/cursor app-state [:suggestions_pvz])
        ]
    (fn []
      [:> AutoComplete
       {:value @pvz_search_text
        :class-name "custom-dropwown" 
        :autoCapitalize "none"
        :options (map (fn [pvz] {:label (:address (:address pvz)) :value (:address (:address pvz))})
                      (take 5
                            (if (empty? @pvz_search_text)
                              @suggestions_pvz
                              (filter #(clojure.string/includes?
                                        (clojure.string/lower-case (:name %))
                                        (clojure.string/lower-case @pvz_search_text))
                                      @suggestions_pvz))))
        :style {:font-family "'orchidea_light', sans-serif"
                :border-radius 10
                :border "1px solid #000000"
                :width "100%"
                :height 42
                :margin-bottom 20}
        :placeholder "Адресс ПВЗ"

        :onPressEnter (fn []
                        (.hideKeyboard web-app))
        
        :onFocus (fn []
                   (swap! app-state assoc :texting? true))
        
        :onBlur (fn []
                  (swap! app-state assoc :texting? false) 
                  ) 

        :onChange (fn [value]
                    (swap! app-state assoc :pvz_search_text value)
                    )
        :onSelect (fn [value]
                    (let [pvz_address value
                          pvz (first (filter (fn [pvz] (= (:address (:address pvz)) value)) @suggestions_pvz))
                          ]
                      (swap! app-state assoc :pvz_search_text pvz_address)
                      (swap! app-state assoc :shipping_pvz pvz_address) 
                      (swap! app-state assoc-in [:shipping_data :cdek_pvz_data] pvz)
                      (reset! suggestions_pvz [])
                      ) 
                    )
        
        }]
      )
    )
  )