(ns markova.pages.technical-works
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   )
  )



(defn technical_works_page []
  (let [Space antd/Space]
    (fn []
      [:div {:style {:padding 26
                     :height "100vh"
                     :box-sizing "border-box"}}
       [:> Space {:direction "vertical"
                  :style {:border-radius 10
                          :width "100%"
                          :height "100%"
                          :padding 18
                          :border "1px solid black"
                          :display "flex"
                          :justify-content "center"
                          :text-align "center"}}

        [:div {:style {:font-family "'orchidea_light', sans-serif"
                       :white-space "pre-wrap"
                       :font-size 18}}
         "Ведутся технические работы. Скоро все заработает."
         ]
        ]
       ]
      )
      )
      )