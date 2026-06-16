(ns markova.events.cdek-calculate-shipping
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]
            [markova.events.cart-get :refer [cart_get]]
            [markova.events.cart-get-summary :refer [cart_get_summary]]))


(defn cdek_calulate_shipping_handler [[ok? response]]
  (swap! app-state assoc-in [:shipping_data :shipping_price] (:res response))
  )


(defn cdek_calulate_shipping [city_code]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "cdek_shipping_calculate")
      :method :post
      :params {:city_code city_code :telegram_user_id id :tarrif_code (case (:delivery_type (:shipping_data @app-state))
                                                                        "cdek" 136
                                                                        "courier" 137)
               }
      :handler cdek_calulate_shipping_handler
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))