(ns mankova-admin.pages.articul.options
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.pages.articul.modal-fill-analogy :refer [modal_fill_analogy]]
   [mankova-admin.events.product-get :refer [product_get]]
   ))




(defn options []
  (let [Row antd/Row
        Col antd/Col
        Input antd/Input
        Button antd/Button

        articul_editing? (reagent/cursor app-state [:articul_editing?])
        articul_changes (reagent/cursor app-state [:articul_changes]) 
        ]
    (fn []
      (if @articul_editing?
        [:> Row {:gutter 16
                 :style {:height 90
                         :margin-bottom 25
                         }
                 
                 } 
         [:> Col {:span 2 
                  :style {:display "flex" 
                          :align-items "flex-end"
                          :height "100%"} 
                  } 

          [:> Button {:style {:background "#D3EAFF"
                              :color "black"
                              :height 50
                              :border-radius 15
                              :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                              :width "100%"
                              :font-size 24
                              :font-weight 300}
                      :onClick (fn [] 
                                 (product_get (assoc {}
                                                     :vendor_code [(:vendor_code (:articul_changes @app-state))]
                                                     :actual (case (:products_mode @app-state)
                                                               "catalog" ["t" "true"]
                                                               "archive" ["f" "false"])) "")
                                 (swap! app-state assoc :articul_changes {})
                                 (swap! app-state assoc :page :product_edit) 
                                 (swap! app-state assoc :articul_editing? false)
                                 (swap! app-state assoc :adding_new_article? false) 
                                 (swap! app-state assoc :adding_new_product? false) 
                                 )
                      :icon (as-element [:> icons/ArrowLeftOutlined])}]]
         
         [:> Col {:span 4
                  :style {:display "flex"
                          :flex-direction "column"
                          :justify-content "flex-end"
                          :height "100%"}
                  }
          [:div {:style {:padding-left 10
                         :padding-bottom 10
                         :font-size 20
                         :color "#777"
                         :overflow "hidden"
                         :white-space "nowrap"
                         }
                 }
           "артикул"
           ]
          
          [:> Button {:style {:color "black"
                              :height 50
                              :border-radius 15
                              :width "100%"
                              :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                              :font-size 24
                              :font-weight 700}}
           (:vendor_code (:current_articul @app-state))
           ]
          ]
         
         [:> Col {:span 4
                  :style {:display "flex"
                          :flex-direction "column"
                          :justify-content "flex-end"
                          :height "100%"}
                  }
          [:div {:style {:padding-left 10
                         :padding-bottom 10
                         :font-size 20
                         :overflow "hidden"
                         :white-space "nowrap"
                         :color "#777"}}
           
           "код товара"]
          [:> Input {:style {:background "#D3EAFF"
                             :border "1px solid #00274C"
                             :color "black"
                             :height 50
                             :border-radius 15
                             :width "100%"
                             :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                             :font-size 24
                             :font-weight 300}
                     :defaultValue  (:id @articul_changes)
                     :onChange (fn [event]
                                 (let [value (.-value (.-target event))]
                                   (swap! app-state assoc-in [:articul_changes :id] value)))
                     }
           ]]
         
         [:> Col {:span 6
                  :style {:display "flex"
                          :flex-direction "column"
                          :justify-content "flex-end"
                          :height "100%"}}
          [:div {:style {:padding-left 10
                         :padding-bottom 10
                         :font-size 20
                         :overflow "hidden"
                         :white-space "nowrap"
                         :color "#777"}}
         
           "Tilda External ID"]
          [:> Input {:style {:background "#D3EAFF"
                             :border "1px solid #00274C"
                             :color "black"
                             :height 50
                             :border-radius 15
                             :width "100%"
                             :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                             :font-size 24
                             :font-weight 300}
                     :defaultValue  (:tilda_external_id @articul_changes)
                     :onChange (fn [event]
                                 (let [value (.-value (.-target event))]
                                   (swap! app-state assoc-in [:articul_changes :tilda_external_id] value)))}]]
         
         [:> Col {:span 4
                  :style {:display "flex"
                          :flex-direction "column"
                          :justify-content "flex-end"
                          :height "100%"}
                  }
          [:div {:style {:padding-left 10
                         :padding-bottom 10
                         :font-size 20
                         :overflow "hidden"
                         :white-space "nowrap"
                         :color "#777"}}
           "цена"]
          [:> Input {:style {:background "#D3EAFF"
                             :border "1px solid #00274C"
                             :color "black"
                             :height 50
                             :border-radius 15
                             :width "100%"
                             :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                             :font-size 24
                             :font-weight 300}
                     :defaultValue  (get-in @articul_changes [:prices :moysklad :price])
                     :onChange (fn [event]
                                 (let [value (.-value (.-target event))]
                                   (swap! app-state assoc-in [:articul_changes :prices :moysklad :price] value) 
                                   )
                                 
                                 
                                 ) 
                     }
           ]
          ]
         
         [:> Col {:span 4
                  :style {:display "flex"
                          :flex-direction "column"
                          :justify-content "flex-end"
                          :height "100%"}
                  }
          [:div {:style {:padding-left 10
                         :padding-bottom 10
                         :font-size 20
                         :overflow "hidden"
                         :white-space "nowrap"
                         :maxHeight 34
                         :color "#777"}}
           "по скидке"]
          [:> Input {:style {:background "#D3EAFF"
                             :border "1px solid #00274C"
                             :color "black"
                             :height 50
                             :border-radius 15
                             :width "100%"
                             :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                             :font-size 24
                             :font-weight 300}
                     :defaultValue  (get-in @articul_changes [:prices :discount_price :price])
                     :onChange (fn [event]
                                 (let [value (.-value (.-target event))]
                                   (swap! app-state assoc-in [:articul_changes :prices :discount_price :price] value)
                                   )
                                 )
                     }
           ]] 
         ]
        




        [:> Row {:gutter 16
                 :style {:height 90
                         :margin-bottom 25}}
         [:> Col {:span 2
                  :style {:display "flex"
                          :flex-direction "column"
                          :justify-content "flex-end"
                          :height "100%"}
                  }
          [:> Button {:style {:background "#D3EAFF"
                              :color "black"
                              :height "50px"
                              :border-radius 15
                              :width "100%"
                              :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                              :font-size 24
                              :font-weight 300}
                      :onClick (fn []
                                 (swap! app-state assoc :page :product_edit) 
                                 (swap! app-state assoc :articul_editing? false)
                                 (swap! app-state assoc :articul_changes {})
                                 (set! (.-href (.-location js/window)) (str "/#/product-edit/" (js/encodeURIComponent (:vendor_code (:current_articul @app-state)))))
                                 )
                      :icon (as-element [:> icons/ArrowLeftOutlined])}]]
        
         [:> Col {:span 4
                  :style {:display "flex"
                          :flex-direction "column"
                          :justify-content "flex-end"
                          :height "100%"}
                  }
          [:div {:style {:padding-left 10
                         :padding-bottom 10
                         :font-size 20
                         :overflow "hidden"
                         :white-space "nowrap"
                         :color "#777"}}
           "артикул"]
          [:div {:style {:background "white"
                         :color "black"
                         :height "50px"
                         :overflow "hidden"
                         :text-overflow "ellipsis"
                         :border-radius 15
                         :width "100%"
                         :white-space "nowrap"
                         :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                         :font-size 24
                         :align-content "center"
                         :padding "0px 20px"
                         :text-align "center"
                         :font-weight 700}}
           (:vendor_code (:current_articul @app-state))]]
        
         [:> Col {:span 4
                  :style {:display "flex"
                          :flex-direction "column"
                          :justify-content "flex-end"
                          :height "100%"}
                  }
          [:div {:style {:padding-left 10
                         :padding-bottom 10
                         :overflow "hidden"
                         :white-space "nowrap"
                         :font-size 20
                         :color "#777"}}
           "код товара"]
          [:div {:style {:background "white"
                         :color "black"
                         :height "50px"
                         :overflow "hidden"
                         :text-overflow "ellipsis"
                         :border-radius 15
                         :width "100%"
                         :white-space "nowrap"
                         :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                         :font-size 24
                         :align-content "center"
                         :padding "0px 20px"
                         :text-align "center"
                         :font-weight 700}}
           (:id @articul_changes)
           ]
          ]
         
         [:> Col {:span 6
                  :style {:display "flex"
                          :flex-direction "column"
                          :justify-content "flex-end"
                          :height "100%"}}
          [:div {:style {:padding-left 10
                         :padding-bottom 10
                         :overflow "hidden"
                         :white-space "nowrap"
                         :font-size 20
                         :text-align "center"
                         :color "#777"}}
           "Tilda External ID"]
          [:div {:style {:background "white"
                         :color "black"
                         :height "50px"
                         :overflow "hidden"
                         :text-overflow "ellipsis"
                         :border-radius 15
                         :white-space "nowrap"
                         :width "100%"
                         :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                         :font-size 24
                         :align-content "center"
                         :padding "0px 20px"
                         :text-align "center"
                         :font-weight 700}}
           (:tilda_external_id @articul_changes)]]
        
         [:> Col {:span 4
                  :style {:display "flex"
                          :flex-direction "column"
                          :justify-content "flex-end"
                          :height "100%"}
                  }
          [:div {:style {:padding-left 10
                         :padding-bottom 10
                         :font-size 20
                         :overflow "hidden"
                         :white-space "nowrap"
                         :color "#777"}}
           "цена"]
          [:div {:style {:background "white"
                         :color "black"
                         :height "50px"
                         :overflow "hidden"
                         :text-overflow "ellipsis"
                         :white-space "nowrap"
                         :border-radius 15
                         :width "100%"
                         :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                         :font-size 24
                         :align-content "center"
                         :padding "0px 20px"
                         :text-align "center"
                         :font-weight 300}}
           (str (get-in @articul_changes [:prices :moysklad :price]) " руб")
           ]
          ]
        
         [:> Col {:span 4
                  :style {:display "flex"
                          :flex-direction "column"
                          :justify-content "flex-end"
                          :height "100%"}
                  }
          [:div {:style {:padding-left 10
                         :padding-bottom 10
                         :font-size 20
                         :overflow "hidden"
                         :white-space "nowrap"
                         :color "#777"}}
           "по скидке"]
          [:div {:style {:background "white"
                         :color "black"
                         :height "50px"
                         :white-space "nowrap"
                         :overflow "hidden"
                         :text-overflow "ellipsis"
                         :border-radius 15
                         :width "100%"
                         :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                         :font-size 24
                         :align-content "center"
                         :padding "0px 20px"
                         :text-align "center"
                         :font-weight 300}}
           (str (get-in @articul_changes [:prices :discount_price :price]) " руб")]]]
        )
      )
    )
  )