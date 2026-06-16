(ns markova.events.moysklad-update
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]
            [markova.events.cart-quantity-check :refer [cart_check]]
            )
  )


(defn moysklad_upd_handler [[ok? response]]
  (cart_check)
  )


(defn moysklad_upd []
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData
        ]
    (ajax/ajax-request
     {:uri (api_uri_maker "moysklad_update")
      :method :post
      :params {:telegram_user_id id :filters (mapv (fn [product] {(keyword (:moysklad_entity_name (:product_attributes product))) (:moysklad_entity_href_id (:product_attributes product))}) (:cart @app-state))}
      :handler moysklad_upd_handler
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))