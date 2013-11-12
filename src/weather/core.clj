(ns weather.core
  (:require [clj-http.client :as http]))

(def weather-api
  "http://api.openweathermap.org/data/2.5/")

(defn weather-response [path params]
  (let [params (merge {:accept :json :as :json} params)
        response (http/get (str weather-api path) params)]
    (:body response)))

(defn find [query]
  (weather-response "find" {:query-params {:q query}}))

(defn weather [location]
  (weather-response "weather" {:query-params {:q location}}))

(defn forecast [location params]
  (let [default-params {:q location
                        :mode :json
                        :units :metric
                        :cnt 7}
        params (merge default-params params)
        response (weather-response "forecast/daily"
                                   {:query-params params})]
    (:list response)))


;; How many cities called London are there?

(:count (find "london"))

;; What are the lat/long positions of all the Londons?

(map :coord (:list (find "london")))

;;  What has been the average temperature of London, UK for the last 5 days? (hint: forecast?q=London)

(defn average-temp [num-days]
  (let [forecast-days (forecast "london,uk" {:cnt num-days})
        temps (map #(:day (:temp %)) forecast-days)]
    (/ (reduce + temps) (count temps))))

(average-temp 5)

;;  What has been the average temperature of London, UK for the last 10 days?

(average-temp 10)

;;  On how many of the last 10 days has it been cloudy?

(count (filter #(= (:main (first (:weather %))) "Clouds") (forecast "london,uk" {:cnt 10})))

;;  On how many of the last 10 days has it not been cloudy?

(count (remove #(= (:main (first (:weather %))) "Clouds") (forecast "london,uk" {:cnt 10})))
