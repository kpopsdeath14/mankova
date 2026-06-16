(ns markova.pages.order-history.order-history
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   )
  )



(defn order_history_page []
  (let [Image antd/Image
        Button antd/Button
        List antd/List
        ListItem (.-Item List)

        order_history (reagent/cursor app-state [:order_history])]
    (fn []
      [:div {:style {:padding 26}}
       [:> List
        {:style {:border "1px solid #000000"
                 :border-radius 10
                 :padding 10}
         :header (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                            :font-size 16}}
                              "Ваши заказы"])
         :itemLayout "vertical"
         :bordered true
         :dataSource @order_history
         :renderItem (fn [product idx]
                       (let []
                         (as-element
                          [:> ListItem {:style {:padding-left "0px"
                                                :padding-right "0px"
                                                :padding-top "20px"
                                                :padding-bottom "20px"
                                                :border-bottom  (if (= idx (dec (count @order_history))) "none" "1px solid #777777")}
                                        :onClick (fn []
                                                   (.impactOccurred (.-HapticFeedback (.-WebApp js/Telegram)) "light")
                                                   (swap! app-state assoc :page :order)
                                                   (set! (.-href (.-location js/window)) (str "/mankova/#/order" ;(:current_model_id @app-state)
                                                                                              )) 
                                                   )
                                        }
                           [:div {:style {:font-family "'orchidea', sans-serif" 
                                          :margin-bottom 20
                                          }
                                  }
                            "Номер заказа 13372848"
                            ]
                           
                           [:div {:style {:display "flex"
                                          :gap 7
                                          :flex-wrap "wrap"
                                          }}
                            (for [i (range 3)]
                              [:> Image {:src "2025_01_30 Mankova2458.jpg"
                                         :key i
                                         :preview false
                                         :style {:height 72
                                                 :width 68
                                                 :object-fit "cover"
                                                 :object-position "center"
                                                 :border-radius 10}}
                               ]
                              )
                            ]
                           
                           [:div
                            {:style {:font-family "'orchidea_light', sans-serif"
                                     :border-radius 10
                                     :height 34
                                     :background-color "#000"
                                     :color "#fff"
                                     :width 126
                                     :margin-top 20
                                     :display "flex"
                                     :justify-content "center"
                                     :align-items "center"
                                     :font-size 12
                                     }
                             }
                            "Прибыл в ПВЗ"
                            ]
                           

                           ]
                          )
                         )
                       )
         }
         ]
         ]
         )
         )
         )