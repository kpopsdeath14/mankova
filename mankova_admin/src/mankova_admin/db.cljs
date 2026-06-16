(ns mankova-admin.db
  (:require
   [reagent.core :as r]))

(defonce app-state (r/atom {
                            :login? true
                            :products [{} {} {} {}]
                            :products_edit [{} {} {} {}]
                            :page :products
                            :show_modal_banner_edit? false
                            :show_modal_product_add? false
                            :articul_editing? false
                            :selected_products_catalog []
                            :selected_products_product_edit []
                            :filters_picked {}
                            :products_mode "catalog"
                            :products_edit_mode "catalog"
                            :articul_changes {}
                            :adding_new_article? false
                            :adding_new_product? false 
                            :show_modal_product_editing? false
                            :show_modal_fill_analogy? false
                            :product_edit_attribute_name "vendor_code" 
                            :current_vendor_changes {}
                            :options_to_fill []
                            :banner_edit_mode false
                            :banners []
                            :banner_images_edit []
                            }
                           )
  )