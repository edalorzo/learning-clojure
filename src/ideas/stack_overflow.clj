(ns ideas.stack-overflow)

(defn any-valid? [x [p & ps :as preds]]
  (if-not (empty? preds)
    (or (p x) (any-valid? x ps))))

(comment 
  
  (defn any-valid? [x [p & ps :as preds]]
    (if-not (empty? preds)
      (let [res (p x)]
        (if-not res
          (recur x ps)
          res))))
)

