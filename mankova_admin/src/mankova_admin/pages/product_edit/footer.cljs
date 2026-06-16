(ns mankova-admin.pages.product-edit.footer
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.events.archive-product :refer [archive_product]]
   [mankova-admin.events.product-del :refer [product_del]]
   ))

(defn footer []
  (let [Button antd/Button
        Checkbox antd/Checkbox
        selected_products (reagent/cursor app-state [:selected_products_product_edit])
        products (reagent/cursor app-state [:products_edit]) 
        ]
    (fn []

      [:div {:style {:height 116
                     :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"
                     :border-top-left-radius 20
                     :border-top-right-radius 20
                     :padding "35px 30px"
                     :display "flex"
                     :justify-content "space-between"
                     :align-items "center"
                     :background "#fff"
                     :overflow "hidden"
                     :flex-wrap "nowrap"
                     :bottom 0
                     :position "fixed"
                     :left 0
                     :right 0
                     :margin "0 30px"
                     :z-index 1000}}
       [:div {:style {:font-size 24
                      :width 169
                      }}
        (str "Выбрано: " (count @selected_products))
        ]
       
       [:div {:style {:display "flex"
                      :gap 15}} 
        [:> Button {:icon (as-element [:> icons/TagOutlined {:style {:font-size "24px"}}])
                    :style {:border "3px solid #D3EAFF"
                            :height 45
                            :width 45
                            :border-radius 6}
                    :onClick (fn [] 
                               (swap! app-state assoc :product_edit_attribute_name "tags") 
                               (swap! app-state assoc :show_modal_product_editing? true) 
                               )
                    }
         ]
        [:> Button {:icon (as-element [:> icons/DollarOutlined {:style {:font-size "24px"}}])
                    :style {:border "3px solid #D3EAFF"
                            :height 45
                            :width 45
                            :border-radius 6}
                    :onClick (fn []
                               (swap! app-state assoc :product_edit_attribute_name "price")
                               (swap! app-state assoc :show_modal_product_editing? true) 
                               )
                    }
         ]
        
        
        [:> Button {:icon (as-element [:> icons/PercentageOutlined {:style {:font-size "24px"}}])
                    :style {:border "3px solid #D3EAFF"
                            :height 45
                            :width 45
                            :border-radius 6}
                    :onClick (fn []
                               (swap! app-state assoc :product_edit_attribute_name "price_discount")
                               (swap! app-state assoc :show_modal_product_editing? true) 
                               )
                    }
         ]
        ]
       





       [:div {:style {:display "flex"
                      :gap 15}}
        [:> Button {:icon (as-element [:> icons/DownloadOutlined {:style {:font-size "24px"}}])
                    :style {:border "3px solid #D3EAFF"
                            :height 45
                            :width 45
                            :border-radius 6}
                    :onClick (fn []
                               (archive_product "f"
                                                [(:current_vendor_code @app-state)]
                                                @selected_products))}] 
        [:> Button {:icon (as-element [:> icons/UploadOutlined {:style {:font-size "24px"}}])
                    :style {:border "3px solid #D3EAFF"
                            :height 45
                            :width 45
                            :border-radius 6}
                    :onClick (fn []
                               (archive_product "t"
                                                [(:current_vendor_code @app-state)]
                                                @selected_products))
                    }]
        [:> Button {:icon (as-element [:> icons/DeleteOutlined {:style {:font-size "24px"}}])
                    :style {:border "3px solid #D3EAFF"
                            :height 45
                            :width 45
                            :border-radius 6}
                    :onClick (fn []
                               (product_del @selected_products)
                               )
                    }]]


       [:> Button {
                   :style {:background "#D3EAFF"
                           :border "1px solid #00274C"
                           :color "black"
                           :height "100%"
                           :border-radius 15
                           :width "auto"
                           :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                           :font-size 24
                           :font-weight 300}
                   :onClick (fn []
                              (if (= (count @selected_products) (count @products))
                                (swap! app-state assoc :selected_products_product_edit [])
                                (swap! app-state assoc :selected_products_product_edit (mapv :product_id @products))
                                )
                              )
                   }
        (if (= (count @selected_products) (count @products))
          "Отменить выбор"
          "Выбрать все"
          )
        ]
       
       ]
       )
       )
       )