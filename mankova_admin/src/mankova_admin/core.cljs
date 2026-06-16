(ns mankova-admin.core
  (:require 
   ["antd" :as antd]
   [reagent.dom :as d]
   [mankova-admin.router :refer [routes]]
   [mankova-admin.db :refer [app-state]]
   [mankova-admin.viewes :refer [current-page]]
   [mankova-admin.pages.header.header :refer [header]] 
   [mankova-admin.events.user-get-init :refer [user_get_init]]
   [mankova-admin.pages.login.login :refer [login_page]]
   [mankova-admin.local-storage :as ls]
   
   [reagent.core :as reagent])
  )

(defn page_template []
  (let [
        Layout antd/Layout
        Content (.-Content Layout)
        ConfigProvider antd/ConfigProvider
        user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        
        development (reagent/cursor app-state [:development])
        login? (reagent/cursor app-state [:login?])
        ]
    (fn []
      [:> ConfigProvider {:theme {:components {:Button {}
                                               :Layout {:bodyBg "#ffffff"}
                                               :Select {}
                                               }
                                  }
                          :wave {:disabled true}
                          :token {:fontFamily "'AA Ordinar', sans-serif"}
                          }
       [:> Layout {:style {:overflow-y "hidden"}} 
        [header]
        [:> Content
         {:style 
          {:min-height "100vh"
           :padding "15px 7% 0 7%"
           }
          }
         (if @login?
           [login_page]

           (cond
             (= "normis" (:user_status @development))
             (str "попросите добавить вас в список администраторов, вот ваш айди - " (if (nil? user)
                                                                                       (ls/get-item "telegram_user_id")
                                                                                       (.-id user)
                                                                                       )
                  )
             
             (= "technical_work" (:app-state @development))
             "ведутся технические работы"
             
             :else
             [current-page]
             ) 
           ) 
         ]
        ]
       ]
       )
       )
       )

(defn mount-root [] 
  (routes)
  (d/render [page_template] (.getElementById js/document "app")))



(defn ^:export init! []
  (let [web-app (.-WebApp js/Telegram)
        user (.. js/Telegram -WebApp -initDataUnsafe -user) 
        start_param (.. js/Telegram -WebApp -initDataUnsafe -start_param)
        back-button (.-BackButton web-app)
        main_button js/Telegram.WebApp.MainButton 
        browser-hash-data (ls/get-item "browser-hash-data")
        ]
    (when browser-hash-data
      (swap! app-state assoc :user_data_hash browser-hash-data) 
      (user_get_init (ls/get-item "telegram_user_id"))
      ) 
    (if user 
      (user_get_init (.-id user))
      )
    (mount-root) 
    )
  )
