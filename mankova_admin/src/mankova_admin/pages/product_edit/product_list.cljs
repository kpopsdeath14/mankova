(ns mankova-admin.pages.product-edit.product-list
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.events.articul-get :refer [articul_get]]
   ))




(defn product_list []
  (let [List antd/List
        Image antd/Image
        Checkbox antd/Checkbox
        Skeleton antd/Skeleton
        SkeletonNode (.-Node Skeleton)
        
        products_edit (reagent/cursor app-state [:products_edit]) 
        selected_products (reagent/cursor app-state [:selected_products_product_edit])
        products_mode (reagent/cursor app-state [:products_mode]) 
        ]
    (fn []
      [:> List {:style {:margin-top 25
                        :margin-bottom 25
                        }
                :header (as-element [:div {:style {:color "white" :height 0}} (count @selected_products)])
                :locale {:emptyText "таких товаров нет"
                         }
                :dataSource @products_edit
                :renderItem (fn [product]
                              (let [product (js->clj product :keywordize-keys true)
                                    product_id (product :product_id)
                                    checked? (reagent/reaction (some #(= % product_id) @selected_products))
                                    ]
                                (as-element
                                 (if (= @products_edit [{} {} {} {}])
                                  [:div {:style {:height 110
                                                 :borderRadius 15
                                                 :marginBottom 25
                                                 :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}}
                                   [:> SkeletonNode
                                    {:style {:height "100%"
                                             :width "100%"}
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
                                                  :box-sizing "border-box"
                                                  :overflow "hidden"
                                                  :flex-wrap "nowrap"
                                                  :padding "20px 30px"
                                                  :display "flex"
                                                  :height 110
                                                  :border-radius 15
                                                  :justify-content "space-between"
                                                  :align-items "center"
                                                  :marginBottom 25
                                                  :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}
                                          :onClick (fn []
                                                     (swap! app-state assoc :page :articul)
                                                     (js/window.scrollTo 0 0)
                                                     (set! (.-href (.-location js/window)) (str "/#/articul/" (product :id))))}
                                   
                                    [:div {:style {:display "flex"
                                                   :gap 20
                                                   :align-items "center"}}
                                     [:> Image {:preview false
                                                :src (str "https://tg-market.qq-pp.ru/mankova/mankova_img/img_raw/"
                                                          (first (product :images)))
                                                :style {:height "70px"
                                                        :width "70px"
                                                        :margin-left "0px"
                                                        :border-radius "15px"
                                                        :object-fit "cover"}}]
                                   
                                   
                                     (if-not (nil? (product :color))
                                       [:div {:style {:font-weight 300
                                                      :font-size 24
                                                      :display "flex"
                                                      :align-items "center"
                                                      :justify-content "center"
                                                      :min-width 200
                                                      :width "auto"
                                                      :height 45
                                                      :padding 10
                                                      :border "1px solid black"
                                                      :border-radius 6}}
                                        (product :color)])
                                   
                                   
                                     (if-not (nil? (product :size))
                                       [:div {:style {:font-weight 300
                                                      :font-size 24
                                                      :display "flex"
                                                      :align-items "center"
                                                      :height 45
                                                      :justify-content "center"
                                                      :min-width 50
                                                      :width "auto"
                                                      :padding 10
                                                      :border "1px solid black"
                                                      :border-radius 6}}
                                        (product :size)])
                                   
                                     [:div {:style {:font-weight 700
                                                    :font-size 24}}
                                      (product :id)]]
                                   
                                   
                                    [:div {:style {:display "flex"
                                                   :gap 20
                                                   :align-items "center"}}
                                   
                                     (if-not (nil? (product :tags))
                                       [:div {:style {:font-weight 300
                                                      :font-size 24
                                                      :display "flex"
                                                      :overflow "hidden"
                                                      :white-space "nowrap"
                                                      :align-items "center"
                                                      :justify-content "center"
                                                      :height 45
                                                      :padding 10
                                                      :border "1px solid black"
                                                      :border-radius 6}}
                                        (first (product :tags))])
                                   
                                   
                                     (if-not (nil? (get-in product [:prices :moysklad :price]))
                                       [:div {:style {:font-weight 300
                                                      :font-size 24
                                                      :display "flex"
                                                      :align-items "center"
                                                      :justify-content "center"
                                                      :overflow "hidden"
                                                      :white-space "nowrap"
                                                      :height 45
                                                      :padding 10
                                                      :border "1px solid black"
                                                      :border-radius 6}}
                                        (str (get-in product [:prices :moysklad :price]) " руб")])
                                   
                                     (if-not (nil? (get-in product [:prices :discount_price :price]))
                                       [:div {:style {:font-weight 300
                                                      :font-size 24
                                                      :display "flex"
                                                      :align-items "center"
                                                      :justify-content "center"
                                                      :overflow "hidden"
                                                      :white-space "nowrap"
                                                      :height 45
                                                      :padding 10
                                                      :border "1px solid red"
                                                      :color "red"
                                                      :border-radius 6}}
                                        (str (get-in product [:prices :discount_price :price]) " руб")])]
                                   
                                   
                                    [:div {:style {:width 100
                                                   :display "flex"
                                                   :height "100%"
                                                   :justify-content "flex-end"}
                                           :onClick (fn [e] (.stopPropagation e))}
                                   
                                     [:> Checkbox {:checked @checked?
                                                   :className "large-checkbox"
                                                   :onChange (fn [e]
                                                               (if @checked?
                                                                 (swap! app-state assoc :selected_products_product_edit (remove #(= % product_id) @selected_products))
                                                                 (swap! app-state assoc :selected_products_product_edit (conj @selected_products product_id))))}]]]
                                   ) 
                                 )
                                )
                              )
                }
       ] 
      )
    )
    )
