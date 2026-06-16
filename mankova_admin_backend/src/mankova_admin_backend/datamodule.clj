(ns mankova-admin-backend.datamodule)

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


(def product_get_vendor_codes_sql           "SELECT * FROM    admin.product_get_vendor_codes(_p := ?);")
(def product_to_archive_sql                 "CALL           \"admin\".product_to_archive(_p := ?);")
(def product_attribute_get_filter_sql       "SELECT * FROM    admin.product_attribute_get_filter(_p := ?);")
(def product_get_one_per_row_sql            "SELECT * FROM    product.product_get_one_per_row(_p := ?);")
(def product_attribute_add_sql              "CALL             product.product_attribute_add(_p := ?);")
(def product_add_sql                        "SELECT * FROM    product.product_add();")
(def product_storage_moysklad_stock_upd_sql "CALL           \"product\".storage_moysklad_stock_upd(_p := ?);")
(def product_attribute_add_using_filter_sql "CALL             product.product_attribute_add_using_filter(_p := ?);")
(def price_set_sql                          "CALL             price.prices_set(_p := ?);")
(def product_del_sql                        "CALL             product.product_del(_p := ?)")
(def product_del_vendor_code                "CALL             product.product_del_vendor_code(_p := ?);")
(def product_get_image_name_sql             "SELECT * FROM  \"product\".product_get_image_name(_p := ?);")
(def banner_get_sql                         "SELECT * FROM    ui.banners_get(_p := ?);")
(def banner_add_sql                         "CALL             ui.banners_add (_p := ?);")
(def user_user_get_init_sql                 "SELECT \"user\".user_get_init(_p := ?);")


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
