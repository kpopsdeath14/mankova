(ns mankova-admin.pages.articul.modal-fill-analogy
  (:require
   ["antd" :as antd]
   [mankova-admin.db :refer [app-state]]
   ["@ant-design/icons" :as icons]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.events.articul-get :refer [articul_get]]
   [mankova-admin.events.articul-get-to-fill :refer [articul_get_to_fill]]
   ))

(defn modal_fill_analogy []
  (let [Modal antd/Modal
        AutoComplete antd/AutoComplete
        TreeSelect antd/TreeSelect
        Select antd/Select
        Button antd/Button

        visible? (reagent/cursor app-state [:show_modal_fill_analogy?])
        filters (reagent/cursor app-state [:filters])
        options_to_fill (reagent/cursor app-state [:options_to_fill])
        
        TreeData [{:title "описание товара"
                   :value :product_description}
                  {:title "наименование"
                   :value :name}
                  {:title "состав"
                   :value :made_of}
                  {:title "уход за изделем"
                   :value :product_care_info}
                  {:title "параметры модели"
                   :value :model_on_picture_parameters}
                 
                  {:title "обмеры изделия"
                   :value :product_measurements}
                 
                  {:title "цвет"
                   :value :color}
                 
                  {:title "размер"
                   :value :size}
                 
                  {:title "тип"
                   :value :type}
                 
                  {:title "коллекция"
                   :value :collection}
                 
                  {:title "категории"
                   :value :categories}
                 
                  {:title "весь образ"
                   :value :12345}
                 
                 
                  {:title "цена"
                   :value :price}
                 
                  {:title "цена по скидке"
                   :value :discount_price}]
        ]
    (fn []
      [:> Modal
       {:visible @visible?
        :closable true
        :width "80vw"
        :style {:top "35%"
                :maxWidth "800px"}
        :onCancel #(swap! app-state assoc :show_modal_fill_analogy? false)

        :footer (fn [_ {:keys [OkBtn CancelBtn]}]
                  (as-element
                   (let []
                     [:> Button
                      {:style {:background "#D3EAFF"
                               :color "black"
                               :height 50
                               :border-radius 15
                               :width "auto"
                               :font-size 24
                               :font-weight 300
                               :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"} 
                       :onClick (fn [] 
                                  (articul_get_to_fill {:id [(:id_product_to_fill @app-state)]}) 
                                  (swap! app-state assoc :show_modal_fill_analogy? false)
                                  )
                       }
                      "Заполнить"])))}

       [:div {:style {:width "100%"
                      :padding 16
                      :boxSizing "border-box"}}

        [:> AutoComplete  {:style {:width "100%"
                                   :height 50
                                   :border "1px solid #00247C"
                                   :border-radius 6
                                   :margin-bottom 12}
                           :placeholder "Введите код товара"
                           :options (let [type-data (first (filter #(= "id" (:attribute_name %)) @filters))]
                                      (map (fn [v] {:label v, :value v})
                                           (:attribute_values type-data)))
                           :onChange (fn [value]
                                       (swap! app-state assoc :id_product_to_fill value) 
                                       )
                           }
         ]
        
        [:> TreeSelect {:treeData (if (:adding_new_article? @app-state)
                                    TreeData
                                    (conj TreeData {:title "изображения"
                                                    :value :images})
                                    ) 
                        :placeholder "Выберите поля для заполнения"
                        :treeCheckable true
                        :values @options_to_fill
                        :onChange (fn [values]
                                    (swap! app-state assoc :options_to_fill (js->clj values))
                                    )
                        :style {:width "100%"
                                :height "100%" 
                                :align-items "center"
                                :min-height 50
                                :border "1px solid #00247C"
                                :border-radius 6
                                :margin-bottom 12}
                        }
         ]

       
        ]
        ]
        )
        )
        )
