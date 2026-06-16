(ns markova.pages.delivery-description.delivery-description
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]))


(defn delivery_description_page []
  (let [Space antd/Space
        Divider antd/Divider
        Collapse antd/Collapse

        PlusOutlined icons/PlusOutlined]
    (fn []
      [:div {:style {:padding 26
                     :box-sizing "border-box"}}
       [:> Space {:direction "vertical"
                  :style {:border-radius 10
                          :width "100%"
                          :height "100%"
                          :padding 18
                          :display "flex"
                          :justify-content "center"
                          :text-align "center"}}
        [:div {:style {:font-size 18
                       :font-family "'orchidea_light', sans-serif"
                       :white-space "pre-line"}}
         "ДОСТАВКА"]

        [:div {:style {:margin-left 10
                       :margin-right 10
                       :margin-top 20}}
         [:> Divider {:style {:borderColor "#d9d9d9"}}]]

        [:> Collapse {:style {:margin-left 10
                              :margin-right 10
                              :border "none"
                              :background-color "white"}
                      :defaultActiveKey "fabric"
                      :expandIconPosition "end"
                      :accordion true
                      :expandIcon (fn [panel-props]
                                    (as-element
                                     [:div {:style {:display "flex"
                                                    :align-items "center"
                                                    :justify-content "center"
                                                    :height "100%"}}
                                      [:> PlusOutlined {:style {:fontSize 16
                                                                :transform (if (.-isActive panel-props)
                                                                             "rotate(45deg)"
                                                                             "rotate(0deg)")
                                                                :transition "transform 0.3s"
                                                                :color "#777777"}}]]))
                      :items [{:label (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                 :height 40
                                                                 :display "flex"
                                                                 :align-items "center"}}
                                                   "Доставка"])
                               :children (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                    :text-align "left"
                                                                    :white-space "pre-wrap"}}
                                                      "По России:

Осуществляется транспортной компанией СДЕК. Стоимость доставки рассчитывается автоматически при оформлении заказа. Срок сборки и отправки заказа составляет 1-3 рабочих дня. Срок доставки от 1 до 5 рабочих дней.

Бесплатная доставка осуществляется при заказе от 20 000 рублей. Заказ может быть отправлен несколькими посылками из разных городов.

За пределы России:

Осуществляется Почтой России.
Стоимость и сроки доставки рассчитываются индивидуально."])
                               :key "fabric"}
                              
                              {:label (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                 :height 40
                                                                 :display "flex"
                                                                 :align-items "center"}}
                                                   "Возврат"])
                               :key "parameters"
                               :children (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                    :text-align "left"
                                                                    :white-space "pre-wrap"}}
                                                      "Если изделие вам не подошло, его можно вернуть в течение 7 дней с момента получения заказа из интернет-магазина согласно Закону РФ «О защите прав потребителей». Для этого оно не должно иметь следов использования, бирки должны быть на месте. В случае возврата заказа, вы оплачиваете стоимость обратной доставки. Возврат чулочно-носочных изделий возможен только если упаковка не вскрывалась и изделия не примерялись.

Если вам необходимо оформить возвратную накладную, пожалуйста, свяжитесь с нами в WhatsApp или Telegram.

После получения посылки возврат обрабатывается в течение 10 рабочих дней. После обработки возврата денежные средства возвращаются на карту, с которой заказ был оплачен на сайте. Обращаем ваше внимание, что срок поступления денежных средств зависит от скорости обработки операции вашим банком и может составлять до 30 банковских дней.

Чтобы вернуть изделие, вам необходимо заполнить заявление на возврат и приложить его к отправляемому заказу. Возврат товара осуществляется через транспортную компанию CDEK до пункта выдачи. Адрес для возврата: ИП Маньков А.Ю., г. Нижний Тагил, Уральский пр. д. 71."])}
                              ]
                              }
                              ]
        [:div {:style {:margin-left 10
                       :margin-right 10}}
         [:> Divider {:style {:borderColor "#d9d9d9"
                              :margin-top "0px !important"}}]]]])))