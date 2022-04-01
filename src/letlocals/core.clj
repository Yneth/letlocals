(ns letlocals.core)

(defn bind? [exp]
  (and (coll? exp)
       (= 'bind (first exp))))

(defn bind-group? [exps]
  (and (coll? exps)
       (bind? (first exps))))

(defn ->bind-exception [exp reason]
  (IllegalStateException.
    (str "Invalid bind statement: " exp \newline reason)))

(defn validate-bind [[_ var-name _ & other :as all]]
  (when (< (count all) 2)
    (throw (->bind-exception all "var name is missing")))

  (when-not (or (symbol? var-name)
                (map? var-name)
                (coll? var-name))
    (throw (->bind-exception all "var name is invalid")))

  (when (< (count all) 3)
    (throw (->bind-exception all "var value is missing")))

  (when (> (count all) 3)
    (throw (->bind-exception all (str "unnecessary options: " other)))))

(defn bind-group->let [bind-exps prev-exps]
  (run! validate-bind bind-exps)
  (let [bindings
        (into
          []
          (comp
            (map (fn [[_ var-name var-val]] [var-name var-val]))
            cat)
          bind-exps)]
    `(let ~bindings ~@prev-exps)))

(def bind
  "used only for editor autocomplete & validation"
  ^{:macro true}
  (fn [name value]
    (list 'bind name value)))

(defmacro letlocals
  "Nested bind expressions are not supported!"
  {:special-form true, :forms '[(letlocals exprs* (bind name val) exprs*)]}
  [& body]
  (let [partitions
        (vec (partition-by bind? body))

        result-body
        (->>
          partitions
          (rseq)
          (reduce
            (fn [acc exp-group]
              (cond
                (bind-group? exp-group)
                (bind-group->let exp-group acc)

                :else
                (let [tail (when-not (nil? acc) (list acc))]
                  (concat exp-group tail))))
            nil))]
    (if (bind-group? (first partitions))
      result-body
      `(do ~@result-body))))
