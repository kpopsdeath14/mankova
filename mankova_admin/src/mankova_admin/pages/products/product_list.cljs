(ns mankova-admin.pages.products.product-list
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.events.product-get :refer [product_get]]
   )
  )



(defn product_list []
  (let [List antd/List
        Image antd/Image
        Checkbox antd/Checkbox
        Skeleton antd/Skeleton
        SkeletonNode (.-Node Skeleton)

        products (reagent/cursor app-state [:products])
        selected_products_catalog (reagent/cursor app-state [:selected_products_catalog])
        search_value (reagent/cursor app-state [:product_edit_search_value])
        filters_picked (reagent/cursor app-state [:filters_picked]) 
        products_mode (reagent/cursor app-state [:products_mode])
        ]
    (fn []
      [:> List {:style {:margin-top 25
                        :margin-bottom 25}
                :locale {:emptyText "таких товаров нет"
                         }
                :header (as-element [:div {:style {:color "white" :height 0}} (count @selected_products_catalog)])
                :dataSource @products
                :renderItem (fn [product]
                              (let [product (js->clj product :keywordize-keys true)
                                    vendor_code (:vendor_code product)
                                    checked? (reagent/reaction (some #(= % vendor_code) @selected_products_catalog))
                                    ]
                                (as-element
                                 (if (= @products [{} {} {} {}])
                                   [:div {:style {:height 110
                                                  :borderRadius 15
                                                  :marginBottom 25
                                                  :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}
                                          }
                                    [:> SkeletonNode
                                     {:style {:height 110
                                              :width "100%"
                                              }
                                      :active true}
                                     ]
                                    ]
                                   
                                   [:div {:style {:backgroundColor (if @checked?
                                                                     (case @products_mode
                                                                       "catalog" "#D9D9D9"
                                                                       "archive" "#8E8E8E")
                                                                     (case @products_mode
                                                                       "catalog" "white"
                                                                       "archive" "#D9D9D9"))
                                                  :boxSizing "border-box"
                                                  :padding "20px 30px"
                                                  :display "flex"
                                                  :height 110
                                                  :overflow "hidden"
                                                  :flex-wrap "nowrap"
                                                  :borderRadius 15
                                                  :justifyContent "space-between"
                                                  :alignItems "center"
                                                  :marginBottom 25
                                                  :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}
                                          :onClick (fn [e] 
                                                     (swap! app-state assoc :selected_products_catalog [])
                                                     (js/window.scrollTo 0 0)
                                                     (set! (.-href (.-location js/window)) (str "/mankova/#/product-edit/" (js/encodeURIComponent vendor_code))))}
                                    
                                    [:div {:style {:display "flex"
                                                   :gap 50
                                                   :alignItems "center"}}
                                     [:> Image {:preview false
                                                :src (str "https://tg-market.qq-pp.ru/mankova/mankova_img/img_raw/"
                                                          (first (product :images)))
                                                :style {:height "70px"
                                                        :width "70px"
                                                        :marginLeft "0px"
                                                        :borderRadius "8px"
                                                        :objectFit "cover"}}]
                                     
                                     [:div {:style {:fontWeight 300
                                                    :fontSize "24px"}}
                                      (:vendor_code product)]
                                     
                                     [:div {:style {:fontWeight 700
                                                    :fontSize "24px"}}
                                      (:name product)]]
                                    
                                    
                                    
                                    [:div {:style {:width 100
                                                   :display "flex"
                                                   :height "100%"
                                                   :justify-content "flex-end"}
                                           :onClick (fn [e] (.stopPropagation e))}
                                     
                                     [:> Checkbox {:checked @checked?
                                                   :className "large-checkbox"
                                                   :onChange (fn [e]
                                                               (if @checked?
                                                                 (swap! app-state assoc :selected_products_catalog (remove #(= % vendor_code) @selected_products_catalog))
                                                                 (swap! app-state assoc :selected_products_catalog (conj @selected_products_catalog vendor_code))))}]]]
                                   )

                                 )
                                )
                              )
                }
                                ]
      )
    )
  )