(ns mankova-admin.router
  (:import goog.history.Html5History
           goog.Uri)
  (:require
   [goog.events :as e]
   [mankova-admin.db :refer [app-state]]
   [secretary.core :as secretary :refer-macros [defroute]]
   [goog.history.EventType :as EventType]
   [mankova-admin.events.product-get :refer [product_get]]
   [mankova-admin.events.articul-get :refer [articul_get]]

   [reagent.core :as reagent]))


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
    (swap! app-state assoc :page :products))

  (defroute "/catalog" []
    (swap! app-state assoc :page :products)) 
  
  (defroute "/product-edit/:vendor_code" [vendor_code]
    (let [decoded-vendor-code (js/decodeURIComponent vendor_code)]
      (product_get (assoc {}
                          :vendor_code [decoded-vendor-code]
                          :actual (case (:products_mode @app-state)
                                    "catalog" ["t" "true"]
                                    "archive" ["f" "false"])) "")
      (swap! app-state assoc :page :product_edit)
      (swap! app-state assoc :current_vendor_code decoded-vendor-code)))
  
  (defroute "/articul/:articul" [articul]
    (swap! app-state assoc :page :articul)
    (articul_get {:id [articul]})
    )
  

  (hook-browser-navigation!))