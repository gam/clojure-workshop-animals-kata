(ns animals.core
  (:use [midje.sweet]))

(unfinished species-exclusions booked-animals-in-period procedures )

(defn build-map [value-generator keys]
  (reduce (fn [so-far procedure]
            (assoc so-far procedure (value-generator procedure)))
          {}
          keys))

(defn excluded-by-species []
  (build-map (fn [procedure] (species-exclusions procedure))
             (procedures)))

(fact "excludes animals from procedures that are not relevant for their species"
  (excluded-by-species) => {...procedure... [...thecow...]
                            ...otherprocedure... [...thehorse...]}
  (provided
    (species-exclusions ...procedure...) => [...thecow...]
    (species-exclusions ...otherprocedure...) => [...thehorse...]
    (procedures) => [...procedure... ...otherprocedure... ]))

(defn excluded-by-timeslice [timeslice]
  (build-map (fn [_] (booked-animals-in-period timeslice))
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

;.;. A journey of a thousand miles begins with a single step. --
;.;. @alanmstokes
(fact "exclusions contain animals that are excluded by species or excluded by reservations"
  (exclusions ...timeslice...) => {...procedure... #{ ...daisycow... ...jackhorse...}}
  (provided
    (excluded-by-timeslice ...timeslice...) => {...procedure... [...jackhorse...]}
    (excluded-by-species) => {...procedure... [...daisycow...]}))


