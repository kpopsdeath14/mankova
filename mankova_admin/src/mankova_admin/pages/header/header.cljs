(ns mankova-admin.pages.header.header
  (:require
    ["antd" :as antd]
    ["@ant-design/icons" :as icons]
    [mankova-admin.db :refer [app-state]]
    [reagent.core :as reagent :refer [as-element]]
   )
  )


(defn header []
  (let [
        Layout antd/Layout
        Header (.-Header Layout)
        ]
    (fn [] 
      [:> Header {:class-name "custom-header"
                  :style {:background-color "white"
                          :display "flex" 
                          :flex-direction "row"
                          :overflow "hidden"
                          :flex-wrap "nowrap"
                          :align-items "center"
                          :justify-content "space-between"
                          :box-shadow "0 2px 8px rgba(0, 2, 5, 0.25)"
                          }}
       [:div {:style {:font-size 24
                      :font-weight 700
                      :overflow "hidden"
                      :white-space "nowrap"
                      :flex-wrap "nowrap"
                      }}
        "Редактировние каталога товаров"]
      
       [:div {:style {:font-size 36
                      :font-weight 900
                      }}
        "MANKOVA"]]
      )
    )
  )