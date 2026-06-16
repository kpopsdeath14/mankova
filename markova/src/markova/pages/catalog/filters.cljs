(ns markova.pages.catalog.filters
  (:require
   ["antd" :as antd]
   ["swiper" :as Swiper]
   [markova.db :refer [app-state]]
   ["@ant-design/icons" :as icons]
   [reagent.core :as reagent :refer [as-element]]
   [markova.events.filter-get :refer [filter_get]]
   [markova.events.product-get :refer [product_get]]
   )
  )


(defn transform [m]
  (->> m
       (filter (fn [[_ v]] (not-empty v)))
       (mapv (fn [[k v]] {:attribute_name (name k) :attribute_values v}))))


(defn handle-scroll []
  (let [select-inputs (array-seq (.querySelectorAll js/document ".ant-select-selection-search-input"))]
    (doseq [input select-inputs]
      (.blur input))))


(defn filters []
  (filter_get)
  (let [
        Select antd/Select
        Collapse antd/Collapse
        Cascader antd/Cascader

        filters (reagent/cursor app-state [:filters])
        filters_picked (reagent/cursor app-state [:filters_picked])
        search_value (reagent/cursor app-state [:search_value])
        ]
    (reagent/create-class
     {:component-did-mount
      (fn [this]
        (.addEventListener js/window "scroll" handle-scroll)
        )
     
      :component-will-unmount
      (fn []
        (.removeEventListener js/window "scroll" handle-scroll)
        )
     
      :reagent-render
      (fn []
        [:> Collapse {:accordion true
                      :style {:border-radius 10
                              :border "1px solid #000000"}
                      :items [{:showArrow false
                               :label (as-element [:div {:style {:display "flex"
                                                                 :align-items "center"
                                                                 :height 62
                                                                 :padding-left 15
                                                                 :font-family "'orchidea_light', sans-serif"}}
                                                   "Фильтры"])
                               :key "filters"
                               :children (as-element
                                          [:div {:style {:padding "15px"}}
                                           (for [filter @filters]
                                             [:> Select {:size "large"
                                                         :mode "multiple"
                                                         :showSearch false
                                                         :placeholder (:attribute_name_rus filter)
                                                         :suffixIcon nil
                                                         :style {:font-family "'orchidea_light', sans-serif"
                                                                 :min-height 42
                                                                 :width "100%"
                                                                 :border-radius 10
                                                                 :border "1px solid black"
                                                                 :margin-bottom 15
                                                                 
                                                                 }
                                                         :options (map (fn [v]
                                                                         {:label v, :value v})
                                                                       (:attribute_values filter))
                                                         :onChange (fn [values]
                                                                     (swap! app-state
                                                                            assoc-in
                                                                            [:filters_picked (keyword (:attribute_name filter))]
                                                                            (js->clj values))
                                                                     (product_get @search_value (transform @filters_picked))
                                                                     )
                                                         }
                                              ]
                                              )
                                                         ]
                                                         )
                                                         }
                                                         ]
                                                         }
                                                         ]
                                                         )
       }
      )
    )
  )