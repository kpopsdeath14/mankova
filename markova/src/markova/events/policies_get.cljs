(ns markova.events.policies-get
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]))


(defn policies_get_handler [[ok? response]] 
  (let [
        unsigned_policies (vec (map (fn [product] (:data product)) response))
        ]
    
    (swap! app-state assoc :policies_links {:personal_data_agreement (:policy_content (first (filter #(= (:policy_name %) "personal_data_agreement") unsigned_policies)))
                                            :personal_data (:policy_content (first (filter #(= (:policy_name %) "personal_data") unsigned_policies)))

                                            }
           ) 
  
    (if-not (empty? unsigned_policies)
      (swap! app-state assoc :show_agreements? true)
      )
    
    )
  )


(defn policies_get []
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "policies_get")
      :method :post
      :params {:telegram_user_id id}
      :handler policies_get_handler
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))