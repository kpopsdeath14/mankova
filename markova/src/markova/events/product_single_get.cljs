(ns markova.events.product-single-get
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]
            )
  )


(defn product_single_get_handler [[ok? response]] 
  (if (nil? (:_result (first response)))
    (do
      (swap! app-state assoc :product_current "failure") 
      )
    (let [
          products_list_current (:product (:_result (first response)))
          products (:products products_list_current)
          current-color (:current_color @app-state)
          selected-product (if (or (nil? current-color) (not (some #(= % current-color) (distinct (map :color_translit products)))))
                             (do 
                               (swap! app-state assoc :current_color (:color_translit (first products))) 
                               (first products)
                               )
                             (first (filter #(= (:color_translit %) current-color) products))
                             )
          ] 
      (swap! app-state assoc :product_current selected-product)
      (swap! app-state assoc :products_list_current products_list_current) 
      (swap! app-state assoc :selected_size (:size (first (:sizes (:product_current @app-state)))))
      (swap! app-state assoc :current_product_id (:product_id (first (:sizes (:product_current @app-state)))))
      (swap! app-state assoc :current_sizes (vec (map (fn [size] {:value (:size size) :label (:size size)}) (:sizes (:product_current @app-state)))))
      
      )
    )
  )


(defn product_single_get [vendor_code product_color]
  (let [
        user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData
        ]
    (ajax/ajax-request
     {:uri (api_uri_maker "product_get_single")
      :method :post
      :params {:telegram_user_id id :vendor_code vendor_code :product_color product_color}
      :handler product_single_get_handler
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})
    )
  )
