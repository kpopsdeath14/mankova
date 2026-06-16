(ns markova-backend.datamodule)

(import 'org.postgresql.util.PGobject)
(require '[next.jdbc.prepare :as prepare])
(require '[next.jdbc.result-set :as rs])
(require '[next.jdbc :as jdbc])
(require '[next.jdbc.sql :as sql])
(require '[jsonista.core :as json_])
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])



(require '[clojure.edn :as edn])

(def mapper (json_/object-mapper {:decode-key-fn keyword}))
(def ->json json_/write-value-as-string)
(def <-json #(json_/read-value % mapper))


(defn ->pgobject
  "Transforms Clojure data to a PGobject that contains the data as
  JSON. PGObject type defaults to `jsonb` but can be changed via
  metadata key `:pgtype`"
  [x]
  (let [pgtype (or (:pgtype (meta x)) "jsonb")]
    (doto (PGobject.)
      (.setType pgtype)
      (.setValue (->json x)))))

(defn <-pgobject
  "Transform PGobject containing `json` or `jsonb` value to Clojure
  data."
  [^org.postgresql.util.PGobject v]
  (let [type  (.getType v)
        value (.getValue v)]
    (if (#{"jsonb" "json"} type)
      (with-meta (<-json value) {:pgtype type})
      value)))

(import  '[java.sql PreparedStatement])

(set! *warn-on-reflection* true)



(extend-protocol prepare/SettableParameter
  clojure.lang.IPersistentMap
  (set-parameter [m ^PreparedStatement s i]
    (.setObject s i (->pgobject m)))

  clojure.lang.IPersistentVector
  (set-parameter [v ^PreparedStatement s i]
    (.setObject s i (->pgobject v))))



(extend-protocol rs/ReadableColumn
  org.postgresql.util.PGobject
  (read-column-by-label [^org.postgresql.util.PGobject v _]
    (<-pgobject v))
  (read-column-by-index [^org.postgresql.util.PGobject v _2 _3]
    (<-pgobject v)))


(def user_user_add_sql                      "SELECT        \"user\".user_add(_parameters := ?);")
(def product_product_get_sql                "SELECT * FROM   ui.product_get_list (_p := ?);")
(def product_product_get_single_sql         "SELECT * FROM   product.product_get_single(_p := ?)")
(def product_product_attribute_get_filter   "SELECT * FROM   product.product_attribute_get_filter();")
(def user_cart_set_sql                      "CALL          \"user\".cart_set(_p := ?);")
(def user_cart_get_sql                      "SELECT * FROM \"user\".cart_get(_p := ?);")
(def user_cart_get_cummary_sql              "SELECT        \"user\".cart_get_summary(_p := ?);")
(def user_user_attribute_get                "SELECT * FROM \"user\".user_attribute_get(_p := ?);")
(def product_storage_moysklad_stock_upd_sql "CALL          \"product\".storage_moysklad_stock_upd(_p := ?);")
(def order_payments_add_sql                 "SELECT        \"order\".payments_add(_p := ?);")
(def order_payments_upd_sql                 "CALL          \"order\".payments_upd(_p := ?);")
(def user_user_attribute_add_sql            "CALL          \"user\".user_attribute_add(_p := ?);")
(def user_cart_set_complete_sql             "SELECT        \"user\".cart_set_complete(_p := ?);")
(def banner_get_sql                         "SELECT * FROM   ui.banners_get(_p := ?);")
(def user_user_get_init_sql                 "SELECT        \"user\".user_get_init(_p := ?);")
(def exchange_history_add_sql               "SELECT * FROM   xtrnl.exchange_history_add(_p := ?);")
(def exchange_history_upd_sql               "CALL            xtrnl.exchange_history_upd(_p := ?);")
(def history_get_cdek_sql                   "SELECT * FROM   xtrnl.exchange_history_get_cdek(_p := ?);")
(def history_get_crm_sql                    "SELECT          xtrnl.exchange_history_get_crm(_p := ?);")
(def user_delivery_cost_get_sql             "SELECT        \"user\".delivery_cost_get(_p := ?);")
(def user_policies_get_sql                  "SELECT * FROM \"user\".policies_get(_p := ?);")


(def mypg-db (with-open [r (io/reader "./db_parameters.edn")]
               (edn/read (java.io.PushbackReader. r))))


(def db_con (jdbc/get-datasource mypg-db))


(defn db_query_sender
  [query_inf temp params]
  (let [first_part_of_query_string (subs temp 0 (str/index-of temp "("))
        splited_query_string (str/split first_part_of_query_string #" ")
        method_name (splited_query_string (- (count splited_query_string) 1))]
    (if (= 1 (- (str/index-of temp ")") (str/index-of temp "(")))
      (let [db_ans (sql/query db_con [temp])]
        db_ans)
      (let [db_ans (sql/query db_con [temp params])]
        db_ans))))


(defn db_proxy
  [query_string params]
  (try (def db_ans (sql/query db_con [query_string params]))
       (catch Exception e (str "caught exception: " (.getMessage e))))

  (if (nil? (:error_message db_ans))
    db_ans
    (println (str "exception message: " (:error_message db_ans)))))
