(ns markova.pages.product.images
  (:require
   ["antd" :as antd]
   [markova.db :refer [app-state]]
   ["@ant-design/icons" :as icons]
   ["react-photo-view" :as photo_review]
   [reagent.core :as reagent :refer [as-element]]
   [clojure.string :as str]
   )
  )



(defn images []
  (let [Skeleton antd/Skeleton
        SkeletonNode (.-Node Skeleton)

        Image antd/Image
        Carousel antd/Carousel
        
        product_current (reagent/cursor app-state [:product_current])
        ]
    (fn []
      [:div {:on-touch-start (fn [] (.disableVerticalSwipes (.-WebApp js/Telegram)))
             :on-touch-end (fn [] (.enableVerticalSwipes (.-WebApp js/Telegram)))}
       

       [:> Carousel {:arrows true
                     :dots false
                     :infinite false
                     :style {:width "100%"
                             :height "auto"
                             :padding 0
                             :margin 0}}
       
        (if-not (and (= (@product_current :color_translit) (:current_color @app-state)) (= (:vendor_code (@app-state :products_list_current)) (:current_vendor_code @app-state)))
          [:div {:style {:position "relative"
                         :height "100vw"
                         :width "100vw"
                         :display "flex"
                         :justify-content "center"
                         :align-items "center"}}
           [:> SkeletonNode
            {:style {:height "100vw"
                     :width "100vw"}
             :active true}]]
          (map (fn [src]
                 [:> Image
                  {:src src
                   :preview false
                   :onClick (fn []
                              (swap! app-state assoc :photo_slider_index 0)
                              (swap! app-state assoc :image_preview_visible? true))
                   :style {:width "100%"
                           :prefix {:mask nil}
                           :marginBottom "16px"
                           :touch-action "pan-x pinch-zoom"}
       
                   :placeholder (as-element
                                 (fn []
                                   [:> SkeletonNode
                                    {:style {:height "100vw"
                                             :width "100vw"}
                                     :active true}]))}])
       
               (vec (map (fn [images_src] (str "https://tg-market.qq-pp.ru/mankova/mankova_img/img_raw/"
                                               images_src)) (@product_current :images)))))]
       ]
      )
    )
  )