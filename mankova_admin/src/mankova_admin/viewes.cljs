(ns mankova-admin.viewes
  (:require
   [reagent.core :as reagent]
   [mankova-admin.db :refer [app-state]]
   [reagent.ratom :as ratom]
   [mankova-admin.pages.products.products :refer [products_page]]
   [mankova-admin.pages.product-edit.product-edit :refer [product_edit_page]]
   [mankova-admin.pages.articul.articul :refer [articul_page]]
   
   )
  )


(defmulti current-page #(@app-state :page))


(defmethod current-page :products []
  [products_page]
  )

(defmethod current-page :product_edit []
  [product_edit_page]
  )

(defmethod current-page :articul []
  [articul_page]
  )
