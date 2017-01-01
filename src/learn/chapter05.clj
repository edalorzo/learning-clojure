(ns learn.chapter05
  (:import (java.util Random Calendar TimeZone)
           (java.text SimpleDateFormat)
           (java.io File FileFilter)
           (java.awt.event MouseAdapter)))


;; There are multiple ways to invoke Java methods

;; Example #1
(defn date-from-string-1 [date-string]
  (let [sdf (new SimpleDateFormat "yyyy-MM-dd")]
    (.parse sdf date-string)))

;; Example #2
(defn date-from-string-2 [date-string]
  (let [sdf (new SimpleDateFormat "yyyy-MM-dd")]
    (. sdf parse date-string)))

 ;; Example #3
(defn date-from-string-3 [date-string]
  (let [sdf (new SimpleDateFormat "yyyy-MM-dd")]
    (. sdf (parse date-string))))


;; Java Syntax can be verbose
;; Consider getting the time zone name

;; Example 1
(defn time-zone-name []
  (. (. (Calendar/getInstance) (getTimeZone)) (getDisplayName)))

;; Example 2: Simplified notation
(defn time-zone-name []
  (. (. (Calendar/getInstance) getTimeZone) getDisplayName))

;; Example 3: Using double dot notation
(defn time-zone-name []
  (.. (Calendar/getInstance) (getTimeZone) (getDisplayName)))

;; Example 4: Double dot with simplified notation
(defn time-zone-name []
  (.. (Calendar/getInstance) getTimeZone getDisplayName))

;;Consider the more elaborate example
(defn time-zone-short-name []
  (.. (Calendar/getInstance) getTimeZone (getDisplayName true TimeZone/SHORT)))


(defn the-past-midnight-1 []
  (let [calendar (Calendar/getInstance)]
    (.set calendar Calendar/AM_PM Calendar/AM)
    (.set calendar Calendar/HOUR 0)
    (.set calendar Calendar/MINUTE 0)
    (.set calendar Calendar/SECOND 0)
    (.set calendar Calendar/MILLISECOND 0)
    (.getTime calendar)))

;; a better way to do the previous thing is

(defn the-past-midnight-2 []
  (let [calendar (Calendar/getInstance)]
    (doto calendar
      (.set Calendar/AM_PM Calendar/AM)
      (.set Calendar/HOUR 0)
      (.set Calendar/MINUTE 0)
      (.set Calendar/SECOND 0)
      (.set Calendar/MILLISECOND 0))
    (.getTime calendar)))

(defn get-string-bytes-1 [strings]
  (map (fn [s] (.getBytes s)) strings))

(defn get-string-bytes-2 [strings]
  (map #(.getBytes %) strings))

(defn get-string-bytes-3 [strings]
  (map (fn [^String s] (.getBytes s)) strings))

(defn get-string-bytes-4 [strings]
  (map (memfn ^String getBytes) strings))

(def my-subseq (memfn ^String subSequence ^Long start ^Long end))


;; IMPLEMENTING INTERFACES AND EXTENDING CLASSES

(defn create-mouse-adapter []
  (proxy [MouseAdapter] []
    (mousePressed 
      [event]
      (println "Hey!"))))

(comment                 
  ;; which you could invoke by doing
  (. (create-mouse-adapter) (mousePressed nil))
)

(def my-filter 
  (reify FileFilter
    (accept [this f]
            (.isDirectory f))))

(comment 
  ;;which I could invoke as
  (. my-filter (accept (File. "."))) ;; yields true
)

