(ns markova.router
  (:import goog.history.Html5History
           goog.Uri)
  (:require
   [goog.events :as e]
   [markova.db :refer [app-state]]
   [secretary.core :as secretary :refer-macros [defroute]]
   [goog.history.EventType :as EventType]

   [reagent.core :as reagent]
   )
  )


(set! *warn-on-infer* true)

(defn hook-browser-navigation! []
  (doto (Html5History.)
    (e/listen
     EventType/NAVIGATE
     (fn [^js/Foo.Bar event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))



(defn routes []

  (secretary/set-config! :prefix "#")

  (defroute "/" []
    (swap! app-state assoc :page :catalog))

  (defroute "/catalog" []
    (swap! app-state assoc :page :catalog)
    )
  
  (defroute "/product/:vendor_code/:color" [vendor_code color] 
    (swap! app-state assoc :current_vendor_code (js/decodeURIComponent vendor_code))
    (swap! app-state assoc :current_color color)
    (swap! app-state assoc :page :product)
    )
  
  (defroute "/product-not-found" []
    (swap! app-state assoc :page :product_not_found))
  
  (defroute "/cart" []
    (swap! app-state assoc :page :cart)
    )
  
  (defroute "/shipping" []
    (swap! app-state assoc :page :shipping)
    )
  
  
  (defroute "/order-history" []
    (swap! app-state assoc :page :order_history)
    )
  
  (defroute "/order" []
    (swap! app-state assoc :page :order)
    )
  
  (defroute "/payment-description" []
    (swap! app-state assoc :page :payment_description)
    )
  
  (defroute "/delivery-description" []
    (swap! app-state assoc :page :delivery_description)
    )
  
  (defroute "/payment-success" []
    (swap! app-state assoc :page :payment_success)
    )
  
  (defroute "/information" []
    (swap! app-state assoc :page :information)
    )
  

  (hook-browser-navigation!))