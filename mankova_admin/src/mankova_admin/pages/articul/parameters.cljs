(ns mankova-admin.pages.articul.parameters
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.events.vendor-attribute-add :refer [vendor_attribute_add]]
   [mankova-admin.pages.articul.modal-fill-analogy :refer [modal_fill_analogy]]
   )
  )


(defn parameter_render [option]
  (let [expanded? (reagent/atom false)]
    (fn [new-option]
      (let [option (or new-option option)]
        [:div {:style {:display "flex"
                       :width "100%"
                       :justify-content "space-between"
                       :overflow "hidden"}}

         [:div {:style {:display "flex"
                        :flex-direction "column"
                        :flex-grow 1
                        :min-width 0
                        :height (when-not @expanded? "auto")}}
          [:div {:style {:font-size 24
                         :font-weight 700
                         :white-space (if @expanded? "normal" "nowrap")
                         :overflow "hidden"
                         :text-overflow (if @expanded? "clip" "ellipsis")
                         :min-height "30px"
                         }
                 }
           (:name option)]

          [:div {:style {:font-size 24
                         :font-weight 300
                         :white-space "pre-wrap"
                         :overflow "hidden" 
                         :text-overflow (if @expanded? "clip" "ellipsis")
                         :display "-webkit-box"
                         :-webkit-box-orient "vertical"
                         :-webkit-line-clamp (if @expanded? "unset" 2) 
                         }}
           (:value option)
           ]
          ]

         [:div {:style {:font-size 24
                        :font-weight 300
                        :color "#7D7D7D"
                        :cursor "pointer"
                        :flex-shrink 0
                        } 
                :on-click #(swap! expanded? not)}
          (if @expanded? "свернуть" "развернуть")]]))))


(defn parameter_render_edit [option]
  (let [
        Button antd/Button
        Input antd/Input
        TextArea (.-TextArea Input)

        articul_changes (reagent/cursor app-state [:articul_changes])
        ]
    
    [:div
     (if (= "Описание товара" (:name option))
       [:div {:style {:display "flex"
                      :justify-content "space-between"
                      }
              }
        
        [:div {:style {:color "black"
                       :margin-bottom "16px"
                       :border-radius "15px"
                       :font-size "24px"
                       :font-weight 300
                       :padding "10px 30px"
                       :display "inline-flex"
                       :align-items "center"
                       :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                       :height "50px"}}
         (:name option)]
        [:> Button {:style {:color "black"
                            :border-radius 15
                            :font-size 24
                            :font-weight 300
                            :height 50
                            :overflow "hidden"
                            :white-space "nowrap"
                            :border "none"
                            :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                            :align-items "center"
                            :justify-content "center"
                            :background "#D3EAFF"
                            :display "flex" 
                            :margin-bottom 16}}
         "Весь образ"]
        ]
       
       [:div {:style {:color "black"
                      :margin-bottom "16px"
                      :border-radius "15px"
                      :font-size "24px"
                      :font-weight 300
                      :padding "10px 30px"
                      :display "inline-flex"
                      :align-items "center"
                      :justify-content "center"
                      :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)" 
                      :height "50px"}}
        (:name option)]
       )

     [:div {:style {:position "relative"
                    :width "100%"}}
      [:> TextArea {:rows 3
                    :placeholder "Введите текст"
                    :style {:border "5px solid #D3EAFF"
                            :font-size 24
                            :font-weight 300
                            :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                            :width "100%"
                            :border-radius 15
                            :padding-right "80px"}
                    :defaultValue (:value option)
                    :onChange (fn [event] 
                                (let [value (.-value (.-target event))]
                                  (swap! app-state assoc-in [:articul_changes
                                                             (keyword (case (:name option)
                                                                        "Описание товара"      "product_description"
                                                                        "Состав"               "made_of"
                                                                        "Уход за изделем"      "product_care_info"
                                                                        "Параметры модели"     "model_on_picture_parameters"
                                                                        "Обмеры изделия"       "product_measurements"))]
                                         value)
                                  ) 
                                )
                    }]
      [:> Button {:style {:position "absolute"
                          :right 20
                          :bottom 20
                          :height 34
                          :border-radius 17
                          :border "1px solid #00247C"}
                  :onClick (fn []
                             (swap! app-state assoc :current_vendor_code (:vendor_code @articul_changes))
                             (vendor_attribute_add (case (:name option)
                                                     "Описание товара"      "product_description"
                                                     "Состав"               "made_of"
                                                     "Уход за изделем"      "product_care_info"
                                                     "Параметры модели"     "model_on_picture_parameters"
                                                     "Обмеры изделия"       "product_measurements") 
                                                   
                                                   ((keyword (case (:name option)
                                                               "Описание товара"      "product_description"
                                                               "Состав"               "made_of"
                                                               "Уход за изделем"      "product_care_info"
                                                               "Параметры модели"     "model_on_picture_parameters"
                                                               "Обмеры изделия"       "product_measurements"))
                                                    @articul_changes)
                                                   )
                             )
                  }
      
       "Сохранить для всех"]
      
      ]
     ]
    ) 
  )




