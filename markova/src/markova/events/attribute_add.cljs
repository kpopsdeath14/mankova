(ns markova.events.attribute-add
  (:require [ajax.core :as ajax]
            [markova.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [markova.apiurimaker :refer [api_uri_maker]]
            [reagent.core :as reagent]))


(defn attribute_add_handler [[ok? response]] 
  (swap! app-state assoc :show_agreements? false)
  )


(defn attribute_add []
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        id (.-id user)
        init-data js/Telegram.WebApp.initData
        current-date (.toISOString (js/Date.))
        attributes [
                    {:attribute_name "cookies_accepted_date"
                     :attribute_value current-date
                     :telegram_user_id id}
                    
                    {:attribute_name "agreement_accepted_date" 
                     :attribute_value current-date
                     :telegram_user_id id}
                    
                    {:attribute_name "agreement_accepted_date"
                     :attribute_value current-date
                     :telegram_user_id id}
                    
                    {:attribute_name "politics_accepted_date"
                     :attribute_value current-date
                     :telegram_user_id id}
                    {:attribute_name "politics_MANKOVA_accepted_date"
                     :attribute_value current-date
                     :telegram_user_id id}
                    {:attribute_name "agreement_MANKOVA_accepted_date"
                     :attribute_value current-date
                     :telegram_user_id id}
                    

                    ]
        ]
    
    (ajax/ajax-request
     {:uri (api_uri_maker "user_attribute_add")
      :method :post
      :params {:attributes attributes :telegram_user_id id}
      :handler attribute_add_handler
      :headers {"X-Telegram-InitData" init-data}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})
    )
  )