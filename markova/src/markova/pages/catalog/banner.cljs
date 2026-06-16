(ns markova.pages.catalog.banner
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   [markova.events.banner-get :refer [banner_get]]
   )
  )


(defn banner []
  (banner_get {})
  (let [
        Image antd/Image
        Carousel antd/Carousel

        banners (reagent/cursor app-state [:banners])
        ]
    (fn []
      
      [:> Carousel {:dots false
                    :autoplay true
                    :autoplaySpeed 3000
                    :style {:width "100%"
                            :padding 0
                            :margin 0}
                    :effect "fade"
                    :className "custom-carousel"}
       
       (for [file (take 4 (:banner_images (first (filter (fn [banner] (= "main_page" (:banner_location banner))) @banners))))]
         [:> Image {:src (str "https://tg-market.qq-pp.ru/mankova/banners/" file)
                    :preview false
                    :style {:width "100%"
                            :height "auto"}}]
         ) 
       ]
      )
    )
  )