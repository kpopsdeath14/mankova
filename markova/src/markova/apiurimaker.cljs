(ns markova.apiurimaker)

(defn api_uri_maker [route]
  (str "https://tg-market.qq-pp.ru/mankova-api/api/" route)
  )