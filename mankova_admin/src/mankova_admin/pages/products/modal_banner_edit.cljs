(ns mankova-admin.pages.products.modal-banner-edit
  (:require
   ["antd" :as antd]
   [mankova-admin.db :refer [app-state]]
   ["@ant-design/icons" :as icons]
   [mankova-admin.api-uri-maker :refer [api_uri_maker]]
   [reagent.core :as reagent :refer [as-element]]
   [mankova-admin.events.banner-get :refer [banner_get]]
   [mankova-admin.events.banner-add :refer [banner_add]]
   [mankova-admin.events.banner-del :refer [banner_del]]
   )
  )

(defn vec_difference [v1 v2]
  (vec (remove (set v2) v1)))


(defn image_grid [imgs]
  (let [Row antd/Row
        Col antd/Col
        Upload antd/Upload
        Button antd/Button
        PlusOutlined icons/PlusOutlined
        DeleteOutlined icons/DeleteOutlined
        ArrowLeftOutlined icons/ArrowLeftOutlined
        ArrowRightOutlined icons/ArrowRightOutlined

        edit-mode? (reagent/cursor app-state [:product_pictures_edit_mode])
        images_edit (reagent/cursor app-state [:banner_images_edit])
        banners (reagent/cursor app-state [:banners])
        init-data js/Telegram.WebApp.initData

        images imgs
        main-image (first images)
        small-images (rest images)
        total-small-images (count small-images)
        need-plus? (< total-small-images 9)
        grouped-images (partition-all 3 small-images)


        delete-image (fn [img]
                       (swap! app-state assoc)
                       (let [new-images (vec (remove #{img} @images_edit))]
                         (swap! app-state assoc-in [:banner_images_edit] new-images)))

        move-image (fn [img direction]
                     (let [current-images (:banner_images_edit @app-state)
                           index (.indexOf current-images img)
                           new-index (case direction
                                       :left (max 0 (dec index))
                                       :right (min (dec (count current-images)) (inc index)))
                           new-images (-> current-images
                                          (assoc index (get current-images new-index))
                                          (assoc new-index img))]
                       (swap! app-state assoc-in [:banner_images_edit] new-images)))]

    [:> Row {:gutter [16 16]
             :style {:width "100%"}}
     [:> Col {:span 8
              :style {:paddingRight 16}}
      [:div {:style {:width "100%"
                     :position "relative"
                     :paddingBottom (if-not (nil? main-image)
                                      "100%")
                     :overflow "hidden"
                     :margin-bottom 16}}
       (if (nil? main-image)

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
           "Изображение отсутствует"]]

         [:img {:src (str "https://mankova.qq-pp.ru/banners/" main-image)
                :style {:position "absolute"
                        :top 0
                        :left 0
                        :border "1px solid #D3EAFF"
                        :width "100%"
                        :height "100%"
                        :objectFit "cover"
                        :borderRadius 8}}])
       (when @edit-mode?
         [:div {:style {:position "absolute"
                        :top 0
                        :left 0
                        :right 0
                        :bottom 0
                        :padding 8}}
          [:div {:style {:display "flex"
                         :justifyContent "space-between"}}
           [:div {:style {:background "rgba(0,0,0,0.5)"
                          :color "white"
                          :padding "4px 8px"
                          :borderRadius 4}}
            "1"]
           [:> Button {:icon (reagent/as-element [:> DeleteOutlined])
                       :shape "circle"
                       :danger true
                       :size "small"
                       :style {:background "rgba(0,0,0,0.5)"
                               :border "none"}
                       :onClick #(delete-image main-image)}]]

          [:div {:style {:position "absolute"
                         :bottom 8
                         :right 8}}
           [:> Button {:icon (reagent/as-element [:> ArrowRightOutlined])
                       :shape "circle"
                       :size "small"
                       :style {:background "rgba(0,0,0,0.5)"
                               :border "none"
                               :color "white"}
                       :onClick #(move-image main-image :right)}]]])]
      [:> Button {:style {:background "#D3EAFF"
                          :color "black"
                          :height 50
                          :border-radius 15
                          :width "100%"
                          :overflow "hidden"
                          :white-space "nowrap"
                          :font-size 24
                          :font-weight 300
                          :box-shadow "0 2px 8px rgba(0, 4, 6, 0.25)"}
                  :onClick (fn []
                             (if @edit-mode?
                               (do 
                                 (banner_add {
                                              :banner_id (:banner_id (first (filter (fn [banner] (= "main_page" (:banner_location banner))) @banners)))
                                              :banner_images (:banner_images_edit @app-state)
                                              } 
                                             )
                                 (banner_del (vec_difference (:old_banner @app-state) (:banner_images_edit @app-state)))
                                 )
                               )

                             (swap! app-state assoc :product_pictures_edit_mode (not @edit-mode?)))}
       (if @edit-mode? "Сохранить" "Редактировать")]]

     [:> Col {:span 16}
      (if (and (empty? small-images) need-plus?)
        [:> Row {:gutter [16 16]
                 :style {:marginBottom 16}}

         (if @edit-mode?
           [:> Col {:span 8}

            [:> Upload
             {:action (api_uri_maker "banner-picture-upload")
              :name "file"
              :accept "image/*"
              :multiple true
              :style {:width "100%"}
              :headers {"X-telegram-InitData" init-data
                        "X-Telegram-Hash" (:user_data_hash @app-state)
                        }
              :showUploadList true
              :maxCount (- 10 (count @images_edit))
              :data {:banner_location "main_page"}

              :onChange (fn [info]
                          (let [status (.-status info.file)
                                file-list (.-fileList info)
                                all-finished? (->> file-list
                                                   (map #(.-status %))
                                                   (every? #(or (= % "done") (= % "error"))))
                                ]


                            (when all-finished?
                              (let [successful-files (->> file-list
                                                          (filter #(= (.-status %) "done")))
                                    failed-files (->> file-list
                                                      (filter #(= (.-status %) "error")))
                                    ]
                                
                                (swap! app-state assoc-in [:banner_images_edit] (vec (concat (get-in @app-state [:banner_images_edit]) (mapv #(:filename (js->clj (.-response %) :keywordize-keys true)) file-list)))) 
                                )
                              )
                            )
                            )
                            }
             
             [:div {:style {:width "100%"
                            :position "relative"
                            :paddingBottom "100%"
                            :border "1px dashed #d9d9d9"
                            :borderRadius 4
                            :cursor "pointer"}}
              [:div {:style {:position "absolute"
                             :top 0
                             :left 0
                             :right 0
                             :bottom 0
                             :display "flex"
                             :alignItems "center"
                             :justifyContent "center"}}
               [:> PlusOutlined {:style {:fontSize "24px"
                                         :color "#999"}}]]]]])]

        (for [[row-idx row-images] (map-indexed vector grouped-images)]
          ^{:key row-idx}
          [:> Row {:gutter [16 16]
                   :style {:marginBottom 16}}
           (for [[col-idx img] (map-indexed vector row-images)]
             (let [img-num (+ 2 (* row-idx 3) col-idx)]
               ^{:key col-idx}
               [:> Col {:span 8}
                [:div {:style {:width "100%"
                               :position "relative"
                               :paddingBottom "100%"}}
                 [:img {:src (str "https://mankova.qq-pp.ru/banners/" img)
                        :style {:position "absolute"
                                :top 0
                                :border "1px solid #D3EAFF"
                                :left 0
                                :width "100%"
                                :height "100%"
                                :objectFit "cover"
                                :borderRadius 4}}]
                 (when @edit-mode?
                   [:div {:style {:position "absolute"
                                  :top 0
                                  :left 0
                                  :right 0
                                  :bottom 0
                                  :padding 8}}
                    [:div {:style {:display "flex"
                                   :justifyContent "space-between"}}
                     [:div {:style {:background "rgba(0,0,0,0.5)"
                                    :color "white"
                                    :padding "4px 8px"
                                    :borderRadius 4}}
                      (str img-num)]
                     [:> Button {:icon (reagent/as-element [:> DeleteOutlined])
                                 :shape "circle"
                                 :danger true
                                 :size "small"
                                 :style {:background "rgba(0,0,0,0.5)"
                                         :border "none"}
                                 :onClick #(delete-image img)}]]
                    [:div {:style {:position "absolute"
                                   :bottom 8
                                   :left 8
                                   :right 8
                                   :display "flex"
                                   :justifyContent "space-between"}}
                     [:> Button {:icon (reagent/as-element [:> ArrowLeftOutlined])
                                 :shape "circle"
                                 :size "small"
                                 :style {:background "rgba(0,0,0,0.5)"
                                         :border "none"
                                         :color "white"}
                                 :onClick #(move-image img :left)}]
                     [:> Button {:icon (reagent/as-element [:> ArrowRightOutlined])
                                 :shape "circle"
                                 :size "small"
                                 :style {:background "rgba(0,0,0,0.5)"
                                         :border "none"
                                         :color "white"}
                                 :onClick #(move-image img :right)}]]])]]))

           (when (and need-plus? (= row-idx (dec (count grouped-images))))
             (let [remaining (mod total-small-images 3)
                   empty-slots (if (zero? remaining) 2 (dec (- 3 remaining)))]
               (for [i (range empty-slots)]
                 ^{:key (str "empty-" i)}
                 [:> Col {:span 8}])

               (if @edit-mode?
                 [:> Col {:span 8}

                  [:> Upload
                   {:action (api_uri_maker "banner-picture-upload")
                    :name "file"
                    :accept "image/*"
                    :multiple true
                    :style {:width "100%"}
                    :headers {"X-telegram-InitData" init-data
                              "X-Telegram-Hash" (:user_data_hash @app-state)
                              }
                    :showUploadList true
                    :maxCount (- 10 (count @images_edit))
                    :data {:banner_location "main_page"}
                  
                    :onChange (fn [info]
                                (let [status (.-status info.file)
                                      file-list (.-fileList info)
                                      all-finished? (->> file-list
                                                         (map #(.-status %))
                                                         (every? #(or (= % "done") (= % "error"))))]
                  
                  
                                  (when all-finished?
                                    (let [successful-files (->> file-list
                                                                (filter #(= (.-status %) "done")))
                                          failed-files (->> file-list
                                                            (filter #(= (.-status %) "error")))]
                  
                                      (swap! app-state assoc-in [:banner_images_edit] (vec (concat (get-in @app-state [:banner_images_edit]) (mapv #(:filename (js->clj (.-response %) :keywordize-keys true)) file-list))))))))}
                  
                   [:div {:style {:width "100%"
                                  :position "relative"
                                  :paddingBottom "100%"
                                  :border "1px dashed #d9d9d9"
                                  :borderRadius 4
                                  :cursor "pointer"}}
                    [:div {:style {:position "absolute"
                                   :top 0
                                   :left 0
                                   :right 0
                                   :bottom 0
                                   :display "flex"
                                   :alignItems "center"
                                   :justifyContent "center"}}
                     [:> PlusOutlined {:style {:fontSize "24px"
                                               :color "#999"}}]]]]
                                               ]
                                               )
                                               )
                                               )
                                               ]
                                               )
                                               )
                                               ]
                                               ]
                                               )
                                               )


(defn modal_banner_edit []
  (banner_get {})
  (let [Modal antd/Modal
        visible? (reagent/cursor app-state [:show_modal_banner_edit?])
        banners (reagent/cursor app-state [:banners])
        images_edit (reagent/cursor app-state [:banner_images_edit])
        ]
    (fn []
      [:> Modal
       {:visible @visible?
        :closable true
        :footer nil
        :width "80vw"
        :style {:maxWidth "800px"}
        :onCancel #(swap! app-state assoc :show_modal_banner_edit? false)}

       [:div {:style {:width "100%"
                      :padding 16
                      :boxSizing "border-box"}}
        [image_grid @images_edit]
        ]
       ]
      )
    )
  )