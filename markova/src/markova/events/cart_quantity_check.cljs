(ns markova.events.cart-quantity-check
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]
            [markova.events.payment-add :refer [payments_add]]
            )
  )



(defn cart_check_handler [[ok? response]]
  (swap! app-state assoc :cart (vec (map (fn [product] (:data product)) response)))
  (.hideProgress js/Telegram.WebApp.MainButton)
  (let [check (every?
               (fn [product]
                 (<= (:quantity product) (:stock_quantity product)))
               (:cart @app-state))
        ]
    (if check
      (case (:page @app-state)
        :cart (do 
                (swap! app-state assoc :page :shipping)
                (set! (.-href (.-location js/window)) "/mankova/#/shipping")
                )
        
        :shipping (do
                    (payments_add)
                    )
        )
      )
    )
  )


(defn cart_check []
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData
        ]
    (ajax/ajax-request
     {:uri (api_uri_maker "cart_get")
      :method :post
      :params {:telegram_user_id id}
      :handler cart_check_handler
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))