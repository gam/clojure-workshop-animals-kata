(ns animals.core
  (:use [midje.sweet]))

(unfinished timeslice-intersects? bookings animals species-exclusions procedures )

(defn build-map [value-generator keys]
  (reduce (fn [so-far procedure]
            (assoc so-far procedure (value-generator procedure)))
          {}
          keys))

(defn booked-animals-in-period [timeslice]
   (map :animal (filter #(timeslice-intersects? timeslice) (bookings))))

;.;. FAIL at (NO_SOURCE_FILE:1)
;.;. You claimed the following was needed, but it was never used:
;.;.     (timeslice-intersects? ...timeslice... ...cowbooking...)
;.;. 
;.;. FAIL at (NO_SOURCE_FILE:1)
;.;.     Expected: [...thecow...]
;.;.       Actual: nil
(fact "returns booked animals given a timeslice"
  (booked-animals-in-period ...timeslice...) => [...thecow...]
  (provided
    (animals) => [...thecow... ...thechicken... ...thehorse...]
    (bookings) => [...cowbooking...]
    (timeslice-intersects? ...timeslice... ...cowbooking...) => true
    ...cowbooking... =contains=> { :animal ...thecow... }))

(defn excluded-by-species []
  (build-map
   (fn [procedure] (species-exclusions procedure))
   (procedures)))

(fact "excludes animals from procedures that are not relevant for their species"
  (excluded-by-species) => {...procedure... [...thecow...]
                            ...otherprocedure... [...thehorse...]}
  (provided
    (species-exclusions ...procedure...) => [...thecow...]
    (species-exclusions ...otherprocedure...) => [...thehorse...]
    (procedures) => [...procedure... ...otherprocedure... ]))

(defn excluded-by-timeslice [timeslice]
  (build-map
   (fn [_] (booked-animals-in-period timeslice))
   (procedures)))

(fact "excludes animals that are already booked in the timeslice"
  (excluded-by-timeslice ...timeslice...) => {...procedure... [...thehorse... ...thecow...]}
  (provided
    (procedures) => [...procedure...]
    (booked-animals-in-period ...timeslice...) => [...thehorse... ...thecow...]))

(defn exclusions [timeslice]
  (merge-with (comp set concat)
              (excluded-by-timeslice timeslice)
              (excluded-by-species)))

(fact "exclusions contain animals that are excluded by species or excluded by reservations"
  (exclusions ...timeslice...) => {...procedure... #{ ...daisycow... ...jackhorse...}}
  (provided
    (excluded-by-timeslice ...timeslice...) => {...procedure... [...jackhorse...]}
    (excluded-by-species) => {...procedure... [...daisycow...]}))


(fact "at the very least, procedures yields a vector of procedure names")
