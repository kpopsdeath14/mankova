(ns markova.events.cdek-search-pvz
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]
            [markova.events.cart-get :refer [cart_get]]
            [markova.events.cart-get-summary :refer [cart_get_summary]]))


(defn cdek_search_pvz_handler [[ok? response]] 
  (swap! app-state assoc :suggestions_pvz (:pvz response))
  )


(defn cdek_search_pvz []
  (let [
        user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData
        ]
    (ajax/ajax-request
     {:uri (api_uri_maker "cdek_search_pvz")
      :method :post
      :params {:search_text (:pvz_search_text @app-state) :city_code (:city_cdek_code @app-state) :telegram_user_id id}
      :handler cdek_search_pvz_handler
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))