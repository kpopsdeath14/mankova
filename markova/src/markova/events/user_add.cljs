(ns markova.events.user-add
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]))


(defn user_add_handler [[ok? response]]
  )


(defn user_add []
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        init-data js/Telegram.WebApp.initData
        id (.-id user)]
    (ajax/ajax-request
     {:uri (api_uri_maker "user_add")
      :method :post
      :params {:telegram_user_id id}
      :handler user_add_handler 
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))