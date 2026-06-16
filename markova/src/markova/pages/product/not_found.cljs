(ns markova.pages.product.not-found
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]))


(defn product_not_found_page []
  (let [Space antd/Space
        Button antd/Button
        ]
    (fn []
      [:div {:style {:padding 26
                     :height "100vh"
                     :box-sizing "border-box"}}
       [:> Space {:direction "vertical"
                  :style {:border-radius 10
                          :border "1px solid #000000"
                          :width "100%"
                          :height "100%"
                          :padding 18
                          :display "flex"
                          :flex-direction "column"
                          :align-items "center"
                          :justify-content "center"
                          :text-align "center"}}
        [:div {:style {:font-size 18
                       :font-family "'orchidea_light', sans-serif"
                       :white-space "pre-line"
                       :margin-bottom 16
                       }}
         "Такого товара не существует" 
         ]
        [:> Button {:onClick (fn []
                               (swap! app-state assoc :page :catalog))
                    :style {:font-size 18
                            :font-family "'orchidea_light', sans-serif"}}
         "Вернуться в каталог"]
        ]
        ]
        )
        )
        )