(ns markova.events.cdek-search-city
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]
            [markova.events.cart-get :refer [cart_get]]
            [markova.events.cart-get-summary :refer [cart_get_summary]]))


(defn cdek_search_city_handler [[ok? response]]
  (swap! app-state assoc :suggestions_city (:cities response))
  )


(defn cdek_search_city []
  (let [
        user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData
        ]
    (ajax/ajax-request
     {:uri (api_uri_maker "cdek_search_city")
      :method :post
      :params {:search_text (:city_search_text @app-state) :telegram_user_id id}
      :handler cdek_search_city_handler
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))