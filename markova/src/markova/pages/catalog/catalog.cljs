(ns markova.pages.catalog.catalog
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [markova.pages.catalog.banner :refer [banner]]
   [markova.pages.catalog.logo :refer [logo]]
   [markova.pages.catalog.search :refer [search]]
   [markova.pages.catalog.filters :refer [filters]]
   [markova.events.product-get :refer [product_get]]
   [markova.events.product-single-get :refer [product_single_get]]
   )
  )



(defn catalog_page [] 
  (let [
        Card antd/Card
        List antd/List
        Button antd/Button
        ListItem (.-Item List)
        Image antd/Image 
        loading? (reagent/atom false)
        images-loaded (reagent/atom false)
        
        to_scroll (reagent/cursor app-state [:to_scroll])
        products (reagent/cursor app-state [:products])
        visible_count (reagent/cursor app-state [:visible_count])
        ]
    
    (defn load-more []
      (reset! loading? true)
      (js/setTimeout
       (fn []
         (swap! app-state assoc :visible_count (+ 20 @visible_count))
         (reset! loading? false))
       300))
    

    (fn []
      (reagent/after-render
       (fn [] 
         (if-not (nil? (js/document.getElementById (str (:current_vendor_code @app-state) "_" (:current_color @app-state))))
           (let [element (js/document.getElementById (str (:current_vendor_code @app-state) "_" (:current_color @app-state)))]
             (if @to_scroll
               (do 
                 (.scrollIntoView element {:behavior "smooth"})
                 (swap! app-state assoc :to_scroll false)
                 )
               )
             )
           )
         )
       )
      [:div
       [logo]
       [banner] 

       [:div {:style {:padding 26}}
        [search]
        [filters]
        [:> List {:style {:margin-top 20
                          :margin-bottom 20
                          }
                  :dataSource (take @visible_count @products)
                  :grid {:column 2
                         :gutter 38}
                  :renderItem (fn [product]
                                (as-element
                                 [:div {:id (str (:vendor_code (js->clj product :keywordize-keys true)) "_" (:color_translit (js->clj product :keywordize-keys true)))}
                                  [:> ListItem
                                   [:> Card {:style {:width "100%"
                                                     :color "black"
                                                     :overflow "hidden"
                                                     :border-radius 0
                                                     :border "none"}

                                             :onClick (fn []
                                                        (.impactOccurred (.-HapticFeedback (.-WebApp js/Telegram)) "light")
                                                        (set! (.-href (.-location js/window)) (str "/mankova/#/product/" (js/encodeURIComponent (:vendor_code (js->clj product :keywordize-keys true))) "/" (:color_translit (js->clj product :keywordize-keys true)))) 
                                                        (swap! app-state assoc :current_color (:color_translit (js->clj product :keywordize-keys true)))
                                                        (swap! app-state assoc :current_vendor_code (:vendor_code (js->clj product :keywordize-keys true))) 
                                                        (swap! app-state assoc :page :product)
                                                        (js/window.scrollTo 0 0)
                                                        (swap! app-state assoc :to_scroll true)
                                                        )

                                             :cover (as-element
                                                     [:div {:style {:position "relative"
                                                                    :width "100%"
                                                                    :aspect-ratio "1/1.4"
                                                                    :display "flex"
                                                                    :justify-content "center"
                                                                    :align-items "center"
                                                                    :overflow "hidden"}}
                                                      [:> Image
                                                       {:style {:width "100%"
                                                                :height "100%"
                                                                :aspect-ratio "1/1.4"
                                                                :object-fit "cover"
                                                                :object-position "center"}
                                                        :preview false
                                                        :src (str "https://tg-market.qq-pp.ru/mankova/mankova_img/img_raw/"
                                                                  (first (:images (js->clj product :keywordize-keys true)))) 
                                                        }
                                                       ]
                                                      
                                                      
                                                      (if-not (nil? (:tags (js->clj product :keywordize-keys true)))
                                                        [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                       :border-radius 15
                                                                       :border "1px solid black"
                                                                       :position "absolute"
                                                                       :min-height 30
                                                                       :heigh "auto"
                                                                       :background-color "#fff"
                                                                       :color "#000"
                                                                       :width "auto"
                                                                       :min-width 100
                                                                       :top 5
                                                                       :right 5
                                                                       :display "flex"
                                                                       :justify-content "center"
                                                                       :align-items "center"
                                                                       :padding "0 12px"}}
                                                         (first (:tags (js->clj product :keywordize-keys true)))]
                                                        )
                                                      ]
                                                     )}
                                    [:div {:style {:padding 10
                                                   :width "100%"
                                                   :font-family "'orchidea_light', sans-serif"
                                                   :text-align "center"}}
                                     [:div ((js->clj product :keywordize-keys true) :product_name)]
                                     [:div
                                      (let [prices      (-> (js->clj product :keywordize-keys true) :prices)
                                            moysklad    (->> prices (filter :moysklad) first :moysklad)
                                            discount    (->> prices (filter :discount_price) first :discount_price)]
                                        (if discount
                                          [:div
                                           [:span (:price discount) " р. "]
                                           [:span {:style {:text-decoration "line-through"
                                                           :color "#777777"
                                                           }} (:price moysklad) " р."]]
                                          [:span (:price moysklad) " р."]))]
                                     ]
                                    ]
                                   ]
                                  ]
                                 )
                                )
                  }
         ]
        ]
       
       (when (< @visible_count (count @products))
         [:div {:style {:display "flex"
                        :justify-content "center"
                        :margin-left 26
                        :margin-right 26
                        }
                }
          [:> Button {:type "primary"
                      :loading @loading?
                      :on-click load-more
                      :style {:font-family "'orchidea_light', sans-serif"
                              :border-radius 10
                              :height 40
                              :background-color "#000"
                              :color "#fff"
                              :width "50%"
                              :margin-top 23 
                              }
                      }
           "Загрузить еще"
           ]
          ]
         )
       ]
      )
    )
  )