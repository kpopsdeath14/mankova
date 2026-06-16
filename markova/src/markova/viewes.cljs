(ns markova.viewes
  (:require
   [reagent.core :as reagent]
   [markova.db :refer [app-state]]
   [reagent.ratom :as ratom]
   [markova.pages.catalog.catalog :refer [catalog_page]]
   [markova.pages.product.product :refer [product_page]]
   [markova.pages.cart.cart :refer [cart_page]]
   [markova.pages.shipping.shipping :refer [shipping_page]]
   [markova.pages.order-history.order-history :refer [order_history_page]]
   [markova.pages.order.order :refer [order_page]]
   [markova.pages.payment-description.payment-descriptions :refer [payment_description_page]]
   [markova.pages.delivery-description.delivery-description :refer [delivery_description_page]]
   [markova.pages.product.not-found :refer [product_not_found_page]]
   [markova.pages.payment-success.payment-success :refer [payment_success_page]]
   [markova.pages.information.information :refer [information]]
   [markova.pages.technical-works :refer [technical_works_page]]
   )
  )

(defmulti current-page #(@app-state :page))


(defmethod current-page :catalog []
  [catalog_page]
  )

(defmethod current-page :product []
  [product_page]
  )

(defmethod current-page :product_not_found []
  [product_not_found_page]
  )

(defmethod current-page :cart []
  [cart_page]
  )

(defmethod current-page :shipping []
  [shipping_page]
  )


(defmethod current-page :order_history []
  [order_history_page]
  )

(defmethod current-page :order []
  [order_page]
  )

(defmethod current-page :payment_description []
  [payment_description_page]
  )

(defmethod current-page :delivery_description []
  [delivery_description_page]
  )

(defmethod current-page :payment_success []
  [payment_success_page])

(defmethod current-page :information []
  [information])
