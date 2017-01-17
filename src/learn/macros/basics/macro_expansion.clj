(ns learn.macros.basics.macro-expansion)

(comment
  
  (macroexpand-1 '(when (= 1 2) (println "Math is broken")))
  ;;expands to: (if (= 1 2) (do (println "Math is broken")))

  ;;buy what if the macro starts with another macro

  (defmacro when-falsy [test & body]
    (list 'when (list 'not test)
          (cons 'do body)))

  (when-falsy (= 1 2) (println "Math is not broken"))

  ;; if we do macroexpand-1 
  (macroexpand-1 '(when-falsy (= 1 2) (println "Math is not broken")))

  ;; we get an expression that starts with a another macro
  ;; (when (not (= 1 2)) (do (println "Math is not broken")))

  ;;but if we do macroexpand instead
  (macroexpand '(when-falsy (= 1 2) (println "Math is not broken")))

  ;;we get the full expansion
  ;; (if (not (= 1 2)) (do (do (println "Math is not broken"))))
)