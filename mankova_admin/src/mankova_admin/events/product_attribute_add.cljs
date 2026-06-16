(ns mankova-admin.events.product-attribute-add
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]
            [mankova-admin.events.filters-get :refer [filters_get]]
            [mankova-admin.events.articul-get :refer [articul_get]]
            ))


(defn attribute_add_handler [[ok? response]] 
  (filters_get [])
  (set! (.-href (.-location js/window)) (str "/#/articul/" (:id (:articul_changes @app-state))))
  (swap! app-state assoc :articul_changes {})
  (swap! app-state assoc :articul_editing? false)
  (swap! app-state assoc :adding_new_article? false)
  )



(defn transform_filters [m product_id]
  (->> m
       (mapv (fn [[k v]] {:product_id product_id :attribute_name (name k) :attribute_value v}))))



(defn attribute_add [product_id] 
   (let [current-changes (:articul_changes @app-state)
         fields (if (some #(= % "images") (:options_to_fill @app-state))
                  [:id :vendor_code :categories :name :product_description :made_of :product_care_info :model_on_picture_parameters :product_measurements :color :type :size :collection :images :tags :tilda_external_id]
                  [:id :vendor_code :categories :name :product_description :made_of :product_care_info :model_on_picture_parameters :product_measurements :color :type :size :collection :tags :tilda_external_id]
                  )
         valid_articul (->> fields
                            (map (fn [field]
                                   (let [k (keyword field)
                                         v (get current-changes k)]
                                     (when v [k v])))) 
                            (into {}))
         
         
         user (.. js/Telegram -WebApp -initDataUnsafe -user)
         ;id (.-id user)
         init-data js/Telegram.WebApp.initData
         ]
     
     

     (ajax/ajax-request
      {:uri (api_uri_maker "product-attribute-add")
       :method :post
       :params {:attributes (transform_filters (if (:adding_new_article? @app-state)
                                                 (assoc valid_articul :actual "f")
                                                 valid_articul
                                                 )
                                               product_id)
                :refresh? (:adding_new_article? @app-state)
                }
       :handler attribute_add_handler
       :headers {"X-telegram-InitData" init-data
                 "X-Telegram-Hash" (:user_data_hash @app-state)
                 }
       :format (ajax/json-request-format)
       :response-format (ajax/json-response-format {:keywords? true})})
     ) 
  )