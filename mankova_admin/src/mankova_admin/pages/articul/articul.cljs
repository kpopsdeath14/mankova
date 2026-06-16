(ns mankova-admin.pages.articul.articul 
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.pages.articul.options :refer [options]]
   [mankova-admin.pages.articul.images :refer [images]]
   [mankova-admin.pages.articul.parameters :refer [parameters]]
   [mankova-admin.events.product-attribute-add :refer [attribute_add]]
   [mankova-admin.pages.articul.modal-picture-edit :refer [modal_picture_edit]]
   [mankova-admin.events.price-set :refer [price_set]]
   [mankova-admin.pages.articul.modal-new-color :refer [modal_new_color]]
   [mankova-admin.events.archive-articul :refer [archive_articul]]
   [mankova-admin.events.product-add :refer [product_add]]
   )
  )


(defn two_column_grid [items]
  (let [Row antd/Row
        Col antd/Col 
        non-empty-items (remove #(or (nil? %) (and (string? %) (empty? %))) items)
        half (Math/ceil (/ (count non-empty-items) 2))
        [col1 col2] (split-at half non-empty-items)]
    [:> Row {:gutter 16}
     [:> Col {:span 12}
      (for [item col1
            :when item]
        [:div
         [:div {:style {:color "black"
                        :margin-bottom "16px"
                        :border-radius 15
                        :width "100%"
                        :font-size 24
                        :font-weight 300
                        :overflow "hidden"
                        :white-space "nowrap"
                        :height "auto"
                        :min-height 50
                        :align-items "center"
                        :justify-content "center"
                        :display "flex"
                        :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}}
          (:label item)]
         
         [:div {:key (:value item)
               :style {:id (:value item)
                       :color "black"
                       :margin-bottom "16px"
                       :border-radius "15px"
                       :width "100%"
                       :font-size "24px"
                       :font-weight 300
                       :min-height "50px"
                       :height "auto"
                       :display "flex"
                       :flex-wrap "wrap"
                       :align-items "center"
                       :justify-content "center"
                       :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                       :word-break "break-word"
                       :overflow-wrap "break-word"
                       :box-sizing "border-box"
                       :text-align "center"
                       :padding "8px"
                       :overflow "hidden"
                       :max-width "100%"}
               }
         (:value item)]
         ]
         )
         ]

     [:> Col {:span 12}
      (for [item col2
            :when item]
        
        [:div
         [:div {:style {:color "black"
                        :margin-bottom "16px"
                        :border-radius 15
                        :width "100%"
                        :font-size 24
                        :font-weight 300
                        :height "auto"
                        :overflow "hidden"
                        :white-space "nowrap"
                        :min-height 50
                        :align-items "center"
                        :justify-content "center"
                        :display "flex"
                        :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}}
          (:label item)]
         
         [:div {:key (:value item)
                :style {:id (:value item)
                        :color "black"
                        :margin-bottom "16px"
                        :border-radius "15px"
                        :width "100%"
                        :font-size "24px"
                        :font-weight 300
                        :min-height "50px"
                        :height "auto"
                        :display "flex"
                        :flex-wrap "wrap"
                        :align-items "center"
                        :justify-content "center"
                        :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                        :word-break "break-word"
                        :overflow-wrap "break-word"
                        :box-sizing "border-box"
                        :text-align "center"
                        :padding "8px"
                        :overflow "hidden"
                        :max-width "100%"}}
          (:value item)]]
        
        )
      ]
     ]
     )
     )




(defn two_column_grid_editing [items] 
  (let [Row antd/Row
        Col antd/Col
        Select antd/Select
        AutoComplete antd/AutoComplete
        half (Math/ceil (/ (count items) 2))
        [col1 col2] (split-at half items)
        articul-changes (reagent/cursor app-state [:articul_changes])
        ]

    (fn []
      (let [current-state @articul-changes]
        [:> Row {:gutter 16}
         [:> Col {:span 12}
          (for [item col1]
            ^{:key (:name item)}
            [:div
             [:div {:style {:color "black"
                            :margin-bottom "16px"
                            :border-radius 15
                            :width "100%"
                            :font-size 24
                            :font-weight 300
                            :height "auto"
                            :min-height 50
                            :align-items "center"
                            :overflow "hidden"
                            :white-space "nowrap"
                            :justify-content "center"
                            :display "flex"
                            :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}}
              (:name item)]
             
             
             (if (= "Категории" (:name item))
             
               [:> Select {:className "custom-select"
                           :style {:color "black"
                                   :margin-bottom "16px"
                                   :border-radius 15
                                   :width "100%"
                                   :height "auto"
                                   :min-height 50
                                   :border "5px solid #D3EAFF" 
                                   :font-weight 300
                                   :align-items "center"
                                   :justify-content "center"
                                   :overflow "hidden"
                                   :white-space "nowrap"
                                   :display "flex"
                                   
                                   :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}
                           :mode "multiple"
                           :value (get current-state :categories)
                           :onChange (fn [values]
                                       (when (not= values "add-new-color")
                                         (swap! app-state assoc-in
                                                [:articul_changes :categories]
                                                (js->clj values))))
                           :options (:options item)}]
             
             
             
               [:> AutoComplete {:className "custom-select"
                                 :style {:color "black"
                                         :margin-bottom "16px"
                                         :border-radius 15
                                         :width "100%" 
                                         :height "auto"
                                         :min-height 50
                                         :border "5px solid #D3EAFF"
                                         :font-weight 300
                                         :align-items "center"
                                         :justify-content "center"
                                         :overflow "hidden"
                                         :white-space "nowrap" 
                                         :display "flex"

                                         :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}
                                 :value (get current-state
                                             (keyword (case (:name item)
                                                        "Цвет"      "color"
                                                        "Размер"    "size"
                                                        "Тип"       "type"
                                                        "Коллекция" "collection"
                                                        "Тег"       "tags")))
                                 :onChange (fn [values]
                                             (when (not= values "add-new-color")
                                               (swap! app-state assoc-in
                                                      [:articul_changes
                                                       (keyword (case (:name item)
                                                                  "Цвет"      "color"
                                                                  "Размер"    "size"
                                                                  "Тип"       "type"
                                                                  "Коллекция" "collection"
                                                                  "Тег"       "tags"))]
                                                      (if (= "Тег" (:name item))
                                                        [(js->clj values)]
                                                        (js->clj values)))))
                                 :options (:options item)
                                 }
                                 ])


             



             
             ]
            )
            ]
         [:> Col {:span 12}
          (for [item col2]
            ^{:key (:name item)}
            [:div
             [:div {:style {:color "black"
                            :margin-bottom "16px"
                            :border-radius 15
                            :width "100%"
                            :font-size 24
                            :font-weight 300
                            :height "auto"
                            :min-height 50
                            :align-items "center"
                            :overflow "hidden"
                            :white-space "nowrap"
                            :justify-content "center"
                            :display "flex"
                            :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}}
              (:name item)]

             
             
             (if (= "Категории" (:name item))
             
               [:> Select {:className "custom-select"
                           :style {:color "black"
                                   :margin-bottom "16px"
                                   :width "100%"
                                   :height "auto"
                                   :border "5px solid #D3EAFF"
                                   :min-height 50
                                   :font-weight 300
                                   :align-items "center"
                                   :justify-content "center"
                                   :overflow "hidden"
                                   :white-space "nowrap"
                                   :display "flex"
                                   :border-radius 15
                                   :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}
                           :mode "multiple"
                           :value (get current-state :categories)
                           :onChange (fn [values]
                                       (when (not= values "add-new-color")
                                         (swap! app-state assoc-in
                                                [:articul_changes :categories]
                                                (js->clj values))))
                           :options (:options item)}]
             
             
             
               [:> AutoComplete {:className "custom-select"
                                 :style {:color "black"
                                         :margin-bottom "16px"
                                         :border-radius 15
                                         :width "100%"
                                         :border "5px solid #D3EAFF"
                                         :height "auto"
                                         :min-height 50
                                         :font-weight 300
                                         :overflow "hidden"
                                         :white-space "nowrap"
                                         :align-items "center"
                                         :justify-content "center"
                                         :display "flex"
                                         :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}
                                 :value (get current-state
                                             (keyword (case (:name item)
                                                        "Цвет"      "color"
                                                        "Размер"    "size"
                                                        "Тип"       "type"
                                                        "Коллекция" "collection"
                                                        "Тег"       "tags")))
                                 :onChange (fn [values]
                                             (when (not= values "add-new-color")
                                               (swap! app-state assoc-in
                                                      [:articul_changes
                                                       (keyword (case (:name item)
                                                                  "Цвет"      "color"
                                                                  "Размер"    "size"
                                                                  "Тип"       "type"
                                                                  "Коллекция" "collection"
                                                                  "Тег"       "tags"))]
                                                      (if (= "Тег" (:name item))
                                                        [(js->clj values)]
                                                        (js->clj values)))))
                                 :options (:options item)}
                                 ]
                                 )
                                            ]
                                            )
                         ]
                         ]
                         )
                         )
                         )
                         )






(defn articul_page [] 
  (let [Row antd/Row
        Col antd/Col
        Button antd/Button

        articul_editing? (reagent/cursor app-state [:articul_editing?])
        articul_changes (reagent/cursor app-state [:articul_changes])
        filters (reagent/cursor app-state [:filters])
        ]
    
    (fn []
      [:div
       [options]
       [modal_new_color]

       [:> Row {:gutter 16}
        [:> Col {:span 8}
         [images]
         [modal_picture_edit]
         (if @articul_editing?
           [two_column_grid_editing [{:name "Цвет"
                                      :options (let [type-data (first (filter #(= "color" (:attribute_name %)) @filters))]
                                                 (map (fn [v] {:label v, :value v})
                                                      (:attribute_values type-data)))}
                                     {:name "Размер"
                                      :options (let [type-data (first (filter #(= "size" (:attribute_name %)) @filters))]
                                                 (map (fn [v] {:label v, :value v})
                                                      (:attribute_values type-data)))}
                                     {:name "Тип"
                                      :options (let [type-data (first (filter #(= "type" (:attribute_name %)) @filters))]
                                                 (map (fn [v] {:label v, :value v})
                                                      (:attribute_values type-data)))}
                                     {:name "Коллекция"
                                      :options (let [type-data (first (filter #(= "collection" (:attribute_name %)) @filters))]
                                                 (map (fn [v] {:label v, :value v})
                                                      (:attribute_values type-data)))}
                                     
                                     {:name "Категории"
                                      :options (let [type-data (first (filter #(= "categories" (:attribute_name %)) @filters))]
                                                 (map (fn [v] {:label v, :value v})
                                                      (:attribute_values type-data)))}
                                     
                                     {:name "Тег"
                                      :options (let [type-data (first (filter #(= "tags" (:attribute_name %)) @filters))]
                                                 (map (fn [v] {:label v, :value v})
                                                      (:attribute_values type-data)))}
                                     ]]
           
           [two_column_grid [{:label "Цвет" :value (:color @articul_changes)}
                             {:label "Размер" :value (:size @articul_changes)}
                             {:label "Тип" :value (:type @articul_changes)} 
                             
                             {:label "Коллекция" :value (:collection @articul_changes)}
                             

                              {:label "Категории" :value (->> @articul_changes
                                                              :categories
                                                              (clojure.string/join ", "))}
                             
                             {:label "Тег" :value (->> @articul_changes
                                                       :tags
                                                       (clojure.string/join "\n")) }
                             ]
            ]
           )
         ]
        
        [:> Col {:span 16} 
         [parameters]
         (if @articul_editing? 
           
           [:> Row {:gutter 16
                     :style {:margin-bottom 15}
                    }
            [:> Col {:span 9 :offset 15}
             [:> Button {:style {:background "#D3EAFF"
                                 :color "black"
                                 :height 50
                                 :border-radius 15
                                 :width "100%"
                                 :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                                 :font-size 24
                                 :font-weight 300}
                         :disabled (or (nil? (:id @articul_changes)) (= (:id @articul_changes) "") (= "" (get-in @articul_changes [:prices :moysklad :price])) (nil? (get-in @articul_changes [:prices :moysklad :price])))
                         :onClick (fn [] 
                                    (if (:adding_new_article? @app-state)
                                      
                                      (product_add)
                                      
                                      (do
                                        (price_set [{:product_id (:product_id (:current_articul @app-state))
                                                     :price (if (= "" (get-in @articul_changes [:prices :moysklad :price]))
                                                              nil
                                                              (get-in @articul_changes [:prices :moysklad :price]))
                                                     :price_type_name "moysklad"}
                                                    {:product_id (:product_id (:current_articul @app-state))
                                                     :price (if (= "" (get-in @articul_changes [:prices :discount_price :price]))
                                                              nil
                                                              (get-in @articul_changes [:prices :discount_price :price]))
                                                     :price_type_name "discount_price"}])
                                        (attribute_add (:product_id (:current_articul @app-state)))
                                        ) 
                                      ) 
                                    )
                         }
              "Сохранить"]
             ]
            ]
           
           [:> Row {:gutter 16 :style {:margin-bottom 15}}
            [:> Col {:span 6}
             [:> Button {:style {:background "#D3EAFF"
                                 :color "black"
                                 :height 50
                                 :border-radius 15
                                 :width "100%"
                                 :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                                 :font-size 24
                                 :overflow "hidden"
                                 :white-space "nowrap"
                                 :font-weight 300}}
              "Весь образ"]]
           
            [:> Col {:span 3 :offset 9}
             [:> Button {:style {:color "black"
                                 :height 50
                                 :border-radius 15
                                 :width "100%"
                                 :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                                 :font-size 24
                                 :font-weight 300}
                         :icon (as-element [:> icons/EditOutlined])
                         :onClick (fn []
                                    (swap! app-state assoc :articul_editing? true))}]]
           
            [:> Col {:span 3}
             [:> Button {:style {:color "black"
                                 :height 50
                                 :border-radius 15
                                 :border "5px solid ##D3EAFF"
                                 :width "100%"
                                 :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                                 :font-size 24
                                 :font-weight 300}
                         :onClick (fn [] 
                                    (archive_articul (case (str (:actual (:articul_changes @app-state)))
                                                       "false" "t"
                                                       "true"  "f"
                                                       "t"
                                                       )
                                                     [(:vendor_code (:current_articul @app-state))]
                                                     [(:product_id (:current_articul @app-state))]) 
                                    )
                         :icon (case (str (:actual (:articul_changes @app-state)))
                                 "false" (as-element [:> icons/UploadOutlined])
                                 "true"  (as-element [:> icons/DownloadOutlined])
                                 (as-element [:> icons/DownloadOutlined])
                                 ) 
                         }
              ]
             ]
           
            [:> Col {:span 3}
             [:> Button {:style {:color "black"
                                 :height 50
                                 :border-radius 15
                                 :border "5px solid ##D3EAFF"
                                 :width "100%"
                                 :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                                 :font-size 24
                                 :font-weight 300}
                         :icon (as-element [:> icons/DeleteOutlined])}]]]
           )
         ]
        ] 
       ]
      ) 
    )
  )