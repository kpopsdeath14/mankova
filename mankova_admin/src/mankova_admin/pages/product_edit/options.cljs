(ns mankova-admin.pages.product-edit.options
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.events.vendore-code-get :refer [vendore_code_get]]
   [clojure.string :as string]
   )
  )


(defn join [sep coll]
  (reduce (fn [acc s] (str acc (when (seq acc) sep) s)) "" coll))



(defn options []
  (let [
        Row antd/Row
        Col antd/Col
        Button antd/Button

        products_edit (reagent/cursor app-state [:products_edit])
        products_mode (reagent/cursor app-state [:products_mode])
        search_value (reagent/cursor app-state [:search_value]) 
        ]
    (fn []
      [:> Row {:gutter 16
               :style {:height "auto"
                       :min-height 80
                       :margin-bottom 25
                       :align-items "flex-end"
                       }}
       [:> Col {:span 2
                :style {:display "flex"
                        :flex-direction "column"
                        :height "100%"}}
        [:> Button {:style {:background "#D3EAFF"
                            :color "black"
                            :height 50
                            :border-radius 15
                            :width "100%"
                            :font-size 24
                            :font-weight 300
                            :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                            :margin-top "auto"
                            } 
                    :onClick (fn []
                               (swap! app-state assoc :selected_products_product_edit [])
                               (swap! app-state assoc-in [:filters_picked :tag] nil)
                               (swap! app-state assoc-in [:filters_picked :color] nil)
                               (swap! app-state assoc-in [:filters_picked :size] nil) 
                               (vendore_code_get (assoc (:filters_picked @app-state) :actual [(case @products_mode
                                                                                                "catalog" ["t" "true"]
                                                                                                "archive" ["f" "false"])]) @search_value)
                               (swap! app-state assoc :page :products)
                               (set! (.-href (.-location js/window)) (str "/#/products")
                                     )
                               )
                    :icon (as-element [:> icons/ArrowLeftOutlined])
                    }]
        ]
       
       [:> Col {:span 4
                :style {:display "flex"
                        :flex-direction "column"
                        :height "100%"}}
        [:div {:style {:font-size 16
                       :color "#777"
                       :padding 10
                       :padding-top 0
                       :padding-bottom 5}}
         "артикул"]
        [:> Button {:style {:background "#D3EAFF"
                            :color "#00274C"
                            :height 50
                            :border-radius 15
                            :overflow "hidden"
                            :white-space "nowrap"
                            :width "100%"
                            :font-size 24
                            :font-weight 300
                            :border "1px solid #00247C"
                            :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"
                            :margin-top "auto"

                            :display "flex"
                            :justify-content "space-between"
                            :align-items "center"
                            :padding "0 16px"
                            }
                    :icon (as-element [:> icons/EditOutlined])
                    :iconPosition "end"
                    :onClick (fn []
                               (swap! app-state assoc :product_edit_attribute_name "vendor_code")
                               (swap! app-state assoc-in [:current_vendor_changes :vendor_code] (:current_vendor_code @app-state)) 
                               (swap! app-state assoc :show_modal_product_editing? true))}
         (:current_vendor_code @app-state)]
        ]
       
       [:> Col {:span 6
                :style {:display "flex"
                        :flex-direction "column"
                        :height "100%"}}
        [:div {:style {:font-size 16
                       :color "#777"
                       :padding 10
                       :padding-top 0
                       :padding-bottom 5}}
         "тип товара"]
        [:> Button {:style {:background "#D3EAFF"
                            :color "#00274C"
                            :height 50
                            :border-radius 15
                            :overflow "hidden"
                            :white-space "nowrap"
                            :width "100%"
                            :font-size 24
                            :font-weight 300
                            :border "1px solid #00247C"
                            :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"
                            :margin-top "auto"
                            :display "flex"
                            :justify-content "space-between"
                            :align-items "center"
                            :padding "0 16px"
                            }
                    :icon (as-element [:> icons/EditOutlined])
                    :iconPosition "end"
                    :onClick (fn []
                               (swap! app-state assoc :product_edit_attribute_name "type")
                               (swap! app-state assoc-in [:current_vendor_changes :type] (:type (first @products_edit))) 
                               (swap! app-state assoc :show_modal_product_editing? true))}
         (if-not (nil? (:type (first @products_edit))) 
           (:type (first @products_edit))
           "Выбрать тип")]
        ]
       
       [:> Col {:span 6
                :style {:display "flex"
                        :flex-direction "column"
                        :height "100%"}}
        [:div {:style {:font-size 16
                       :color "#777"
                       :padding 10
                       :padding-top 0
                       :padding-bottom 5}}
         "категории"]
        [:> Button {:style {:background "#D3EAFF"
                            :color "#00274C"
                            :height "auto"
                            :min-height 50
                            :max-height 120
                            :border-radius 15
                            :width "100%"
                            :overflow "hidden"
                            :white-space "nowrap"
                            :font-size 24
                            :font-weight 300
                            :border "1px solid #00247C"
                            :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)" 
                            :word-wrap "break-word"
                            :text-align "start"
                            :padding "0px 16px"
                            :margin-top "auto"
                            :display "flex"
                            :justify-content "space-between"
                            :align-items "center" 
                            }
                    
                    :icon (as-element [:> icons/EditOutlined])
                    :iconPosition "end"

                    :onClick (fn []
                               (swap! app-state assoc :product_edit_attribute_name "categories")
                               (swap! app-state assoc-in [:current_vendor_changes :categories] (:categories (first @products_edit))) 
                               (swap! app-state assoc :show_modal_product_editing? true))}
         (if-not (nil? (:categories (first @products_edit)))
           (-> @products_edit
               first
               :categories
               (->> (interpose ", ")
                    (apply str)))
           "Выбрать категории")]
        ]
       
       [:> Col {:span 6
                :style {:display "flex"
                        :flex-direction "column"
                        :height "100%"}}
        [:div {:style {:font-size 16
                       :color "#777"
                       :padding 10
                       :padding-top 0
                       :padding-bottom 5}}
         "коллекция"]
        [:> Button {:style {:background "#D3EAFF"
                            :color "#00274C"
                            :height 50
                            :border-radius 15
                            :overflow "hidden"
                            :white-space "nowrap"
                            :width "100%"
                            :font-size 24
                            :font-weight 300
                            :border "1px solid #00247C"
                            :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"
                            :margin-top "auto"
                            :display "flex"
                            :justify-content "space-between"
                            :align-items "center"
                            :padding "0 16px"
                            }
                    :icon (as-element [:> icons/EditOutlined])
                    :iconPosition "end"
                    :onClick (fn []
                               (swap! app-state assoc :product_edit_attribute_name "collection")
                               (swap! app-state assoc-in [:current_vendor_changes :collection] (:collection (first @products_edit))) 
                               (swap! app-state assoc :show_modal_product_editing? true))
                    }
         (if-not (nil? (:collection (first @products_edit))) 
           (:collection (first @products_edit))
           "Выберите коллекцию")
         ]
        ]
       ])))