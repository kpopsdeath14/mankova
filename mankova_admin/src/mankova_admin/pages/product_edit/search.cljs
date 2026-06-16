(ns mankova-admin.pages.product-edit.search
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.events.product-get :refer [product_get]]
   )
  )






(defn search []
  (let [Button antd/Button
        products_edit (reagent/cursor app-state [:products_edit])
        ]
    (fn []
      [:> Button {
                 :id "catalog_search_input"
                 :size "large"
                 :placeholder "Наименование"
                 :style {:border "1px solid #00247C"
                         :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"
                         :border-radius 15
                         :height 50
                         :background "#D3EAFF"
                         :width "100%"
                         :text-align "left"
                         :font-size 24
                         :font-weight 300

                         :display "flex"
                         :justify-content "space-between"
                         :align-items "center"
                         :padding "0 16px"
                         }
                  :icon (as-element [:> icons/EditOutlined])
                  :iconPosition "end"
                 :onClick (fn []
                            (swap! app-state assoc :product_edit_attribute_name "name")
                            (swap! app-state assoc-in [:current_vendor_changes :name] (:name (first @products_edit)))
                            (swap! app-state assoc :show_modal_product_editing? true) 
                            )
                 }
       (:name (first @products_edit))
       ]
      )
    )
    )