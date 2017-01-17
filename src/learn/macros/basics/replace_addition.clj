(ns learn.macros.basics.replace-addition)

(comment
  ;; There are two phases: read and eval
  ;; We can get in the middle of reading to change
  ;; the output data structures before they are evaled.

  (let [expression (read-string "(+ 1 2 3 4 5)")]
    (let [changed (cons (read-string "*") 
                        (rest expression))]
      (eval changed)))

  ;; We cannot simply type the expression (+ 1 2 3 4 5)
  ;; because it would be evaulated right away, so to
  ;; suppress the evaluation of a list we must quote it.

  (let [expression (quote (+ 1 2 3 4 5))]
    (let [changed (cons (quote *)
                        (rest expression))]
      (eval changed))))

(comment
  
  ;; there ia reader macro to simplify quoting
  (let [expression '(+ 1 2 3 4 5)]
    (let [changed (cons '* (rest expression))]
      (eval changed))))

