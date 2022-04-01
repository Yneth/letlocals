(ns letlocals.core)

(defn bind? [exp]
  (and (coll? exp)
       (= 'bind (first exp))))

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

(def bind
  "used only for editor autocomplete & validation"
  ^{:macro true}
  (fn [name value]
    (list 'bind name value)))

(defmacro letlocals
  "Nested bind expressions are not supported!"
  {:special-form true, :forms '[(letlocals exprs* (bind name val) exprs*)]}
  [& body]
  (let [exp->let-binding
        (fn [exp]
          (if (bind? exp)
            (let [[_ var-name var-value :as bind] exp]
              (validate-bind bind)
              [var-name var-value])
            #_else
            ['_ exp]))

        bindings
        (into []
              (comp (map exp->let-binding) cat)
              (butlast body))

        ret
        (last body)]
    `(let ~bindings ~ret)))
