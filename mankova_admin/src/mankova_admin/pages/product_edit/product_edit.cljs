(ns mankova-admin.pages.product-edit.product-edit
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.pages.product-edit.options :refer [options]]
   [mankova-admin.pages.product-edit.search :refer [search]]
   [mankova-admin.pages.product-edit.filters :refer [filters]]
   [mankova-admin.pages.product-edit.product-list :refer [product_list]]
   [mankova-admin.pages.product-edit.footer :refer [footer]]
   [mankova-admin.events.product-get :refer [product_get]]
   [mankova-admin.pages.product-edit.modal-product-editing :refer [modal_product_editing]]
   )
  )


(defn product_edit_page []
  (let [Button antd/Button
        Row (.-Row antd)
        Col (.-Col antd)

        filters_picked (reagent/cursor app-state [:filters_picked]) 
        products_mode (reagent/cursor app-state [:products_mode])
        search_value (reagent/cursor app-state [:product_edit_search_value])
        ]
    (fn []
      [:div {:style {:margin-bottom 141}}
       [modal_product_editing]
       [options]

       [:> Row {:gutter 16
                :style {:margin-bottom 25}}
       
        [:> Col {:span 18}
         [search]
         ]
       
        [:> Col {:span 6}
         [:> Button {:type "primary"
                     :block true
                     :style {:height 50
                             :border-radius 15
                             :overflow "hidden"
                      :white-space "nowrap"
                             :backgroundColor "#D3EAFF"
                             :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"
                             :color "black"
                             :font-size 24
                             :font-weight 300}
                     :onClick (fn []
                                (swap! app-state assoc :adding_new_article? true)
                                (swap! app-state assoc :articul_editing? true) 
                                (set! (.-href (.-location js/window)) (str "/#/articul")) 
                                (if-not (:adding_new_product? @app-state)
                                  (do
                                    (swap! app-state assoc :current_articul {})
                                    (swap! app-state assoc-in [:current_articul :name] (:name (first (:products_edit @app-state))))
                                    (swap! app-state assoc-in [:current_articul :vendor_code] (:current_vendor_code @app-state))
                                    (swap! app-state assoc-in [:articul_changes :name] (:name (first (:products_edit @app-state)))) 
                                    (swap! app-state assoc-in [:articul_changes :actual] "false")
                                    (swap! app-state assoc-in [:articul_changes :vendor_code] (:current_vendor_code @app-state))
                                    )
                                  )
                                (swap! app-state assoc :page :articul)
                                (js/window.scrollTo 0 0)
                                )
                     } 
          "Добавить товар"]]]
       
       [:> Row {:gutter 16
                :style {:margin-bottom 25}}
       
        [:> Col {:span 18}
         [filters]]
       
        [:> Col {:span 6}
         [:> Button {:type "primary"
                     :block true
                     :style {:height 50
                             :border-radius 15
                             :overflow "hidden"
                             :white-space "nowrap"
                             :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"
                             :backgroundColor "#D3EAFF"
                             :color "black"
                             :font-size 24
                             :font-weight 300}
                     
                     :onClick (fn []
                                (swap! app-state assoc :selected_products_product_edit [])
                                (swap! app-state assoc :products_mode (case @products_mode
                                                                        "catalog" "archive"
                                                                        "archive" "catalog"
                                                                        )
                                       ) 
                                (product_get (assoc @filters_picked
                                                    :vendor_code [(:current_vendor_code @app-state)]
                                                    :actual (case @products_mode
                                                              "catalog" ["t" "true"]
                                                              "archive" ["f" "false"]
                                                              )
                                                    ) @search_value) 
                                )
                     }
          (case @products_mode
            "catalog" "Перейти в архив"
            "archive" "Перейти в каталог"
            )
          ]
         ]
        ]
       
       [product_list]

       [footer]
       ]
      )
    )
  )