(ns markova.pages.product.characteristics
  (:require
   ["antd" :as antd]
   [markova.db :refer [app-state]]
   ["@ant-design/icons" :as icons]
   ["react-photo-view" :as photo_review]
   [reagent.core :as reagent :refer [as-element]]
   [clojure.string :as str]
   [markova.events.cart-set :refer [cart_set]] 
   )
  )


(defn find-article-by-size [ size]
  (some (fn [item]
          (when (= (:size item) size)
            (:article item)))
        (:sizes (:product_current @app-state)))
  )

(defn find-product-id-by-size [ size]
  (some (fn [item]
          (when (= (:size item) size)
            (:product_id item)))
        (:sizes (:product_current @app-state)))
  )


(defn characteristics []
  (let [
        Flex antd/Flex
        Image antd/Image
        Space antd/Space
        Select antd/Select
        Divider antd/Divider
        Button antd/Button
        Collapse antd/Collapse
        Skeleton antd/Skeleton

        PlusOutlined icons/PlusOutlined

        product_current (reagent/cursor app-state [:product_current])
        current_sizes   (reagent/cursor app-state [:current_sizes])
        selected_size   (reagent/cursor app-state [:selected_size])
        ]
    (fn []
      (if-not (and (= (@product_current :color_translit) (:current_color @app-state)) (= (:vendor_code (@app-state :products_list_current)) (:current_vendor_code @app-state)))
        [:> Skeleton]
        
        [:> Space {:direction "vertical"
                   :style {:width "100%"
                           :padding 26
                           :text-align "left"}}
         
         [:div {:style {:font-family "'orchidea_light', sans-serif"
                        :display "flex"
                        :justify-content "space-between"
                        :font-size 16
                        :align-items "center"
                        :text-align "center"
                        }}
          
          [:div (:product_name @product_current)]
         
          [:div
           [:> antd/Typography.Link {:href (str "https://t.me/share/url?url="
                                                (:product_link @product_current)
                                                "&text=" (:product_name @product_current) " (" (:color @product_current) ")"
                                                )
                                     :onClick (fn []
                                                (.impactOccurred (.-HapticFeedback (.-WebApp js/Telegram)) "light"))
                                     :style {:display "flex"
                                             :align-items "center"}}
            [:> Image {:src "share.png"
                       :preview false
                       :style {:height 20
                               :width  20
                               }
                       }
             ]
            ]]
          ]
         
         
         [:div {:style {:margin-top 20
                        :font-family "'orchidea_light', sans-serif"
                        :color "#777777"}}  (str "Артикул " (find-article-by-size @selected_size))]
         [:> Flex {:justify "flex-start"
                   :style {:margin-top 20}}
          
          [:div {:style {:font-family "'orchidea_light', sans-serif"}}
           (let [prices      (-> (:product_current @app-state) :prices)
                 moysklad    (->> prices (filter :moysklad) first :moysklad)
                 discount    (->> prices (filter :discount_price) first :discount_price)]
             (if discount
               [:div
                [:span (:price discount) " р. "]
                [:span {:style {:text-decoration "line-through"
                                :color "#777777"}} (:price moysklad) " р."]]
               [:span (:price moysklad) " р."]))]
          ]
         
         [:div {:style {:font-family "'orchidea_light', sans-serif"
                        :font-size 16
                        :margin-top 20}}
          "Цвет"]
         (let [
               products (:products_list_current @app-state)
               ]
           [:> Select {:options (map (fn [product] {:label (:color product) :value (:color_translit product)}) (:products products))
                       :value (:color @product_current)
                       :onChange (fn [value]
                                   (swap! app-state assoc :product_current (first (filter #(= (:color_translit %) value) (:products products))))
                                   (swap! app-state assoc :current_color value)
                                   (swap! app-state assoc :selected_size (:size (first (:sizes (:product_current @app-state)))))
                                   (swap! app-state assoc :current_product_id (:product_id (first (:sizes (:product_current @app-state)))))
                                   (swap! app-state assoc :current_sizes (vec (map (fn [size] {:value (:size size) :label (:size size)}) (:sizes (:product_current @app-state)))))
                                   )
                       :style {:width 150
                               :font-family "'orchidea_light', sans-serif"
                               :height 25
                               :border "1px solid black"
                               :border-radius 5
                               }
                       }
            ]
           )
         
         
         [:div {:style {:font-family "'orchidea_light', sans-serif"
                        :font-size 16
                        :margin-top 10}}
          "Размер"]
         [:> Select {:options @current_sizes
                     :value @selected_size
                     :onChange (fn [value]
                                 (swap! app-state assoc :selected_size value)
                                 (swap! app-state assoc :current_product_id (find-product-id-by-size value)))
                     :style {:width 150 
                             :border "1px solid black"
                             :border-radius 5
                             :font-family "'orchidea_light', sans-serif"
                             :height 25}}]
         
         [:> Button
          {:style {:font-family "'orchidea_light', sans-serif"
                   :border-radius 10
                   :height 62
                   :background-color "#000"
                   :color "#fff"
                   :width "100%"
                   :margin-top 23}
           :onClick (fn []
                      (cart_set (@app-state :current_product_id) 1 (:price (:moysklad (first (:prices (:current_product @app-state)))))))}
          "Добавить в корзину"] 
         
         [:div {:style {:font-family "'orchidea_light', sans-serif"
                        :margin-top 10
                        :white-space "pre-line"}}
          (:product_description @product_current)]
         
         [:div {:style {:font-family "'orchidea_light', sans-serif"
                        :font-size 13
                        :margin-top 10
                        :text-decoration "underline"}
                :onClick (fn []
                           (swap! app-state assoc :page :delivery_description)
                           (set! (.-href (.-location js/window)) (str "/mankova/#/delivery-description"))
                           (js/window.scrollTo 0 0))}
          "ДОСТАВКА"]
         
         [:div {:style {:margin-left 10
                        :margin-right 10
                        :margin-top 20}}
          [:> Divider {:style {:borderColor "#d9d9d9"}}]]
         
         [:> Collapse {:style {:margin-left 10
                               :margin-right 10
                               :border "none"
                               :background-color "white"}
                       :defaultActiveKey "fabric"
                       :expandIconPosition "end"
                       :accordion true
                       :expandIcon (fn [panel-props]
                                     (as-element
                                      [:div {:style {:display "flex"
                                                     :align-items "center"
                                                     :justify-content "center"
                                                     :height "100%"}}
                                       [:> PlusOutlined {:style {:fontSize 16
                                                                 :transform (if (.-isActive panel-props)
                                                                              "rotate(45deg)"
                                                                              "rotate(0deg)")
                                                                 :transition "transform 0.3s"
                                                                 :color "#777777"}}]]))
                       :items (->> [{:label (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                       :height 40
                                                                       :display "flex"
                                                                       :align-items "center"}}
                                                         "Cостав"])
                                     :children (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"}}
                                                            (:made_of @product_current)])
                                     :key "fabric"
                                     :visible? (:made_of @product_current)}
                                    
                                    {:label (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                       :height 40
                                                                       :display "flex"
                                                                       :align-items "center"}}
                                                         "Обмеры изделия"])
                                     :children (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"}}
                                                            (:product_measurements @product_current)])
                                     :key "sizes"
                                     :visible? (:product_measurements @product_current)}
                                    
                                    {:label (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                       :height 40
                                                                       :display "flex"
                                                                       :align-items "center"}}
                                                         "Параметры модели"])
                                     :children (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"}}
                                                            (:model_on_picture_parameters @product_current)])
                                     :key "parameters"
                                     :visible? (:model_on_picture_parameters @product_current)}
                                    
                                    {:label (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                       :height 40
                                                                       :display "flex"
                                                                       :align-items "center"}}
                                                         "Уход"])
                                     :children (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"}}
                                                            (:product_care_info @product_current)])
                                     :key "care"
                                     :visible? (:product_care_info @product_current)}
                                    
                                    ]
                                   
                                   (filter :visible?)
                                   (map #(dissoc % :visible?)))
                       }
          ]
         [:div {:style {:margin-left 10
                        :margin-right 10}}
          [:> Divider {:style {:borderColor "#d9d9d9"
                               :margin-top "0px !important"}}]]
         ]
        ) 
      )
    )
  )