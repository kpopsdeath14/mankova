(ns markova.events.user-get-init
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]))


(defn user_get_init_handler [[ok? response]]
  (let [
        main_button js/Telegram.WebApp.MainButton
        info (:user_get_init (first response))
        ] 
    
    (if (and (= "normis" (:user_status info)) (= "technical_work" (:app_state info)))
      (do
        (swap! app-state assoc :production false)
        (.hide main_button)
        )
      )
    )
  )


(defn user_get_init []
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "user_get_init")
      :method :post
      :params {:telegram_user_id id}
      :headers {"X-Telegram-InitData" init-data}
      :handler user_get_init_handler
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))
