(ns mankova-admin.pages.products.products
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.pages.products.banner-list :refer [banner_list]]
   [mankova-admin.pages.products.search :refer [search]]
   [mankova-admin.pages.products.filters :refer [filters]]
   [mankova-admin.pages.products.product-list :refer [product_list]]
   [mankova-admin.pages.products.footer :refer [footer]]
   [mankova-admin.pages.products.modal-banner-edit :refer [modal_banner_edit]] 
   [mankova-admin.pages.products.modal-product-add :refer [modal_product_add]]
   [mankova-admin.events.vendore-code-get :refer [vendore_code_get]] 
   )
  )


(defn transform_filters [m]
  (->> m
       (filter (fn [[_ v]] (not-empty v)))
       (mapv (fn [[k v]] {:attribute_name (name k) :attribute_values v}))))


(defn products_page []
  (let [Button antd/Button
        Row (.-Row antd)
        Col (.-Col antd) 

        products_mode (reagent/cursor app-state [:products_mode])
        search_value (reagent/cursor app-state [:search_value])
        ]
    (fn []
      [:div {:style {:margin-bottom 141}}
       [modal_banner_edit]
       [modal_product_add]
       [banner_list]
       [:> Row {:gutter 16
                :style {
                        :margin-bottom 25
                }
                }
        
        [:> Col {:span 18}
         [search]
         ]
        
        [:> Col {:span 6}
         [:> Button {:type "primary" 
                     :block true
                     :style {:height 50
                             :overflow "hidden"
                             :white-space "nowrap"
                             :border-radius 15
                             :backgroundColor "#D3EAFF"
                             :color "black"
                             :font-size 24
                             :font-weight 300
                             :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"
                             }
                     :onClick (fn []
                                (swap! app-state assoc :show_modal_product_add? true)
                                (swap! app-state assoc :current_articul {})
                                )
                     } 
          "Добавить товар"
          ]
         ]
        
        ]
       
       [:> Row {:gutter 16
                :style {:margin-bottom 25}}
       
        [:> Col {:span 18}
         [filters]
         ]
       
        [:> Col {:span 6}
         [:> Button {:type "primary"
                     :block true
                     :style {:height 50
                             :border-radius 15
                             :backgroundColor "#D3EAFF"
                             :color "black"
                             :overflow "hidden"
                             :white-space "nowrap"
                             :font-size 24
                             :font-weight 300
                             :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"
                             }
                     :onClick (fn []
                                (swap! app-state assoc :selected_products_catalog [])
                                (swap! app-state assoc :products_mode (case @products_mode
                                                                        "catalog" "archive"
                                                                        "archive" "catalog"
                                                                        )
                                       )
                                (vendore_code_get (assoc (:filters_picked @app-state) :actual [(case @products_mode
                                                                                                 "catalog" ["t" "true"]
                                                                                                 "archive" ["f" "false"])]) @search_value)
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

       [:> Row {:gutter 16
                :style {:margin-bottom 25}
                }
        [:> Col {:span 6 :offset 6}
         [:div {:style {:background "#D3EAFF",
                        :padding "16px",
                        :height "50px",
                        :border-radius "15px" 
                        :font-size 24
                        :font-weight 300
                        :display "flex"
                        :align-items "center"
                        :justify-content "center"
                        }}
          "Страница 1 из 10"
          ]
         ]
       
        [:> Col {:span 6}
         [:div {:style {:background "#D3EAFF",
                        :padding "16px",
                        :height "50px",
                        :border-radius "15px"
                        :font-size 24
                        :font-weight 300
                        :display "flex"
                        :align-items "center"
                        :justify-content "center"
                        }}
          "Показывать: 10"]
         ] 
        ]
       [footer]
       ]
      )
    )
  )