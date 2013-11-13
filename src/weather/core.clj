(ns weather.core
  (:require [clj-http.client :as http]))

(def weather-api
  "http://api.openweathermap.org/data/2.5/")

(defn weather-response [path params]
  (let [params (merge {:accept :json :as :json} params)
        response (http/get (str weather-api path) params)]
    (:body response)))

(defn search [query]
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

(:count (search "london"))

;; What are the lat/long positions of all the Londons?

(let [londons (:list (search "london"))]
  (map :coord londons))

;;  What will be the average temperature of London, UK for the next 5 days? (hint: forecast?q=London)

(defn average-temp [num-days]
  (let [forecast-days (forecast "london,uk" {:cnt num-days})
        temps (map #(:day (:temp %)) forecast-days)]
    (/ (reduce + temps) (count temps))))

(average-temp 5)

;; What will be the average temperature of London, UK for the next 10 days

(average-temp 10)

;; On how many of the next 10 days will be cloudy?

(let [forecast-days (forecast "london,uk" {:cnt 10})
      cloudy #(= (:main (first (:weather %))) "Clouds")]
  (count (filter cloudy forecast-days)))

;; On how many of the next 10 days will not be cloudy?

(let [forecast-days (forecast "london,uk" {:cnt 10})
      cloudy #(= (:main (first (:weather %))) "Clouds")]
  (count (remove cloudy forecast-days)))

