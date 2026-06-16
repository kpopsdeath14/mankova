(ns markova.db
  (:require
   [reagent.core :as r]
   )
  )

(defonce app-state (r/atom {:production true
                            :page :catalog
                            :image_preview_visible? false
                            :side_menu_open false
                            :cart []
                            :payment_widget_opened? false
                            :order_history [{} {}]
                            :products []
                            :filters_picked {}
                            :selected_size {}
                            :current_sizes []
                            :product_current {} 
                            :show_agreements? false
                            :products_list_current []
                            :visible_count 20
                            :loading? false 
                            :to_scroll false
                            :prev_page :catalog 
                            :show_modal? false
                            :texting? false

                            :shipping_data {:payment_type "cloudpayments"
                                            :shipping_price 0
                                            :delivery_type "cdek"
                                            }
                            }
                           )
  )