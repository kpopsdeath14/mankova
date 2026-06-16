(ns markova.core
    (:require 
     ["antd" :as antd]
     [reagent.core :as r] 
     [reagent.dom :as d]
     [clojure.string :as str]
     [clojure.set :as set]
     [markova.db :refer [app-state]]
     [markova.router :refer [routes]]
     [markova.viewes :refer [current-page]]
     [markova.events.cart-get :refer [cart_get]]
     [markova.events.user-add :refer [user_add]]
     [markova.events.user-attribute-get :refer [user_attribute_get]]
     [markova.events.product-get :refer [product_get]]
     [markova.events.product-single-get :refer [product_single_get]]
     [markova.events.cart-get-summary :refer [cart_get_summary]]
     [markova.events.moysklad-update :refer [moysklad_upd]]
     [markova.pages.modal-check :refer [modal_check]]
     [markova.events.attribute-add :refer [attribute_add]]
     [markova.events.user-get-init :refer [user_get_init]]
     [markova.events.policies-get :refer [policies_get]]
     
     [reagent.core :as reagent])
  )


(defn page_template []
  (let [
        Space antd/Space
        Button antd/Button
        Layout antd/Layout
        Content (.-Content Layout)
        ConfigProvider antd/ConfigProvider
        
        production (reagent/cursor app-state [:production])
        show_agreements? (reagent/cursor app-state [:show_agreements?])

        web-app (.-WebApp js/Telegram)
        ]
    (fn []
      [:> ConfigProvider {:theme {:components {:Button {:colorPrimaryHover nil
                                                        :colorPrimaryActive nil
                                                        :controlOutline nil
                                                        :controlOutlineWidth nil}
                                               :Layout {:bodyBg "#ffffff"}
                                               :Select {:colorBorder "#000000"
                                                        :hoverBorderColor "#000000"
                                                        :activeBorderColor "#000000"}}}
                          :wave {:disabled true}}
       [:> Layout {:style {:overflow-y "hidden"}}
        [:> Content
         {:style
          {:display "flex"
           :flex-direction "column"
           :min-height "100vh"}}
         [modal_check]

         (if @show_agreements?
           [:div {:style {:position :fixed
                          :bottom 0
                          :left 0
                          :right 0
                          :background-color "#f8f9fa"
                          :padding "15px"
                          :border-top "1px solid #dee2e6"
                          :z-index 1000}}
            
            [:div {:style {:white-space "pre-line"
                           :font-family "'orchidea_light', sans-serif"}}
             
             "Настройки конфиденциальности
                       
                               Продолжая использовать приложение вы даете:
                                  
                               Согласие на использование cookies и аналогичных
                               технологий для улучшения работы сервиса
                       "
             [:span {:style {:color "#777777"
                             :text-decoration "underline"}
                     :onClick (fn []
                                (swap! app-state assoc :page :information)
                                (set! (.-href (.-location js/window)) "/mankova/#/information"))}
              "Согласие"]
             
             " на обработку моих персональных данных в соответствии с "
             
             [:span {:style {:color "#777777"
                             :text-decoration "underline"}
                     :onClick (fn []
                                (swap! app-state assoc :page :information)
                                (set! (.-href (.-location js/window)) "/mankova/#/information"))}
              "Политикой конфиденциальности сервисов MANKOVA
                               
                               "]
             
             (let [link (:personal_data_agreement (:policies_links @app-state))]
               (if-not (nil? link)
                 [:span {:style {:color "#777777"
                                 :text-decoration "underline"}
                         :onClick (fn []
                                    (.openLink web-app link)
                                    )
                         }
                  "Согласие"]
                 "Согласие"
                 )
               
               )
             
             
             " на обработку моих персональных данных в соответствии с "


             (let [link (:personal_data (:policies_links @app-state))]
               (if-not (nil? link)
                 [:span {:style {:color "#777777"
                                 :text-decoration "underline"}
                         :onClick (fn []
                                    (.openLink web-app link)
                                    )
                         }
                  "Политикой конфиденциальности сервисов BIBI-ZEN"]
                 "Политикой конфиденциальности сервисов BIBI-ZEN"
                 )
               )

             ]
            
            [:> Button
             {:style {:font-family "'orchidea_light', sans-serif"
                      :border-radius 10
                      :background-color "#000"
                      :margin-top 20
                      :color "#fff"}
              :onClick (fn []
                         (attribute_add)
                         )}
             "Соглашаюсь"]]
           ) 
      
         (if @production
           [current-page]
           [:div {:style {:padding 26
                          :height "100vh"
                          :box-sizing "border-box"}}
            [:> Space {:direction "vertical"
                       :style {:border-radius 10
                               :width "100%"
                               :height "100%"
                               :padding 18
                               :border "1px solid black"
                               :display "flex"
                               :justify-content "center"
                               :text-align "center"}}
      
             [:div {:style {:font-family "'orchidea_light', sans-serif"
                            :white-space "pre-wrap"
                            :font-size 18}}
              "Ведутся технические работы. Скоро все заработает."]]])
      
      
         [:div {:style {:text-align "center"
                        :font-size "12px"
                        :position "sticky"
                        :margin-top "auto"
                        :padding-top 20 
                        :padding-bottom 20
                        :color "#777"}
                :onClick (fn []
                           (.openTelegramLink web-app "https://t.me/bibi_zen_bot")
                           )
                }
          "разработал BIBI-ZEN ©"]
         ]
         ]
         ] 
      
      )
    )
  )


(defn is-mobile? []
  (let [user-agent (.-userAgent (.-navigator js/window))
        mobile-regex #"(?i)android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini"]
    (boolean (re-find mobile-regex user-agent))))


(defn mount-root []
  (let [main_button js/Telegram.WebApp.MainButton
        back-button js/Telegram.WebApp.BackButton
        
        texting? (r/cursor app-state [:texting?])
        ]
    
    (add-watch app-state :page_listener (fn [key atom old-state new-state]
                                          
                                          (if-not (= (:page new-state) (:page old-state))
                                            (if-not (or (= :payment_description (:page old-state)) (= :product (:page old-state)) (= :delivery_description (:page old-state)))
                                              (swap! app-state assoc :prev_page (:page old-state))
                                              ) 
                                            )

                                          (if (= :catalog (:page new-state))
                                            (.hide back-button)
                                            (.show back-button)
                                            ) 
                                          
                                          (if (:show_modal? new-state) 
                                            (.hide main_button)
                                            (if (is-mobile?)
                                              (if (and (not @texting?) (not (empty? (:cart new-state))))
                                                (.show main_button)
                                                (.hide main_button)
                                                )
                                              (if (not (empty? (:cart new-state))) 
                                                (.show main_button)
                                                (.hide main_button)
                                                )
                                              ) 
                                            )
                                          
                                          (cond
                                            (= :catalog  (:page new-state)) (.setText main_button "Корзина")
                                            
                                            (= :product  (:page new-state)) (.setText main_button "Корзина")
                                            
                                            (= :cart     (:page new-state)) (.setText main_button "Оплатить") 

                                            (= :shipping (:page new-state)) (.setText main_button "Оплатить") 
                                            )
                                          )
               
               )
    ) 


  (routes)
  (d/render [page_template] (.getElementById js/document "app"))
  )


(def message (.-message antd))


(defn ^:export init! []
  (let [
        web-app (.-WebApp js/Telegram)
        user (.. js/Telegram -WebApp -initDataUnsafe -user)
        start_param (.. js/Telegram -WebApp -initDataUnsafe -start_param)
        back-button (.-BackButton web-app)
        main_button js/Telegram.WebApp.MainButton
        ]
    (policies_get)
    (user_get_init)
    (user_add)
    (user_attribute_get) 
    (cart_get)
    (cart_get_summary)
    (product_get "" [])
    (.setBottomBarColor web-app "#FFFFFF")
    (.setParams main_button #js {:color "#000000"
                                 :textColor "#FFFFFF"})
    (.setText main_button "Корзина")
    

    (if-not (nil? start_param)
      (if (= start_param "information")
        (swap! app-state assoc :page :information)

        (let [params (str/split start_param #"-1-1-1-")
              vendor_code (js/atob (first params))
              color (second params)]
          (swap! app-state assoc :current_vendor_code vendor_code)
          (swap! app-state assoc :current_color color)
          (swap! app-state assoc :page :product)
          )
        ) 
      ) 


    (.onClick main_button (fn []
      (.impactOccurred (.-HapticFeedback (.-WebApp js/Telegram)) "medium")
      
      (case (:page @app-state)
        :cart (do
                (.showProgress main_button)
                (moysklad_upd)
                )
        
        :information (do
                       (swap! app-state assoc :page :cart)
                       (set! (.-href (.-location js/window)) "/mankova/#/cart"))
        
        :product (do
                   (swap! app-state assoc :page :cart)
                   (set! (.-href (.-location js/window)) "/mankova/#/cart"))
        
        :catalog (do
                   (swap! app-state assoc :page :cart)
                   (set! (.-href (.-location js/window)) "/mankova/#/cart"))
        
        :shipping (let [required-fields #{:surname :first_name :mail :phone :city_cdek}]
                    (if-let [missing-fields (seq (remove #(get-in @app-state [:shipping_data %]) required-fields))] 
                      (do 
                        (.error message
                                (clj->js
                                 {:content (apply str (cons "Заполните обязательные поля: \n"
                                                            (interpose ", "
                                                                       (map #(% {:surname "фамилия"
                                                                                 :first_name "имя"
                                                                                 :mail "почта"
                                                                                 :phone "телефон"
                                                                                 :city_cdek "город"})
                                                                            missing-fields))))
                                  :style {:fontFamily "'orchidea_light', sans-serif"
                                          :white-space "pre-line"
                                          }
                                  }
                                 )
                                )
                        (.hideProgress main_button))
                      
                      (let [phone (get-in @app-state [:shipping_data :phone])
                            mail (get-in @app-state [:shipping_data :mail])
                            ]
                        (when (or (not (clojure.string/starts-with? phone "+7"))
                                  (not= (count phone) 12))
                          (.error message
                                  (clj->js
                                   {:content "Телефон должен быть в формате\n+7 (XXX) XXX-XX-XX (11 цифр)"
                                    :style {:fontFamily "'orchidea_light', sans-serif"
                                            :white-space "pre-line"}})) 
                          (.hideProgress main_button)
                          (throw (js/Error. "Неверный формат телефона")))
                        
                        (when (or (not (clojure.string/includes? mail "@"))
                                  (not (clojure.string/includes? mail ".")))
                          (.error message
                                  (clj->js
                                   {:content "Укажите корректный email адрес"
                                    :style {:fontFamily "'orchidea_light', sans-serif"
                                            :white-space "pre-line"}})) 
                          (.hideProgress main_button)
                          (throw (js/Error. "Неверный формат email")))
                        

                        (case (:delivery_type (:shipping_data @app-state))
                          "cdek" (if (not (:shipping_pvz @app-state))
                                   (do
                                     (.error message
                                             (clj->js
                                              {:content "Выберите пункт выдачи СДЭК"
                                               :style {:fontFamily "'orchidea_light', sans-serif"
                                                       :white-space "pre-line"}})) 
                                     (.hideProgress main_button))
                                   (do
                                     (.showProgress main_button)
                                     (moysklad_upd)))
                          
                          "courier" (let [courier-fields #{:street :home :flat :entrance :floor}]
                                      (if-let [missing (seq (remove #(get-in @app-state [:shipping_data %]) courier-fields))]
                                        (do
                                          (.error message
                                                  (clj->js
                                                   {:content "Заполните адрес доставки"
                                                    :style {:fontFamily "'orchidea_light', sans-serif"
                                                            :white-space "pre-line"}})) 
                                          (.hideProgress main_button))
                                        (do
                                          (.showProgress main_button)
                                          (moysklad_upd))))
                          
                          (do
                            (.error message
                                    (clj->js
                                     {:content "Выберите способ доставки"
                                      :style {:fontFamily "'orchidea_light', sans-serif"
                                              :white-space "pre-line"}})) 
                            (.hideProgress main_button)))
                        

)

) 
)
        
        :order_history (do
                         (swap! app-state assoc :page :cart)
                         (set! (.-href (.-location js/window)) "/mankova/#/cart")
                         )
        
        :order (do
                 (swap! app-state assoc :page :cart)
                 (set! (.-href (.-location js/window)) "/mankova/#/cart")))))


    (.onClick back-button (fn []
                            (case (:page @app-state)
                              :product (do
                                         (swap! app-state assoc :page (:prev_page @app-state))
                                         (case (:page @app-state)
                                           :catalog (set! (.-href (.-location js/window)) "/mankova/#/catalog")
                                           :cart (set! (.-href (.-location js/window)) "/mankova/#/cart"))
                                         (swap! app-state assoc :prev_page nil)
                                         ) 
                              
                              :cart (do 
                                      (swap! app-state assoc :page :catalog) 
                                      (set! (.-href (.-location js/window)) "/mankova/#/catalog")
                                      )
                              
                              :shipping (if-not (:payment_widget_opened? @app-state)
                                          (do
                                            (swap! app-state assoc :page :cart)
                                            (set! (.-href (.-location js/window)) "/mankova/#/cart")
                                            )
                                          ) 
                              
                              :payment_success (do
                                                 (swap! app-state assoc :page :catalog)
                                                 (set! (.-href (.-location js/window)) "/mankova/#/catalog")
                                                 )
                              
                              :order_history (do
                                               (swap! app-state assoc :page :catalog)
                                               (set! (.-href (.-location js/window)) "/mankova/#/catalog")
                                               )
                              
                              :order (do
                                       (swap! app-state assoc :page :order_history)
                                       (set! (.-href (.-location js/window)) "/mankova/#/order-history")
                                       )
                              
                              :delivery_description (do
                                                      (swap! app-state assoc :page :product)
                                                      (set! (.-href (.-location js/window)) (str "/mankova/#/product" (:current_vendor_code @app-state) "/" (:current_color @app-state)))
                                                      )
                              
                              :payment_description (do
                                                      (swap! app-state assoc :page :product)
                                                      (set! (.-href (.-location js/window)) (str "/mankova/#/product" (:current_vendor_code @app-state) "/" (:current_color @app-state)))
                                                      )
                              
                              :information (do
                                             (swap! app-state assoc :page :catalog)
                                             (set! (.-href (.-location js/window)) "/mankova/#/catalog")
                                             )
                              )
                            )
              )
    (mount-root)
    ) 
  )
