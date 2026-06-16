(ns mankova-admin.pages.products.footer
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.events.archive-product :refer [archive_product]]
   [mankova-admin.events.vendor-code-del :refer [vendor_code_del]]
   )
  )

(defn footer []
  (let [
        Button antd/Button
        Checkbox antd/Checkbox

        products (reagent/cursor app-state [:products])
        selected_products_catalog (reagent/cursor app-state [:selected_products_catalog])
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
                     :z-index 1000
                     
                     }}
       [:div {:style {:font-size 24
                      :width 169
                      }}
        (str "Выбрано: " (count @selected_products_catalog))
        ]
      
       [:div {:style {:display "flex"
                      :gap 15}}
        [:> Button {:icon (as-element [:> icons/DownloadOutlined {:style {:font-size "24px"}}])
                    :style {:border "3px solid #D3EAFF"
                            :height 45
                            :width 45
                            :border-radius 6
                            }
                    :onClick (fn []
                               (archive_product "f"
                                                @selected_products_catalog
                                                []
                                                )
                               )
                    }]
        [:> Button {:icon (as-element [:> icons/UploadOutlined {:style {:font-size "24px"}}])
                    :style {:border "3px solid #D3EAFF"
                            :height 45
                            :width 45
                            :border-radius 6
                            }
                    :onClick (fn []
                               (archive_product "t"
                                                @selected_products_catalog
                                                []
                                                )
                               )
                    }]
        [:> Button {:icon (as-element [:> icons/DeleteOutlined {:style {:font-size "24px"}}])
                    :style {:border "3px solid #D3EAFF"
                            :height 45
                            :width 45
                            :border-radius 6
                            }
                    :onClick (fn []
                               (if (>(count @selected_products_catalog) 1)
                                 (js/alert "удаление более 1 вендор кода запрещено")
                                 (vendor_code_del (first @selected_products_catalog))
                                 )
                               )
                    }]
        ]
      
      
       [:> Button {:style {:background "#D3EAFF"
                           :color "black"
                           :height "100%"
                           :border-radius 15
                           :width "auto"
                           :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                           :font-size 24
                           :font-weight 300}
                   :onClick (fn []
                              (if (= (count @selected_products_catalog) (count @products))
                                (swap! app-state assoc :selected_products_catalog [])
                                (swap! app-state assoc :selected_products_catalog (mapv :vendor_code @products))) 
                              )
                   }
        (if (= (count @selected_products_catalog) (count @products))
          "Отменить выбор"
          "Выбрать все")]
       
       ]
      )
    )
  )