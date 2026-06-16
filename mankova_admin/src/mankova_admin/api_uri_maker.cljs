(ns mankova-admin.api-uri-maker)

(defn api_uri_maker [route]
  (str "https://mankova-admin-api.qq-pp.ru/api/" route)
  ;(str "https://mankova-admin-api.qq-pp.ru/" route)
  )