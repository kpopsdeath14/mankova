(ns markova.pages.product.product
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [markova.pages.product.images :refer [images]]
   [markova.pages.product.characteristics :refer [characteristics]]
   [markova.events.product-single-get :refer [product_single_get]]
   [markova.pages.product.not-found :refer [product_not_found_page]]
   )
  )

(defn product_page []
  (product_single_get (:current_vendor_code @app-state) (:current_color @app-state))
  (let [
        Layout antd/Layout
        Content (.-Content Layout)

        product_current (reagent/cursor app-state [:product_current])
        ]
    (fn []
      (if (= "failure" @product_current)
        [product_not_found_page]
    
        [:> Layout
         {:style {:minHeight "100vh"}}
         [:> Content
          [images]
          [characteristics]]
         ]
        )
      ) 
    )
    )