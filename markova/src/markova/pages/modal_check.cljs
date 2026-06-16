(ns markova.pages.modal-check
  (:require
   ["antd" :as antd]
   [markova.db :refer [app-state]]
   ["@ant-design/icons" :as icons]
   [reagent.core :as reagent :refer [as-element]]
   [markova.events.attribute-add :refer [attribute_add]]
   )
  )

(defn modal_check [] 
  (let [
        web-app (.-WebApp js/Telegram)

        Modal antd/Modal
        Checkbox antd/Checkbox
        CheckboxGroup (.-Group Checkbox)

        policies_cookies (reagent/cursor app-state [:policies_cookies])
        visible? (reagent/cursor app-state [:show_modal?])
        ]
    (fn [] 
       [:> Modal
        {:title "Настройки конфиденциальности"
         :visible @visible?
         :closable false
         :class-name "custom-checkbox"
         :style {:top "35%"}
         :okButtonProps {:disabled (not= (count @policies_cookies) 2)} 
         :okText "Принять"
         :cancelText "Отмена"
         :onOk (fn []
                 (swap! app-state assoc :show_modal? false)
                 (attribute_add)
                 )
         :onCancel (fn []
                     (.close web-app)
                     )
         }
        
        [:> CheckboxGroup
         {:options [{:label (as-element
                             [:div
                              "Я даю согласие на обработку моих персональных данных в соответствии с "
                              [:span {:style {:text-decoration "underline"}
                                      :onClick (fn [e]
                                                 (if-not (nil? (:personal (:policies_links @app-state)))
                                                   (.openLink web-app (:personal (:policies_links @app-state)))) 
                                                 (.preventDefault e)
                                                 )
                                      }
                               "Политикой конфиденциальности"]
                              ]
                             )
                     :value "personal_data"
                     :style {:marginBottom "16px"}
                     }
        
                    {:label (as-element
                             [:div
                              "Я "
                              [:span {:style {:text-decoration "underline"}
                                      :onClick (fn [e] 
                                                 (if-not (nil? (:cookies (:policies_links @app-state)))
                                                   (.openLink web-app (:cookies (:policies_links @app-state)))
                                                   ) 
                                                 (.preventDefault e)
                                                 )
                                      }
                               "согласен"
                               ]
                              " на использование cookies и аналогичных технологий для улучшения работы сервиса"
                              ]
                             )
                     :value "cookies"}]
          :value @policies_cookies
          :onChange (fn [values]
                      (swap! app-state assoc :policies_cookies values))}]
        ]
      ) 
    )
  )