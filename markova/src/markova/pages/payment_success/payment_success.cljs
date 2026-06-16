(ns markova.pages.payment-success.payment-success
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   )
  )



(defn payment_success_page []
  (let [
        web-app (.-WebApp js/Telegram)

        Space antd/Space
        ]
    (fn []
      [:div {:style {:padding 26
                     :height "100vh"
                     :box-sizing "border-box"
                     }
             }
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
                       :font-size 18
                       }
               }
         "Спасибо за ваш заказ!\n\nВ ближайшее время вы получите сообщение в личные сообщения с ботом в телеграмме со всеми данными заказка.\n\nЕсли по каким-то причинам сообщение не пришло или у вас остались вопросы, пожалуйста, свяжитесь с нами:\n\n"
         
         [:span {:style {:color "#777777"
                         :text-decoration "underline"}
                 :onClick (fn []
                            (.openLink web-app "https://wa.me/79002031213")
                            )
                 }
          "WhatsApp"]
         
         " или "
         
         [:span {:style {:color "#777777"
                         :text-decoration "underline" 
                         }
                 :onClick (fn []
                            (.openTelegramLink web-app "https://t.me/mankova_officialbot")
                            )
                 }
          "Telegram"]
         "\n"
         "e-mail: store@mankova.ru"
         ]
        ]
  
        
  
        ]))
  )