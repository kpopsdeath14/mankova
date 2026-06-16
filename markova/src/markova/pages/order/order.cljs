(ns markova.pages.order.order
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   )
  )



(defn order_page []
  (let [Image antd/Image
        Button antd/Button
        MinusCircleOutlined icons/MinusCircleOutlined
        PlusCircleOutlined icons/PlusCircleOutlined
        CloseCircleOutlined icons/CloseCircleOutlined
        Flex antd/Flex
        List antd/List
        ListItem (.-Item List)

        cart (reagent/cursor app-state [:cart])]
    (fn []
      [:div {:style {:padding 26}}
       [:> List
        {:style {:border "1px solid #000000"
                 :border-radius 10
                 :padding 15
                 }
         :header (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                            :font-size 16}}
                              "Заказ 13372848"])
         :footer (as-element
                  [:div {:style {:font-family "'orchidea_light', sans-serif"
                                 :display "flex"
                                 :flex-direction "column"
                                 :gap "8px"
                                 :margin-top 20
                                 }
                         }
                   (for [status [{:status "Оформлен" :date "21.02.2025"}
                                 {:status "Отправлен" :date "21.02.2025"} 
                                 {:status "Доставляется" :date "21.02.2025"}
                                 {:status "Прибыл в ПВЗ" :date "21.02.2025"}
                                 {:status "Выполнен" :date "21.02.2025"}
                                 ]
                         ]
                     [:> Flex {:style {:border "1px solid #000000"
                                       :border-radius 10
                                       :height 34
                                       :padding-left 34
                                       :padding-right 34
                                       :align-items "center"
                                       :font-size 12}
                               :justify "space-between"}
                      [:div (status :status)]
                      [:div (status :date)]
                      ]
                     )
                   ]
                  
                  )
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
                                                   (.impactOccurred (.-HapticFeedback (.-WebApp js/Telegram)) "light")
                                                   (swap! app-state assoc :current_model_id (js/parseInt (get-in (js->clj product :keywordize-keys true) [:product_info :model_id])))
                                                   (set! (.-href (.-location js/window)) (str "/mankova/#/product/" (:current_model_id @app-state)))
                                                   (swap! app-state assoc :number_of_picked_product (get-in (js->clj product :keywordize-keys true) [:amount]))
                                                   (swap! app-state assoc :picked_color (get-in (js->clj product :keywordize-keys true) [:product_info :color]))
                                                   (swap! app-state assoc :picked_size (get-in (js->clj product :keywordize-keys true) [:product_info :size]))
                                                   (swap! app-state assoc :dont_update_product_options? true)
                                                   (swap! app-state assoc :page :product))}
                           [:div
                            [:> Flex {:justify "space-between"
                                      :align-items "flex-start"
                                      :style {:display "flex"
                                              :height "100%"
                                              :gap 10}}

                             [:> Image {:src "2025_01_30 Mankova2458.jpg"
                                        :preview false
                                        :style {:height "75px"
                                                :width "75px"
                                                :margin-left "0px"
                                                :border-radius "10px"
                                                :object-fit "cover"}}]

                             [:> Flex {:style {:flex-direction "column"
                                               :width "80%"
                                               :marin-left 75
                                               :margin-top "0px"
                                               :padding-top "0px"}
                                       :justify "space-between"}

                              [:div {:style {:font-family "'orchidea', sans-serif"
                                             :font-size 16}}
                               "Top Force"]


                              [:div {:style {:font-family "'orchidea_light', sans-serif"
                                             :font-size 12
                                             :color "#777777"}}
                               "Цвет: серый"]

                              [:div {:style {:font-family "'orchidea_light', sans-serif"
                                             :font-size 12
                                             :color "#777777"}}
                               "Размер: S"]

                              [:div {:style {:font-family "'orchidea_light', sans-serif"
                                             :font-size 12
                                             :color "#777777"}}
                               "001337"]]

                             [:> Flex {:style {:flex-direction "column"
                                               :width "fit-content"
                                               :min-width "100px"
                                               :white-space "nowrap"
                                               :margin-top "0px"
                                               :padding-top "0px"
                                               :align-items "flex-end"}
                                       :justify "space-between"}


                              [:div {:style {:font-family "'orchidea_light', sans-serif"
                                             :font-size 16}}
                               "7 990 р"
                               ]
                              
                              [:div {:style {:font-family "'orchidea_light', sans-serif"
                                             :font-size 16}}
                               "1"
                               ]
                              
                              ]
                             ] 
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