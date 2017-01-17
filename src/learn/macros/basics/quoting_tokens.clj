(ns learn.macros.basics.quoting-tokens)


(comment
  (class (quote 1))       ; Long
  (class (quote 0xFF))    ; Long
  (class (quote 017))     ; Long
  (class (quote 2r1011))  ; Long
  (class (quote 2.78))    ; Double
  (class (quote 4.2M))    ; BigDecimal
  (class (quote 7N))      ; BigInt
  (class (quote 1/2))     ; Ratio
  (class (quote "hello")) ; String
  (class (quote :name))   ; Keyworkd
  (class (quote sum))     ; Symbol
  (class (quote map))     ; Symbol
  
)

