(ns markova.pages.payment-description.payment-descriptions
  (:require
   ["antd" :as antd]
   ["@ant-design/icons" :as icons]
   [markova.db :refer [app-state]]
   [reagent.core :as reagent :refer [as-element]]
   )
  )


(defn payment_description_page []
  (let [
        Space antd/Space
        Divider antd/Divider
        Collapse antd/Collapse

        PlusOutlined icons/PlusOutlined
        ]
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
         "ОПЛАТА"
         ]
        
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
                                                   "Оплата банковскими картами:"])
                               :children (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                    :text-align "left"
                                                                    :white-space "pre-wrap"
                                                                    }}
                                                      "В интернет-магазине вы можете расплатиться картой российских банков.
Если оплата прошла успешно, вам на почту придет письмо с уведомлением о том, что заказ принят."])
                               :key "fabric"}
                              
                              {:label (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                 :height 40
                                                                 :display "flex"
                                                                 :align-items "center"}}
                                                   "Оплата Яндекс Сплит"])
                               :key "sizes"
                               :children (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                    :text-align "left"
                                                                    :white-space "pre-wrap"
                                                                    }}
                                                      "Сумма онлайн-покупок не может превышать порог 150 000 рублей, покупок может быть несколько. Разбивайте оплату покупок на 2, 4 или 6 месяцев. Первый платеж спишется сразу, остальные — по графику раз в 2 недели. Заказ вы получаете уже после первой оплаты.

Описание процесса платежа через сервис «Яндекс Сплит»:
                                                       
Сформируйте корзину с покупками на сайте. Выберите способ оплаты — нажмите кнопку «Оплата картой или Яндекс Пэй / Сплит». Система перенаправит вас в личный кабинет Яндекс Пэй, где вы сможете выбирать комфортный график платежей. Оплатите часть стоимости покупки онлайн. Оставшиеся три части спишутся автоматически с шагом в две недели. Для удобства отслеживания платежей рекомендуем скачать приложение Яндекс Пэй."
                                                      ])
                               }
                              
                              {:label (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                 :height 40
                                                                 :display "flex"
                                                                 :align-items "center"}}
                                                   "Оплата Долями"])
                               :key "parameters"
                               :children (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                    :text-align "left"
                                                                    :white-space "pre-wrap"
                                                                    }}
                                                      "Сумма онлайн-покупок не может превышать порог 30 000 рублей, покупок может быть несколько. Если лимит превышен, совершить новый заказ с оплатой через сервис «Долями» возможно только после того, когда хотя бы один из активных заказов будет завершен. Сервис «Долями» помогает вам разделить сумму покупки на четыре равных платежа — без комиссии и дополнительных плат. Покупку получите сразу после оплаты первой части.

Описание процесса платежа через сервис «Долями»:

Сформируйте корзину с покупками на сайте. Выберите способ оплаты — нажмите кнопку «Банковской картой CloudPauments». Система перенаправит вас на меню, где нужно выбрать «Долями». Укажите номер телефона, ФИО, дату рождения и e-mail. Оплатите 25% стоимости покупки онлайн. Оставшиеся три части спишутся автоматически с шагом в две недели."
                                                      ])
                               }
                              
                              {:label (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                 :height 40
                                                                 :display "flex"
                                                                 :align-items "center"}}
                                                   "Правила использования промокода:"])
                               :key "care"
                               :children (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                    :text-align "left"
                                                                    :white-space "pre-wrap"
                                                                    }}
                                                      "Промокод не суммируется с другими скидками на нашем сайте и может быть ограничен в период проведения Season sale."
                                                      ])
                               }
                              
                              {:label (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                 :height 40
                                                                 :display "flex"
                                                                 :align-items "center"}}
                                                   "Реквизиты"])
                               :key "outfit"
                               :children (as-element [:div {:style {:font-family "'orchidea_light', sans-serif"
                                                                    :text-align "left"
                                                                    :white-space "pre-wrap"
                                                                    }}
                                                      "
ИП МАНЬКОВ АНТОН ЮРЬЕВИЧ
ОГРИП: 319665800081550
ИНН: 662331342395
ОКПО: 0160964849

БАНКОВСКИЕ РЕКВИЗИТЫ:
р/с 40802810616540000505
Уральский банк ПАО «Сбербанк»
г. Екатеринбург
к/с 30101810500000000674
БИК 046577674

электронная почта: mankova_official@mail.ru
тел.: 8 900 203 12 13 (только WhatsApp/Telegram)"
                                                      ])
                               }]}]
        [:div {:style {:margin-left 10
                       :margin-right 10}}
         [:> Divider {:style {:borderColor "#d9d9d9"
                              :margin-top "0px !important"}}]]
        ]
       ]
      )
      )
      )