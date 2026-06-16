(ns markova.events.cart-get-summary
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]
            )
  )


(defn cart_get_summary_handler [[ok? response]]
  (swap! app-state assoc :cart_summary (:final_summ ((first response) :cart_get_summary)))
  )


(defn cart_get_summary []
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData
        ]
    (ajax/ajax-request
     {:uri (api_uri_maker "cart_get_summary")
      :method :post
      :params {:telegram_user_id id}
      :handler cart_get_summary_handler
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})}
     )
    )
  )