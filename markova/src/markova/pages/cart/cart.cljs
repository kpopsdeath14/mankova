(ns markova.pages.cart.cart
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [markova.events.cart-get :refer [cart_get]]
   [markova.events.cart-set :refer [cart_set]]
   )
  )



(defn cart_page []
  (cart_get)
  (let [
        Image antd/Image
        Button antd/Button
        MinusCircleOutlined icons/MinusCircleOutlined
        PlusCircleOutlined icons/PlusCircleOutlined
        CloseCircleOutlined icons/CloseCircleOutlined
        Flex antd/Flex
        List antd/List
        ListItem (.-Item List)
        
        cart (reagent/cursor app-state [:cart])
        cart_summary (reagent/cursor app-state [:cart_summary])
        ]
    (fn []
      [:div {:style {
                     :padding 26
                     }
             }
       [:> List
        {:style {:border "1px solid #000000"
                 :border-radius 10
                 :padding 15
                 :font-family "'orchidea_light', sans-serif"
                 :font-size 16
                 }
         :header (as-element [:div {:style {
                                            :font-family "'orchidea_light', sans-serif"
                                            :font-size 16
                                            }
                                    }
                              "Ваш заказ"
                              ]
                             )
         :footer (as-element 
                  [:div {:style {:font-family "'orchidea_light', sans-serif" 
                                 :font-size 16
                                 :text-align "end"
                                 }
                         }
                   (if-not (or (= 0 @cart_summary) (nil? @cart_summary) (= "0" @cart_summary))
                     (str "Сумма: " @cart_summary " р")
                     )
                   ]
                  ) 
         :locale {:emptyText "Корзина пуста"}
         :itemLayout "vertical"
         :bordered true
         :dataSource @cart
         :renderItem (fn [product idx]
                       (let []
                         (as-element
                          [:> ListItem {:style {:padding-left "0px"
                                                :padding-right "0px"
                                                :padding-top "35px"
                                                :padding-bottom "35px"
                                                :border-bottom "1px solid #777777"}
                                        :onClick (fn []
                                                   (let [
                                                         product (:product_attributes (js->clj product :keywordize-keys true))
                                                         ]
                                                     (.impactOccurred (.-HapticFeedback (.-WebApp js/Telegram)) "light")
                                                     (set! (.-href (.-location js/window)) (str "/mankova/#/product/" (:vendor_code product) "/" (:color_translit product)))
                                                     (swap! app-state assoc :current_color (:color_translit product))
                                                     (swap! app-state assoc :current_vendor_code (:vendor_code product))
                                                     (swap! app-state assoc :page :product)
                                                     (js/window.scrollTo 0 0)
                                                     (swap! app-state assoc :to_scroll false)
                                                     ) 
                                                   )
                                        }
                           [:div
                            [:> Flex {:justify "space-between"
                                      :align-items "flex-start"
                                      :style {:display "flex"
                                              :height "100%"
                                              :gap 10}}
                             
                             [:> Image {:src (str "https://tg-market.qq-pp.ru/mankova/mankova_img/img_raw/"
                                                  (first (get-in (js->clj product :keywordize-keys true) [:product_attributes :images]))
                                                  )
                                        :preview false
                                        :style {
                                                :height "75px"
                                                :width "75px"
                                                :margin-left "0px"
                                                :border-radius "10px"
                                                :object-fit "cover"
                                                }
                                        }
                              ]
                             
                             [:> Flex {:style {:flex-direction "column"
                                               :width "80%"
                                               :marin-left 75
                                               :margin-top "0px"
                                               :padding-top "0px"}
                                       :justify "space-between"}
                              
                              [:div {:style {:font-family "'orchidea', sans-serif"
                                             :font-size 16
                                             }
                                     }
                               (get-in (js->clj product :keywordize-keys true) [:product_attributes :name])
                               ]
                              

                              [:div {:style {:font-family "'orchidea_light', sans-serif"
                                             :font-size 12
                                             :color "#777777"}} 
                               (str  "Цвет: " (get-in (js->clj product :keywordize-keys true) [:product_attributes :color]))
                               ]

                              [:div {:style {
                                             :font-family "'orchidea_light', sans-serif"
                                             :font-size 12
                                             :color "#777777"
                                             }
                                     }
                               (str "Размер: " (get-in (js->clj product :keywordize-keys true) [:product_attributes :size]))
                               ]
                              
                              [:div {:style {:font-family "'orchidea_light', sans-serif"
                                             :font-size 12
                                             :color "#777777"}}
                               (get-in (js->clj product :keywordize-keys true) [:product_attributes :id])]
                              ]
                             
                             [:> Flex {:style {:flex-direction "column"
                                               :width "20%"
                                               :margin-top "0px"
                                               :padding-top "0px"
                                               :align-items "flex-end"}
                                       :justify "space-between"}
                              
                              
                              [:> Button
                               {:onClick (fn [e]
                                           (.stopPropagation e)
                                           (cart_set (:product_id (js->clj product :keywordize-keys true))
                                                     0
                                                     0
                                                     )
                                           )
                                :style {:box-shadow "none"
                                        :border "none"
                                        :background "none"
                                        :color "#777777"}
                                :icon (as-element [:> CloseCircleOutlined {:style {:font-size "16px"}}])}]
                              ]
                             ]
                            
                            [:> Flex {:style {:gap 30}}
                             
                             [:> Flex
                              {:style {:margin-left 85}
                               :onClick (fn [e]
                                          (.stopPropagation e))}
                              [:> Button
                               {:onClick (fn [e]
                                           (.stopPropagation e)
                                           
                                           (cart_set (:product_id (js->clj product :keywordize-keys true))
                                                     (dec (:quantity (js->clj product :keywordize-keys true)))
                                                     (* (dec (:quantity (js->clj product :keywordize-keys true))) (:summ (js->clj product :keywordize-keys true)))))
                                :style {:box-shadow "none"
                                        :border "none"
                                        :background "none"
                                        :color "#777777"}
                                :icon (as-element [:> MinusCircleOutlined {:style {:font-size "16px"}}])}]
                              [:span
                               {:style {:text-align "center"
                                        :padding "0 10px"
                                        :min-width "30px"
                                        :display "flex"
                                        :align-items "center"
                                        :justify-content "center"
                                        :font-family "'orchidea_light', sans-serif"
                                        :font-size 16}}
                               (get-in (js->clj product :keywordize-keys true) [:quantity])]
                              [:> Button
                               {:onClick (fn [e]
                                           (.stopPropagation e)
                                           (if-not (> (inc (:quantity (js->clj product :keywordize-keys true))) (:stock_quantity (js->clj product :keywordize-keys true)))
                                             (cart_set (:product_id (js->clj product :keywordize-keys true))
                                                       (inc (:quantity (js->clj product :keywordize-keys true)))
                                                       (* (inc (:quantity (js->clj product :keywordize-keys true))) (:summ (js->clj product :keywordize-keys true))))))
                                :style {:box-shadow "none"
                                        :border "none"
                                        :background "none"
                                        :color "#777777"}
                                :icon (as-element [:> PlusCircleOutlined {:style {:font-size "16px"}}])}]]
                             
                             [:div {:style {:font-family "'orchidea_light', sans-serif"
                                            :font-size 16}}
                              (str (* (:quantity (js->clj product :keywordize-keys true)) (:summ (js->clj product :keywordize-keys true))) " р.")
                              ]
                             ]
                            
                            (cond 
                              (= 0 (:stock_quantity (js->clj product :keywordize-keys true))) 
                              
                              [:div {:style {:border "1px solid red"
                                             :border-radius 10
                                             :font-family "'orchidea_light', sans-serif"
                                             :font-size 12
                                             :height 25
                                             :display "flex"
                                             :color "red"
                                             :justify-content "center"
                                             :align-items "center"}}
                               "нет в наличии"]
                              

                              (> (:quantity (js->clj product :keywordize-keys true)) (:stock_quantity (js->clj product :keywordize-keys true)))

                              [:div {:style {:border "1px solid red"
                                             :border-radius 10
                                             :font-family "'orchidea_light', sans-serif"
                                             :font-size 12
                                             :height 25
                                             :display "flex"
                                             :color "red"
                                             :justify-content "center"
                                             :align-items "center"}}
                               (str "доступно: " (:stock_quantity (js->clj product :keywordize-keys true)) "шт")
                               ]
                              
                              :else nil
                              )
                            ]
                            ])))}]
                            
                            [:div {:style {:border "1px solid #000000"
                                           :border-radius 10
                                           :padding 15
                                           :font-family "'orchidea_light', sans-serif"
                                           :font-size 16
                                           :text-align "center"
                                           :margin-top 20}
                                   }
                             "Оформляя заказ, вы подтверждаете, что ознакомлены и согласны с условиями "
                             [:span {:style {:color "#777777"
                                             :text-decoration "underline"}
                                     :onClick (fn []
                                                (swap! app-state assoc :page :information)
                                                (set! (.-href (.-location js/window)) "/mankova/#/information"))}
                              "публичного договора-оферты."]
                             ]
                            ]
      
      ) 
    )
    )