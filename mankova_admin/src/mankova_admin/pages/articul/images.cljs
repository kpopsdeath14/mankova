(ns mankova-admin.pages.articul.images
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.pages.articul.options :refer [options]]
   )
  )


(defn images []
  (let [
        articul_changes (reagent/cursor app-state [:articul_changes]) 
        adding_new_article? (reagent/cursor app-state [:adding_new_article?]) 
        Image antd/Image
        Button antd/Button]
    (fn []
      (if @adding_new_article?
        [:div {:style {:width "100%"
                       :margin-bottom 10
                       :aspect-ratio "1/1"
                       :border-radius "15px"
                       :background "#f0f0f0"
                       :display "flex"
                       :text-align "center"
                       :align-items "center"
                       :justify-content "center"
                       :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}}
         [:span {:style {:color "#666"
                         :font-size "16px"}}
          "Сначала создайте товар, потом загружайте изображения."]
         ]
        
        [:div {:style {:display "grid"
                       :grid-template-columns "repeat(4, 1fr)"
                       :gap "16px"
                       :margin-bottom 10}}
        
         [:div {:style {:grid-column "span 4"
                        :width "100%"
                        :height "100%"
                        :aspect-ratio "1/1"}}
          (if-let [img-url (first (:images @articul_changes))]
            [:> Image {:src (str "https://mankova.qq-pp.ru/mankova_img/img_raw/" img-url)
                       :preview false
                       :style {:width "100%"
                               :height "100%"
                               :aspect-ratio "1/1"
                               :border-radius "15px"
                               :object-fit "cover"
                               :border "1px solid #D3EAFF"}}]
            [:div {:style {:width "100%"
                           :height "100%"
                           :aspect-ratio "1/1"
                           :border-radius "15px"
                           :background "#f0f0f0"
                           :display "flex"
                           :align-items "center"
                           :justify-content "center"
                           :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}}
             [:span {:style {:color "#666"
                             :font-size "16px"}}
              "Изображение отсутствует"]])]
        
         (for [file (take 3 (rest (:images @articul_changes)))]
           [:> Image {:preview false
                      :src (str "https://mankova.qq-pp.ru/mankova_img/img_raw/" file)
                      :style {:width "100%"
                              :aspect-ratio "1/1"
                              :border-radius "15px"
                              :object-fit "cover"
                              :border "1px solid #D3EAFF"}}])
        
         [:> Button {:type "button"
                     :style {
                             :aspect-ratio "1/1"
                             :border "6px solid #D3EAFF"
                             :background "none"
                             :border-radius 15
                             :width "100%"
                             :height "100%"
                             :padding 0
                             :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"}
                     :onClick (fn []
                                (swap! app-state assoc :images_edit (:images (:articul_changes @app-state)))
        
                                (swap! app-state assoc :show_modal_picture_edit? true))}
          [:> Image {:src "image_plus.png"
                     :preview false
                     :style {:width "66%"
                             :aspect-ratio "1/1"}}]]]
        )
      )
    )
    )