(defn parameters []
  (let [
        Input antd/Input
        Button antd/Button

        articul_editing? (reagent/cursor app-state [:articul_editing?])
        articul_changes (reagent/cursor app-state [:articul_changes])
        ]
    (fn []
      (when @articul_changes
        (if @articul_editing?
          [:div {:style {:margin-bottom 16}}
           [:> Input {:style {:color "black"
                              :border-radius 15
                              :width "100%"
                              :font-size 24
                              :height 50
                              :align-items "center"
                              :justify-content "center"
                              :border "1px solid #00247C"
                              :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"
                              :background "#D3EAFF"
                              :display "flex"
                              :margin-bottom 16} 
                      :value (:name @articul_changes)
                      :onChange (fn [event] 
                                  (let [value (.-value (.-target event))]
                                    (swap! app-state assoc-in [:articul_changes :name] value)
                                    )
                                  )
                      }
            
            ]
           [modal_fill_analogy]
           [:> Button {:style {:background "white"
                               :border "5px solid #D3EAFF"
                               :color "black"
                               :height 50
                               :margin-bottom 16
                               :border-radius 15
                               :width "100%"
                               :overflow "hidden"
                               :white-space "nowrap"
                               :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                               :font-size 24
                               :font-weight 300}
                       :onClick (fn []
                                  (swap! app-state assoc :show_modal_fill_analogy? true))}
            "Заполнить по аналогии"]
           
           [:div {:style {:width "100%"
                          :display "flex"
                          :justify-content "space-between"
                          :flex-direction "column"
                          :gap 30}}
            (for [option [{:name "Описание товара" :value (:product_description @articul_changes)}
                          {:name "Состав" :value (:made_of @articul_changes)}
                          {:name "Уход за изделем" :value (:product_care_info @articul_changes)}
                          {:name "Параметры модели" :value (:model_on_picture_parameters @articul_changes)}
                          {:name "Обмеры изделия" :value (:product_measurements @articul_changes)}]]
              [parameter_render_edit option])]]
          
          
          [:div {:style {:margin-bottom 16}}
           [:div {:style {:color "black"
                          :border-radius 15
                          :width "100%"
                          :font-size 24
                          :font-weight 700
                          :height 50
                          :align-items "center"
                          :justify-content "center"
                          :display "flex"
                          :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                          :margin-bottom 16}}
            (:name @articul_changes)]
           
           [:div {:style {:padding "10px 20px"
                          :color "black"
                          :border-radius 15
                          :width "100%"
                          :height "auto"
                          :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                          :display "flex"
                          :justify-content "space-between"
                          :flex-direction "column"
                          :gap 30}}
            (for [option [{:name "Описание товара" :value (or (:product_description @articul_changes) "Нет данных")}
                          {:name "Состав" :value (or (:made_of @articul_changes) "Нет данных")}
                          {:name "Уход за изделем" :value (or (:product_care_info @articul_changes) "Нет данных")}
                          {:name "Параметры модели" :value (or (:model_on_picture_parameters @articul_changes) "Нет данных")}
                          {:name "Обмеры изделия" :value (or (:product_measurements @articul_changes) "Нет данных")}]]
              [parameter_render option]
              )
            ]
           ]
          
          )
          )
          )
          )
          )