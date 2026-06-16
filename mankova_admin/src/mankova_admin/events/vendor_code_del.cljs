(ns mankova-admin.events.vendor-code-del
  (:require [ajax.core :as ajax]
            [mankova-admin.db :refer [app-state]]
            [reagent.cookies :as cookies]
            [mankova-admin.api-uri-maker :refer [api_uri_maker]]
            [mankova-admin.events.vendore-code-get :refer [vendore_code_get]]
            ))


(defn vendor_code_del_handler [[ok? response]]
  (vendore_code_get (assoc (:filters_picked @app-state) :actual [(case (:products_mode @app-state)
                                                                   "catalog" ["t" "true"]
                                                                   "archive" ["f" "false"])]) (:search_value @app-state))
  )





(defn vendor_code_del [vendor_code]
  (let [user (.. js/Telegram -WebApp -initDataUnsafe -user)
        ;id (.-id user)
        init-data js/Telegram.WebApp.initData]
    (ajax/ajax-request
     {:uri (api_uri_maker "vendor-code-del")
      :method :post
      :params {:vendor_code vendor_code}
      :handler vendor_code_del_handler
      :headers {"X-telegram-InitData" init-data
                "X-Telegram-Hash" (:user_data_hash @app-state)
                }
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))