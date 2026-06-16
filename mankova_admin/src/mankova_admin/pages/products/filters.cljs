(ns mankova-admin.pages.products.filters
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.events.vendore-code-get :refer [vendore_code_get]]
   )
  )



(defn transform_filters [m]
  (->> m
       (filter (fn [[_ v]] (not-empty v)))
       (mapv (fn [[k v]] {:attribute_name (name k) :attribute_values v}))))



(defn filters []
  (let [
        Select antd/Select

        filters (reagent/cursor app-state [:filters])
        filters_picked (reagent/cursor app-state [:filters_picked])
        search_value (reagent/cursor app-state [:search_value])
        ]
    (fn []
      [:div {:style {:background "#D3EAFF"
                     :border-radius 15
                     :height 50
                     :overflow "hidden"
                     :flex-wrap "nowrap"
                     :font-size 24
                     :display "flex"
                     :justify-content "space-around"
                     :align-items "center"
                     :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"
                     }
             }
       [:> Select {:style {:background "transparent"
                           :border "none"
                           :box-shadow "none"
                           :min-width "200px"
                           }
                   :dropdownStyle {:background "#D3EAFF"}
                   :placeholder "Тип"
                   :bordered false
                   :value (:type @filters_picked)
                   :mode "multiple"
                   :allowClear true
                   :options (let [
                                  type-data (first (filter #(= "type" (:attribute_name %)) @filters))
                                  ]
                              (map (fn [v] {:label v, :value v})
                                   (:attribute_values type-data)
                                   )
                              ) 
                   :onChange (fn [values]
                               (swap! app-state
                                      assoc-in
                                      [:filters_picked :type]
                                      (js->clj values)
                                      )
                               
                               (vendore_code_get (assoc @filters_picked :actual [(case (:products_mode @app-state)
                                                                                   "catalog" ["t" "true"]
                                                                                   "archive" ["f" "false"])]) @search_value)
                               )
                   } 
        ]
       
       [:> Select {:style {:background "transparent"
                           :border "none"
                           :box-shadow "none"
                           :min-width "200px"}
                   :dropdownStyle {:background "#D3EAFF"}
                   :placeholder "Категории"
                   :bordered false
                   :mode "multiple"
                   :value (:categories @filters_picked)
                   :allowClear true
                   :options (let [type-data (first (filter #(= "categories" (:attribute_name %)) @filters))]
                              (map (fn [v] {:label v, :value v})
                                   (:attribute_values type-data)))
                   :onChange (fn [values]
                               (swap! app-state
                                      assoc-in
                                      [:filters_picked :categories]
                                      (js->clj values))
       
                               (vendore_code_get (assoc @filters_picked :actual [(case (:products_mode @app-state)
                                                                                   "catalog" ["t" "true"]
                                                                                   "archive" ["f" "false"])]) @search_value))}]
       
       [:> Select {:style {:background "transparent"
                           :border "none"
                           :box-shadow "none"
                           :min-width "200px"}
                   :dropdownStyle {:background "#D3EAFF"}
                   :placeholder "Коллекция"
                   :bordered false
                   :value (:collection @filters_picked)
                   :mode "multiple"
                   :allowClear true
                   :options (let [type-data (first (filter #(= "collection" (:attribute_name %)) @filters))]
                              (map (fn [v] {:label v, :value v})
                                   (:attribute_values type-data)))
                   :onChange (fn [values]
                               (swap! app-state
                                      assoc-in
                                      [:filters_picked :collection]
                                      (js->clj values))
       
                               (vendore_code_get (assoc @filters_picked :actual [(case (:products_mode @app-state)
                                                                                   "catalog" ["t" "true"]
                                                                                   "archive" ["f" "false"])]) @search_value))}]
       
       [:> Select {:style {:background "transparent"
                           :border "none"
                           :box-shadow "none"
                           }
                   :dropdownStyle {:background "#D3EAFF"}
                   :placeholder "Сортировка"
                   :bordered false
                   :allowClear true
                   }
        ]
       ]
      )
    )
  )