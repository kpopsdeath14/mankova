(ns markova.pages.shipping.shipping
  (:require
   ["antd" :as antd]
   ["react-phone-number-input" :default PhoneInput]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [clojure.string :as string]
   [markova.pages.shipping.address-select :refer [address_select]]
   [markova.pages.shipping.city-select :refer [city_select]]
   [markova.events.cdek-calculate-shipping :refer [cdek_calulate_shipping]]
   )
  )


(defn phone_input []
  (let [value (reagent/atom "+7")
        phone (reagent/cursor app-state [:shipping_data :phone])]

    (fn []
      (let [handle-change (fn [e]
                            (let [new-value (.. e -target -value)
                                  digits (-> new-value
                                             (clojure.string/replace #"\D" "")
                                             (subs 1))
                                  formatted (if (<= (count digits) 10)
                                              (str "+7 ("
                                                   (when (seq digits) (subs digits 0 3))
                                                   (when (> (count digits) 3) (str ") " (subs digits 3 6)))
                                                   (when (> (count digits) 6) (str "-" (subs digits 6 8)))
                                                   (when (> (count digits) 8) (str "-" (subs digits 8 10))))
                                              @value)]
                              (reset! value formatted)
                              (reset! phone (str "+7" digits))))]

        [:> antd/Input
         {:value @value
          :on-change handle-change
          :inputMode "tel"
          :placeholder "+7 (___) ___-__-__"
          :maxLength 18
          :style {:font-family "'orchidea_light', sans-serif"
                  :border-radius 10
                  :border "1px solid #000000"
                  :width "100%"
                  :height 42}}]))))


(defn shipping_page []
  (let [web-app (.-WebApp js/Telegram)
        
        Form antd/Form
        FormItem (.-Item Form)
        Space antd/Space
        Input antd/Input
        Divider antd/Divider 

        Radio antd/Radio
        RadioGroup (.-Group Radio)
        
        delivery_type (reagent/cursor app-state [:shipping_data :delivery_type])
        payment_type (reagent/cursor app-state [:shipping_data :payment_type])
        
        shipping_city (reagent/cursor app-state [:shipping_data :shipping_city]) 
        shipping_price (reagent/cursor app-state [:shipping_data :shipping_price]) 
        cart_summary (reagent/cursor app-state [:cart_summary])
        home (reagent/cursor app-state [:shipping_data :home])
        
        ]
    (if-not (nil? (:city_cdek_code @app-state))
      (cdek_calulate_shipping (:city_cdek_code @app-state))
      )
    (fn [] 
      [:div {:style {:padding 26}}
       [:> Form
        [:> Space {:direction "vertical"
                   :style {:border-radius 10
                           :border "1px solid #000000"
                           :width "100%"
                           :text-align "left"
                           :padding 15}}
         [:div {:style {:margin-top 15
                        :font-size 18
                        :font-family "'orchidea', sans-serif"}}
          "Заполните данные"
          [:> Divider {:style {:borderColor "#777777"
                               :margin "0px !important"}}]]
         [:div {:style {:padding-top 20
                        :padding-left 20
                        :padding-right 20}}
          
          [:> FormItem {:name "surname"
                        :rules [{:required true :message "введите фамилию"}]
                        :style {:margin-bottom 20}
                        }
           [:> Input {:style {:font-family "'orchidea_light', sans-serif"
                              :border-radius 10
                              :border "1px solid #000000"
                              :width "100%"
                              :height 42
                              }
                      :defaultValue (get-in @app-state [:shipping_data :surname])

                      :onPressEnter (fn []
                                      (.hideKeyboard web-app))
                      
                      :onChange (fn [value]
                                  (swap! app-state assoc-in [:shipping_data :surname] (-> value .-target .-value))
                                  )
                      :placeholder "Фамилия"}
            ]
           ]
          
          [:> FormItem {:name "first_name"
                        :rules [{:required true :message "введите имя"}]
                        :style {:margin-bottom 20}}
           [:> Input {:style {:font-family "'orchidea_light', sans-serif"
                              :border-radius 10
                              :border "1px solid #000000"
                              :width "100%"
                              :height 42}
                      :defaultValue (get-in @app-state [:shipping_data :first_name])
                      :onPressEnter (fn []
                                      (.hideKeyboard web-app))
                      :onChange (fn [value]
                                  (swap! app-state assoc-in [:shipping_data :first_name] (-> value .-target .-value)))
                      :placeholder "Имя"}]]
          
          [:> FormItem {:name "patronymic"
                        :rules [{:required true :message "введите отчество"}]
                        :style {:margin-bottom 20}}
           [:> Input {:style {:font-family "'orchidea_light', sans-serif"
                              :border-radius 10
                              :border "1px solid #000000"
                              :width "100%"
                              :height 42}
                      :defaultValue (get-in @app-state [:shipping_data :patronymic])
                      :onPressEnter (fn []
                                      (.hideKeyboard web-app))
                      :onChange (fn [value]
                                  (swap! app-state assoc-in [:shipping_data :patronymic] (-> value .-target .-value)))
                      :placeholder "Отчество"}]]
          
          [:> FormItem {:name "mail" 
                        :rules [{:required true :message "Введите почту"}
                                {:type "email" :message "Введите корректный email"}]
                        :validateTrigger "onBlur"
                        :style {:margin-bottom 20}
                        }
        
          [:> Input {:style {:font-family "'orchidea_light', sans-serif"
                             :border-radius 10
                             :border "1px solid #000000"
                             :width "100%"
                             :height 42
                             :type "email"
                             }
                     :defaultValue (get-in @app-state [:shipping_data :mail])
                     :onPressEnter (fn []
                                     (.hideKeyboard web-app))
                     :onChange (fn [value]
                                 (swap! app-state assoc-in [:shipping_data :mail] (-> value .-target .-value)))
                     :placeholder "Почта"}]
           
           ]
          
          [:> FormItem {:name "phone"
                        :rules [{:required true
                                 :message "Пожалуйста, введите номер телефона!"}
                                ]
                        :style {:marginBottom 20}
                        }

           
           [phone_input]


           ]
        
          [:div {:style {:font-size 16
                         :font-family "'orchidea', sans-serif"
                         :margin-bottom 20}}
           "Доставка"]
        
          [:div {:style {:font-size 16
                         :font-family "'orchidea_light', sans-serif"
                         :margin-bottom 20}}
           "Город"]
         
          [city_select]
        
          [:> RadioGroup {:value @delivery_type
                          :class-name "custom-radio"
                          :style {:display "flex"
                                  :flex-direction "column"
                                  :gap "12px"
                                  :margin-bottom 20}
                          :onChange (fn [e]
                                      (let [value (.. e -target -value)]
                                        (swap! app-state assoc-in [:shipping_data :delivery_type] value)
                                        (cdek_calulate_shipping (:city_cdek_code @app-state))
                                        )
                                      )
                          :options [{:label "Доставка CDЕК в пункт от 1 дня" :value "cdek"}
                                    {:label "Доставка курьером CDЕК от 1 дня" :value "courier"}]}
           ]
        
          (cond (= @delivery_type "cdek")
                [:div 
                 [:div {:style {:font-family "'orchidea_light', sans-serif"
                                :font-size 16
                                :margin-bottom 20}}
                  "Пункт получения"
                  ] 
                 [address_select]
                 ]
                
                (= @delivery_type "courier")
                   [:div
                    [:> FormItem {:name "street"
                                  :rules [{:required true :message "введите улицу"}]
                                  :style {:margin-bottom 20}}
                     [:> Input {:style {:font-family "'orchidea_light', sans-serif"
                                        :border-radius 10
                                        :border "1px solid #000000"
                                        :width "100%"
                                        :height 42}
                                :defaultValue (get-in @app-state [:shipping_data :street])
                                :onPressEnter (fn []
                                                (.hideKeyboard web-app))
                                :onChange (fn [value]
                                            (swap! app-state assoc-in [:shipping_data :street] (-> value .-target .-value)))
                                :placeholder "Улица"}]]
                    
                    [:div {:style {:display "flex" 
                                   :margin-bottom 20
                                   :gap 20
                                   }
                           }
                     [:> FormItem {:name "home"
                                   :rules [{:required true :message "введите дом"}]
                                   :style {:flex 1 :margin-bottom 0}}
                      [:> Input {:style {:font-family "'orchidea_light', sans-serif"
                                         :border-radius 10
                                         :border "1px solid #000000"
                                         :width "100%"
                                         :height 42}
                                 :defaultValue (get-in @app-state [:shipping_data :home])
                                 :onPressEnter (fn []
                                                 (.hideKeyboard web-app))
                                 :onChange (fn [value]
                                             (swap! app-state assoc-in [:shipping_data :home] (-> value .-target .-value)))
                                 :placeholder "Дом и корп."}]
                      ]
                    
                     [:> FormItem {:name "flat"
                                   :rules [{:required true :message "введите номер квартиры"}]
                                   :style {:flex 1 :margin-bottom 0 }}
                      [:> Input {:style {:font-family "'orchidea_light', sans-serif"
                                         :border-radius 10
                                         :border "1px solid #000000"
                                         :width "100%"
                                         :height 42}
                                 :defaultValue (get-in @app-state [:shipping_data :flat])
                                 :onPressEnter (fn []
                                                 (.hideKeyboard web-app))
                                 :onChange (fn [value]
                                             (swap! app-state assoc-in [:shipping_data :flat] (-> value .-target .-value)))
                                 :placeholder "Квартира/офис"}]]]
                    

                    [:div {:style {:display "flex"
                                   :margin-bottom 20
                                   :gap 20
                                   }
                           }
                     [:> FormItem {:name "entrance"
                                   :rules [{:required true :message "введите номер подъезда"}]
                                   :style {:flex 1 :margin-bottom 0}}
                      [:> Input {:style {:font-family "'orchidea_light', sans-serif"
                                         :border-radius 10
                                         :border "1px solid #000000"
                                         :width "100%"
                                         :height 42}
                                 :defaultValue (get-in @app-state [:shipping_data :entrance])
                                 :onPressEnter (fn []
                                                 (.hideKeyboard web-app))
                                 :onChange (fn [value]
                                             (swap! app-state assoc-in [:shipping_data :entrance] (-> value .-target .-value)))
                                 :placeholder "Подъезд"}]]
                    
                     [:> FormItem {:name "floor"
                                   :rules [{:required true :message "введите этаж"}]
                                   :style {:flex 1 :margin-bottom 0}}
                      [:> Input {:style {:font-family "'orchidea_light', sans-serif"
                                         :border-radius 10
                                         :border "1px solid #000000"
                                         :width "100%"
                                         :height 42}
                                 :defaultValue (get-in @app-state [:shipping_data :floor])
                                 :onPressEnter (fn []
                                                 (.hideKeyboard web-app))
                                 :onChange (fn [value]
                                             (swap! app-state assoc-in [:shipping_data :floor] (-> value .-target .-value)))
                                 :placeholder "Этаж"}]]]
                    
                    
                    [:> FormItem {:name "intercom"
                                  :style {:margin-bottom 20}}
                     [:> Input {:style {:font-family "'orchidea_light', sans-serif"
                                        :border-radius 10
                                        :border "1px solid #000000"
                                        :width "100%"
                                        :height 42}
                                :defaultValue (get-in @app-state [:shipping_data :intercom])
                                :onPressEnter (fn []
                                                (.hideKeyboard web-app))
                                :onChange (fn [value]
                                            (swap! app-state assoc-in [:shipping_data :intercom] (-> value .-target .-value)))
                                :placeholder "Домофон"}]]
                    ]
                   
                )
        
          
        
          [:div {:style {:font-family "'orchidea_light', sans-serif"
                         :font-size 16
                         :margin-bottom 20}}
           "Комментарий"]
        
          [:> Input {:style {:font-family "'orchidea_light', sans-serif"
                             :border-radius 10
                             :border "1px solid #000000"
                             :width "100%"
                             :height 42
                             :margin-bottom 20}
                     :defaultValue (get-in @app-state [:shipping_data :comment])
                     :onPressEnter (fn []
                                     (.hideKeyboard web-app))
                     :onChange (fn [value]
                                 (swap! app-state assoc-in [:shipping_data :comment] (-> value .-target .-value)))
                     :placeholder "Комментарий к заказу"}]
        
        
          [:div {:style {:font-family "'orchidea', sans-serif"
                         :font-size 16
                         :margin-bottom 20}}
           "Способ оплаты"]
        
          [:> RadioGroup {:class-name "custom-radio"
                          :value @payment_type
                          :onChange (fn [e]
                                      (let [value (.. e -target -value)]
                                        (swap! app-state assoc-in [:shipping_data :payment_type] value))
                                      )
                          :style {:display "flex"
                                  :flex-direction "column"
                                  :gap "12px"
                                  :margin-bottom 42}
                          :options [{:label "Банковской картой CloudPayments / Долями" :value "cloudpayments" :class-name "custom-radio"}
                                    ]
                          }
           ]
        
          [:div {:style {:font-family "'orchidea_light', sans-serif"
                         :color "#777777"
                         :text-align "right"}}
           [:div {:style {:margin-bottom 5}} (str "Сумма: " @cart_summary " р.")]
           [:div {:style {:margin-bottom 5}} @shipping_city]
           
           [:div {:style {:margin-bottom 5}} (str "Доставка: " @shipping_price " р.")]
           ]
        
          [:div {:style {:font-family "'orchidea', sans-serif"
                         :text-align "right"}}
           (str "Итоговая сумма: " (+ @shipping_price @cart_summary) " р.")]
          ]
          ]
        ] 
       ] 
      )
    )
  )