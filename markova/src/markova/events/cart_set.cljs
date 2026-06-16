(ns markova.events.cart-set
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]
            [markova.events.cart-get :refer [cart_get]]
            [markova.events.cart-get-summary :refer [cart_get_summary]]
            )
  )


(defn cart_set_handler [[ok? response]]
  (cart_get)
  (cart_get_summary)
  )



(defn cart_set [product_id quantity summ]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData
        ]
    (ajax/ajax-request
     {:uri (api_uri_maker "cart_set")
      :method :post
      :params {:telegram_user_id id :product_id product_id :quantity quantity :summ summ}
      :handler cart_set_handler
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))