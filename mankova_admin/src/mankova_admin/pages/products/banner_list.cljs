(ns mankova-admin.pages.products.banner-list
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [mankova-admin.db :refer [app-state]]
   [mankova-admin.api-uri-maker :refer [api_uri_maker]]
   [reagent.core :as reagent :refer [as-element]])
  )

(defn banner_list []
  (let [
        Image antd/Image
        Button antd/Button

        banners (reagent/cursor app-state [:banners]) 
        
        ]
    (fn []
      [:div {:style {
                     :height "110px"
                     :box-sizing "border-box"
                     :display "flex"
                     :justify-content "space-between"
                     :padding "15px 30px"
                     :border-radius "15px"
                     :box-shadow "0 2px 8px 0 rgba(0, 0, 0, 0.1)"
                     :align-items "center"
                     :margin-bottom 25
                     :overflow "hidden"
                     :flex-wrap "nowrap"
                     }
             :onClick (fn []
             
                        (swap! app-state assoc :old_banner (:banner_images (first (filter (fn [banner] (= "main_page" (:banner_location banner))) @banners))))
                        (swap! app-state assoc :banner_images_edit (:banner_images (first (filter (fn [banner] (= "main_page" (:banner_location banner))) @banners))))
             
                        (swap! app-state assoc :show_modal_banner_edit? true))
             }
       [:div {:style {:font-size 20
                      :font-weight 700
                      }}
        "Баннеры на главной странице"
        ]
       
       [:div {:style {:display "flex"
                      :gap "8px"
                      :overflow "hidden"
                      :flex-wrap "nowrap"
                      } 
              }
        
        (for [file (take 4 (:banner_images (first (filter (fn [banner] (= "main_page" (:banner_location banner))) @banners))))]
          [:> Image {:preview false
                     :src (str "https://mankova.qq-pp.ru/banners/" file)
                     :style {:height "80px"
                             :width "80px"
                             :margin-left "0px"
                             :border "1px solid #D3EAFF"
                             :border-radius "15px"
                             :object-fit "cover"}
                     }
           ]
          )
        [:> Button {:type "button"
                    :style {:background "none"
                            :height "80px" 
                            :border-radius "15px"
                            :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                            :width "80px"
                            :border "6px solid #D3EAFF"
                            }
                    :onClick (fn [])}
         [:> Image {:src "image_plus.png"
                    :preview false
                    :style {:height "40px"
                            :width "40px"
                            }
                    }]
         ]
        ]
       
       
       ]
      )
    )
  )