(ns mankova-admin.pages.login.login
  (:require 
   [reagent.core :as reagent] 
   [mankova-admin.events.user-get-init :refer [user_get_init]]
   [mankova-admin.db :refer [app-state]]
   [mankova-admin.local-storage :as ls]
   )
  )

(defn load-telegram-script []
  (let [script (.createElement js/document "script")]
    (set! (.-src script) "https://telegram.org/js/telegram-widget.js?22")
    (set! (.-async script) true)
    (.appendChild (.-body js/document) script)))


(defn telegram-login-button [{:keys [bot-name on-auth size request-access]}]
  (reagent/create-class
   {:component-did-mount
    (fn [this]
      (load-telegram-script)
      (let [props (reagent/props this)
            {:keys [on-auth bot-name size request-access]} props
            div (.getElementById js/document "telegram-login")
            ]
        
        (set! js/window.onTelegramAuth on-auth)
        
        (set! (.-innerHTML div)
              (str "<script async src=\"https://telegram.org/js/telegram-widget.js?22\" 
                   data-telegram-login=\"" (or bot-name "your_default_bot") "\" 
                   data-size=\"" (or size "large") "\" 
                   data-onauth=onTelegramAuth(user)
                   data-request-access=\"" (or request-access "write") "\"></script>"))))
    :reagent-render
    (fn []
      [:div {:style {:height "100vh"}}
       [:div {:id "telegram-login"
              :style {:display "flex"
                      :justify-content "center"
                      :align-items "center"
                      :height "100%"}}]]
      )
    }
    )
    )



(defn login_page []
  [telegram-login-button
   {:bot-name "mankova_admin_bot"
    :size "large"
    :request-access "write"
    :on-auth (fn [user]
               (let [user-data-hash (->> (js->clj user :keywordize-keys true)
                                         (sort-by key)
                                         (map (fn [[k v]] (str (name k) "=" v)))
                                         (clojure.string/join "\n")
                                         (js/encodeURIComponent))
                     ]
                 (swap! app-state assoc :user_data_hash user-data-hash)
                 (ls/set-item! "browser-hash-data" user-data-hash)
                 (ls/set-item! "telegram_user_id" (:id (js->clj user :keywordize-keys true)))
                 (user_get_init (:id (js->clj user :keywordize-keys true)))))}])