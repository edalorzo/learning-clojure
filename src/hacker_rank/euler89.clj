(ns hacker-rank.euler89)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; ROMAN NUMERALS
;; https://www.hackerrank.com/contests/projecteuler/challenges/euler089
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def roman-numerals {\I 1 \V 5 \X 10 \L 50 \C 100 \D 500 \M 1000})

(defn roman-to-dec [roman]
  (let [roman (vec (reverse roman))]
    (loop [[r & rs :as before] roman prev 0 result 0]
      (if-not (empty? before)
        (let [n (roman-numerals r)]
          (if (>= n prev)
            (recur rs n (+ result n))
            (recur rs n (- result n))))
        result))))

(defn explode-to-digits [number]
  (map #(- (int %) 48) (str number)))

(def roman-units [
  ["I","II","III","IV","V","VI","VII","VIII","IX"],
  ["X","XX","XXX","XL","L","LX","LXX","LXXX","XC"],
  ["C","CC","CCC","CD","D","DC","DCC","DCCC","CM"]])

(defn dec-to-roman [number]
  {:pre [(> number 0)]}
  (let [indexer (partial map-indexed vector)
        indexed (comp indexer reverse explode-to-digits)
        digits (indexed number)]
    (loop [[[i n] & more :as numbers] digits romans ""]
      (if-not (empty? numbers)
        (if (> n 0)
          (if (< i 3)
            (recur more (str (get-in roman-units [i (dec n)]) romans))
            (let [size (* n (int (Math/pow 10 (- i 3))))
                  ms (apply str (repeat size \M))]
                (recur more (str ms romans))))
          (recur more romans))
        romans))))

(comment
  
  ;; Reads HackerRank input data
  (defn get-input []
    (let [total-lines (Integer/parseInt (read-line))]
      (for [line (range 0 total-lines)]
        (read-line))))

  ;; Solves the algorithm

  (let [fix (comp dec-to-roman roman-to-dec)
        input (get-input)]
    (map println (map fix input)))
  
)