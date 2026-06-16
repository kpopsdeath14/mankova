(ns markova.events.payment-add
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [reagent.core :as reagent :refer [as-element]]
            [markova.apiurimaker :refer [api_uri_maker]]
            [goog.events :as events]
            [goog.net.EventType :as EventType]
            [markova.events.cart-get :refer [cart_get]]
            [markova.events.cart-get-summary :refer [cart_get_summary]]
            [markova.events.history-add :refer [history_add]]
            )
  )





 
(defn pay
   [payment-type {:keys [public-id description amount currency account-id invoice-id email skin data]
                  :or {currency "RUB" skin "mini"}}]
   
   (let [widget (js/cp.CloudPayments.)
         main_button js/Telegram.WebApp.MainButton]
     (swap! app-state assoc :payment_widget_opened? true)
 
     (.pay widget
           (clj->js payment-type)
           (clj->js {:publicId public-id
                     :description description
                     :amount amount
                     :currency currency
                     :accountId account-id
                     :invoiceId invoice-id
                     :email email
                     :skin skin
                     :data data
                     :autoClose 3
                     :jsonData {:cloudPayments {:cart (clj->js {:items (data :items)})}}})
           (clj->js {:onSuccess (fn [options] 
                                  (swap! app-state assoc :payment_widget_opened? false)
                                  (set! (.-href (.-location js/window)) "/mankova/#/payment-success")
                                  (swap! app-state assoc :page :payment_success)
                                  (cart_get)
                                  (cart_get_summary)
                                  )
                     
                     :onFail (fn [reason options] 
                               (swap! app-state assoc :payment_widget_opened? false)
                               (.show main_button)
                               )
                     }
                    )
           )
           )
           )
 
 (defn payments_add_handler [[ok? response]]
   (let [res response
         shipping_price (reagent/cursor app-state [:shipping_data :shipping_price])
         cart_summary (reagent/cursor app-state [:cart_summary])
         items-list (mapv (fn [item]
                            {:label (:name (:product_attributes item))
                             :price (:summ item)
                             :quantity (:quantity item)
                             :amount (* (:summ item) (:quantity item))})
                          (@app-state :cart))
         total-amount (+ @shipping_price @cart_summary)
         main_button js/Telegram.WebApp.MainButton]
     (swap! app-state assoc :current_payment_id (res :payment_id))
     (.hide main_button)

     (history_add 
      {:public-id "pk_5f93aea5a9475fc401fc4d8725b14"
       :description "Покупка товаров"
       :amount total-amount
       :currency "RUB"
       :account-id (res :user_id)
       :invoice-id (res :payment_id)
       :email "user@example.com"
       :skin "mini"
       :data {:items items-list
              :history_id (:history_id res)}}
      "cloud_payments"
      {:payment_id (res :payment_id)}
      )

     (pay "auth"
          {:public-id "pk_5f93aea5a9475fc401fc4d8725b14"
           :description "Покупка товаров"
           :amount total-amount
           :currency "RUB"
           :account-id (res :user_id)
           :invoice-id (res :payment_id)
           :email "user@example.com"
           :skin "mini" 
           :data {:items items-list
                  :history_id (:history_id res)
                  }
           }
          )
     )
     )
  


(defn payments_add []
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData
        shipping_data (case (:delivery_type (:shipping_data @app-state))
                        "cdek" 
                        (dissoc (:shipping_data @app-state) :street :home :flat :entrance :floor)
                        "courier" 
                        (dissoc (:shipping_data @app-state) :cdek_pvz_data) 
                        )
        ]
    (ajax/ajax-request
     {:uri (api_uri_maker "order_payments_add")
      :method :post
      :params {:telegram_user_id id :order_data (assoc shipping_data :summ (:cart_summary @app-state) :shipmentSumm (:shipping_price (:shipping_data @app-state)) :totalSumm (+ (:cart_summary @app-state) (:shipping_price (:shipping_data @app-state))))}
      :handler payments_add_handler
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))




