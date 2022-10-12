;; This is a plain STRIPS formulation of the standard Logistics domain.

;; In this domain, there are six different types of objects: "object"
;; (the packages to be transported), "truck", "airplane" and their
;; common supertype "vehicle", "location" and the subtype "airport",
;; and finally "city". Types are defined by static (in the sense that
;; there are no operators that change their truth value) unary predicates.
;; The types of objects in a problem instance must be defined by including
;; the appropriate typing predicates in the initial state.

;; A binary static predicate called "loc" describes the topology of the
;; problem instance: "(loc ?l ?c)" is true iff the location ?l is in city
;; ?c.

(define (domain logistics)
  (:requirements :strips :disjunctive-preconditions :negative-preconditions)
  (:predicates

   ;; Static predicates:
   (object ?o) (truck ?t) (airplane ?p) (vehicle ?v)
   (location ?l) (airport ?a) (city ?c) (loc ?l ?c)
   (train ?tr) (trainStation ?trs)


   (smallVehicle ?st) ;;small vehicle
   (largeVehicle ?lt) ;;large vehicle

   (smallPackage ?so) ;; small package
   (largePackage ?lo) ;;large package

   ;; Non-static predicates:
   (full ?v) ;; Vechicle is full
   (at ?x ?l) ;; ?x (package or vehicle) is at location ?l
   (in ?p ?v) ;; package ?p is in vehicle ?v
   )

  ;; Actions for loading and unloading packages.
  ;; By declaring all trucks and airplanes to be also "vehicle", we
  ;; can use the same load/unload operator for both (otherwise we
  ;; would need one for each subtype of vehicle). 

  ;; A small vehicle may only have a small package
  ;; A large vehicle may load a large or small package
  (:action load
    :parameters (?o ?v ?l)
    :precondition (and (object ?o) (vehicle ?v) (location ?l)
		                (at ?v ?l) (at ?o ?l) (not (full ?v))
                  (or (and (smallVehicle ?v) (smallPackage ?o))
           (and(largeVehicle ?v) (or (largePackage ?o) (smallPackage ?o)))
           )
           )
    :effect (and (full ?v)(in ?o ?v) (not (at ?o ?l))))

    

  (:action unload
    :parameters (?o ?v ?l)
    :precondition (and (object ?o) (vehicle ?v) (location ?l)
		       (at ?v ?l) (in ?o ?v) (full ?v))
    :effect (and (not(full ?v))(at ?o ?l) (not (in ?o ?v))))

  ;; Drive a truck between two locations in the same city.
  ;; By declaring all locations, including airports, to be of type
  ;; "location", we can use only one driving operator (otherwise,
  ;; we would again need one for each case, i.e. one for from-location-
  ;; to-airport, one for from-location-to-location, etc. Very
  ;; unnecessay).
  (:action drive
    :parameters (?t ?l1 ?l2 ?c)
    :precondition (and (truck ?t) (location ?l1) (location ?l2) (city ?c)
		       (at ?t ?l1) (loc ?l1 ?c) (loc ?l2 ?c))
    :effect (and (at ?t ?l2) (not (at ?t ?l1))))

  ;; Fly an airplane between two airports.
  (:action fly
    :parameters (?p ?a1 ?a2)
    :precondition (and (airplane ?p) (airport ?a1) (airport ?a2)
		       (at ?p ?a1))
    :effect (and (at ?p ?a2) (not (at ?p ?a1))))
  

  ;; Drive a train between two train stations in different cities.
  (:action driveTrain
    :parameters (?tr ?trs1 ?trs2)
    :precondition (and (train ?tr) (trainStation ?trs1) (trainStation ?trs2)
		       (at ?tr ?trs1))
    :effect (and (at ?tr ?trs2) (not (at ?tr ?trs1))))
  )
