(ns mankova-admin.pages.product-edit.modal-product-editing
  (:require
   ["antd" :as antd]
   [mankova-admin.db :refer [app-state]]
   ["@ant-design/icons" :as icons]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.events.vendor-attribute-add :refer [vendor_attribute_add]]
   [mankova-admin.events.price-set :refer [price_set]] 
   [mankova-admin.events.product-filter-attribute-add :refer [product_filter_attribute_add]]
   )
  )

(defn modal_product_editing []
  (let [Modal antd/Modal
        Input antd/Input
        Select antd/Select
        Button antd/Button

        visible? (reagent/cursor app-state [:show_modal_product_editing?])
        filters (reagent/cursor app-state [:filters]) 
        product_edit_attribute_name (reagent/cursor app-state [:product_edit_attribute_name])
        current_vendor_changes (reagent/cursor app-state [:current_vendor_changes])
        selected_products (reagent/cursor app-state [:selected_products_product_edit])
        ] 
    (fn []
      [:> Modal
       {:visible @visible?
        :closable true
        :width "80vw"
        :style {:top "35%"
                :maxWidth "800px"}
        :onCancel (fn []
                    (swap! app-state assoc :show_modal_product_editing? false)
                    (swap! app-state assoc :current_vendor_changes {}))
    
        :footer (fn [_ {:keys [OkBtn CancelBtn]}]
                  (as-element
                   (let []
                     [:> Button
                      {:style {:background "#D3EAFF"
                               :color "black"
                               :height 50
                               :border-radius 15
                               :width "auto"
                               :font-size 24
                               :font-weight 300
                               :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"}
                       :disabled false
                       :onClick (fn []
                                  (cond
                                    (not (nil? (:price @current_vendor_changes))) (price_set (mapv (fn [product_id]
                                                                                                     {:product_id product_id
                                                                                                      :price (:price @current_vendor_changes)
                                                                                                      :price_type_name "moysklad"}) @selected_products))
    
                                    (not (nil? (:price_discount @current_vendor_changes))) (price_set (mapv (fn [product_id]
                                                                                                              {:product_id product_id
                                                                                                               :price (:price_discount @current_vendor_changes)
                                                                                                               :price_type_name "discount_price"}) @selected_products))
    
    
                                    (some #(= @product_edit_attribute_name %) ["name" "type" "categories" "collection" "vendor_code"])
                                    (vendor_attribute_add @product_edit_attribute_name (get-in @app-state [:current_vendor_changes (keyword (if (nil? (get-in @app-state [:current_vendor_changes (keyword @product_edit_attribute_name)]))
                                                                                                                                              (str @product_edit_attribute_name "_string")
                                                                                                                                              @product_edit_attribute_name))]))
    
                                    (= @product_edit_attribute_name "tags")
                                    (product_filter_attribute_add @selected_products "tags" (get-in @app-state [:current_vendor_changes (keyword (if (nil? (get-in @app-state [:current_vendor_changes :tags]))
                                                                                                                                                   "tags_string"
                                                                                                                                                   "tags"))])))
                                  (swap! app-state assoc :selected_products_product_edit [])
                                  (swap! app-state assoc :show_modal_product_editing? false))}
                      "Сохранить"])))}
    
       [:div {:style {:width "100%"
                      :padding 16
                      :boxSizing "border-box"}}
    
    
        (case @product_edit_attribute_name
          "vendor_code" [:> Input {:key "vendor-code-input-vendor_changes"
                                   :id "vendor-code-input-vendor_changes"
                                   :style {:width "100%"
                                           :height 50
                                           :border "1px solid #00247C"
                                           :border-radius 6
                                           :margin-bottom 12}
                                   :autoFocus true
                                   :defaultValue (:vendor_code @current_vendor_changes)
                                   :placeholder "Введите артикул"
                                   :onChange (fn [event]
                                               (let [value (.-value (.-target event))]
                                                 (swap! app-state
                                                        assoc-in
                                                        [:current_vendor_changes :vendor_code]
                                                        value)))}]
    
          "name" [:> Input {:key "vendor-code-input-vendor_changes"
                            :id "vendor-code-input-vendor_changes"
                            :style {:width "100%"
                                    :height 50
                                    :border "1px solid #00247C"
                                    :border-radius 6
                                    :margin-bottom 12}
                            :placeholder "Введите наименование товара"
                            :autoFocus true
                            :defaultValue (:name @current_vendor_changes)
                            :onChange (fn [event]
                                        (let [value (.-value (.-target event))]
                                          (swap! app-state
                                                 assoc-in
                                                 [:current_vendor_changes :name]
                                                 value)))}]
    
          "type" [:div
                  [:> Select {:style {:width "100%"
                                      :height 50
                                      :border "3px solid #D3EAFF"
                                      :border-radius 15
                                      :box-shadow "none"
                                      :margin-bottom 15}
                              :bordered false
                              :value (:type @current_vendor_changes)
                              :placeholder "Тип"
                              :options (let [type-data (first (filter #(= "type" (:attribute_name %)) @filters))]
                                         (map (fn [v] {:label v, :value v})
                                              (:attribute_values type-data)))
                              :onChange (fn [values]
                                          (swap! app-state
                                                 assoc-in
                                                 [:current_vendor_changes :type]
                                                 (js->clj values))
    
                                          (swap! app-state
                                                 assoc-in
                                                 [:current_vendor_changes :type_string]
                                                 nil))}]
    
                  [:> Input {:key "vendor-code-input-vendor_changes"
                             :id "vendor-code-input-vendor_changes"
                             :style {:width "100%"
                                     :height 50
                                     :border "1px solid #00247C"
                                     :border-radius 6}
                             :placeholder "Введите новый тип"
                             :defaultValue (:type_string @current_vendor_changes)
                             :onChange (fn [event]
                                         (let [value (.-value (.-target event))]
                                           (swap! app-state
                                                  assoc-in
                                                  [:current_vendor_changes :type_string]
                                                  value)
    
                                           (swap! app-state
                                                  assoc-in
                                                  [:current_vendor_changes :type]
                                                  nil)))}]]
    
    
          "categories"  [:div
    
                         [:> Select {:style {:width "100%"
                                             :height 50
                                             :border "3px solid #D3EAFF"
                                             :border-radius 15
                                             :box-shadow "none"
                                             :margin-bottom 15}
                                     :bordered false
                                     :value (:categories @current_vendor_changes)
                                     :placeholder "Категория"
                                     :mode "multiple"
    
                                     :options (let [type-data (first (filter #(= "categories" (:attribute_name %)) @filters))]
                                                (map (fn [v] {:label v, :value v})
                                                     (:attribute_values type-data)))
                                     :onChange (fn [values]
                                                 (swap! app-state
                                                        assoc-in
                                                        [:current_vendor_changes :categories]
                                                        (js->clj values))
    
                                                 (swap! app-state
                                                        assoc-in
                                                        [:current_vendor_changes :categories_string]
                                                        nil))}]
    
                         [:> Input {:key "vendor-code-input-vendor_changes"
                                    :id "vendor-code-input-vendor_changes"
                                    :style {:width "100%"
                                            :height 50
                                            :border "1px solid #00247C"
                                            :border-radius 6}
                                    :placeholder "Введите новую категорию"
                                    :defaultValue (:categories_str @current_vendor_changes)
                                    :onChange (fn [event]
                                                (let [value (.-value (.-target event))]
    
                                                  (swap! app-state
                                                         assoc-in
                                                         [:current_vendor_changes :categories_str]
                                                         value)
    
                                                  (swap! app-state
                                                         assoc-in
                                                         [:current_vendor_changes :categories_string]
                                                         (conj (vec (-> (:products_edit @app-state)
                                                                        first
                                                                        :categories)) value))
    
                                                  (swap! app-state
                                                         assoc-in
                                                         [:current_vendor_changes :categories]
                                                         nil)))}]]
    
          "collection"  [:div
                         [:> Select {:style {:width "100%"
                                             :height 50
                                             :border "3px solid #D3EAFF"
                                             :border-radius 15
                                             :box-shadow "none"
                                             :margin-bottom 15}
                                     :bordered false
                                     :value (:collection @current_vendor_changes)
                                     :placeholder "Коллекция"
    
                                     :options (let [type-data (first (filter #(= "collection" (:attribute_name %)) @filters))]
                                                (map (fn [v] {:label v, :value v})
                                                     (:attribute_values type-data)))
                                     :onChange (fn [values]
                                                 (swap! app-state
                                                        assoc-in
                                                        [:current_vendor_changes :collection]
                                                        (js->clj values))
    
                                                 (swap! app-state
                                                        assoc-in
                                                        [:current_vendor_changes :collection_string]
                                                        nil))}]
    
                         [:> Input {:key "vendor-code-input-vendor_changes"
                                    :id "vendor-code-input-vendor_changes"
                                    :style {:width "100%"
                                            :height 50
                                            :border "1px solid #00247C"
                                            :border-radius 6}
                                    :placeholder "Введите новую коллекцию"
                                    :defaultValue (:collection_string @current_vendor_changes)
                                    :onChange (fn [event]
                                                (let [value (.-value (.-target event))]
                                                  (swap! app-state
                                                         assoc-in
                                                         [:current_vendor_changes :collection_string]
                                                         value)
    
                                                  (swap! app-state
                                                         assoc-in
                                                         [:current_vendor_changes :collection]
                                                         nil)))}]]
    
          "tags"  [:div
                   [:> Select {:style {:width "100%"
                                       :height 50
                                       :border "3px solid #D3EAFF"
                                       :border-radius 15
                                       :box-shadow "none"
                                       :margin-bottom 15}
                               :bordered false
                               :value (:tags @current_vendor_changes)
                               :placeholder "Тег"
    
                               :options (let [type-data (first (filter #(= "tags" (:attribute_name %)) @filters))]
                                          (map (fn [v] {:label v, :value v})
                                               (:attribute_values type-data)))
                               :onChange (fn [values]
                                           (swap! app-state
                                                  assoc-in
                                                  [:current_vendor_changes :tags]
                                                  [(js->clj values)])
    
                                           (swap! app-state
                                                  assoc-in
                                                  [:current_vendor_changes :tags_string]
                                                  nil))}]
    
                   [:> Input {:key "vendor-code-input-vendor_changes"
                              :id "vendor-code-input-vendor_changes"
                              :style {:width "100%"
                                      :height 50
                                      :border "1px solid #00247C"
                                      :border-radius 6}
                              :placeholder "Введите новый тег"
                              :defaultValue (:tags_string @current_vendor_changes)
                              :onChange (fn [event]
                                          (let [value (.-value (.-target event))]
                                            (swap! app-state
                                                   assoc-in
                                                   [:current_vendor_changes :tags_string]
                                                   [value])
    
                                            (swap! app-state
                                                   assoc-in
                                                   [:current_vendor_changes :tags]
                                                   nil)))}]]
    
          "price" [:> Input {:key "vendor-code-input-vendor_changes"
                             :id "vendor-code-input-vendor_changes"
                             :style {:width "100%"
                                     :height 50
                                     :border "1px solid #00247C"
                                     :border-radius 6
                                     :margin-bottom 12}
                             :placeholder "Введите цену"
                             :defaultValue (:price @current_vendor_changes)
                             :onChange (fn [event]
                                         (let [value (.-value (.-target event))]
                                           (swap! app-state
                                                  assoc-in
                                                  [:current_vendor_changes :price]
                                                  value)))}]
    
          "price_discount" [:> Input {:key "vendor-code-input-vendor_changes"
                                      :id "vendor-code-input-vendor_changes"
                                      :style {:width "100%"
                                              :height 50
                                              :border "1px solid #00247C"
                                              :border-radius 6
                                              :margin-bottom 12}
                                      :placeholder "Введите цену со скидкой"
                                      :defaultValue (:price_discount @current_vendor_changes)
                                      :onChange (fn [event]
                                                  (let [value (.-value (.-target event))]
                                                    (swap! app-state
                                                           assoc-in
                                                           [:current_vendor_changes :price_discount]
                                                           value)))}]
    
    
    
          :else nil)]])
    )
  )