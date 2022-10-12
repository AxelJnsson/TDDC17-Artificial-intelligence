;; This is a small problem instance for the standard Logistics domain,
;; as defined in "logistic.pddl".

(define (problem C3_2)
  (:domain logistics)
  (:objects
   city1 city2 city3           ;; there are three cities,
   truck1 truck2 truck3 truck4 truck5        ;; one truck in each city, some have two
   airplane1                  ;; only one airplane,
   train1                     ;; one train
   trainStation1 trainStation2  ;; two trainstations
   office1 office2 office3    ;; offices are "non-airport" locations
   airport1 airport3 ;; two airports
   packet1 packet2 packet3 packet4 packet5           ;; 5 packages to be delivered
   )
  (:init
   ;; Type declarations:
   (object packet1) (smallPackage packet1) 
   (object packet2) (largePackage packet2) 
   (object packet3) (largePackage packet3) 
   (object packet4) (smallPackage packet4) 
   (object packet5) (smallPackage packet5) 

   ;; all vehicles must be declared as both "vehicle" and their
   ;; appropriate subtype, and size
   (vehicle truck1) 
   (vehicle truck2) 
   (vehicle truck3) 
   (vehicle truck4) 
   (vehicle truck5) 
   (vehicle airplane1) 
   (vehicle train1)
   (truck truck1) 
   (truck truck2) 
   (truck truck3) 
   (truck truck4) 
   (truck truck5)
   (smallVehicle truck1) 
   (smallVehicle truck2) 
   (largeVehicle truck3) 
   (largeVehicle truck4) 
   (largeVehicle truck5)  
   (airplane airplane1) 
   (train train1)
   (largeVehicle train1) 
   (largeVehicle airplane1) 


   ;; likewise, airports must be declared both as "location" and as
   ;; the subtype "airport",
   (location office1) (location office2) (location office3)
   (location airport1)  (location airport3)
   (location trainStation1) (location trainStation2) 
   (airport airport1)  (airport airport3)
   (trainStation trainStation1) (trainStation trainStation2) 
   (city city1) (city city2) (city city3)

   ;; "loc" defines the topology of the problem,
   (loc office1 city1) (loc airport1 city1) (loc trainStation1 city1) (loc office2 city2) (loc trainStation2 city2)
   (loc office3 city3) (loc airport3 city3) 

   ;; The actual initial state of the problem, which specifies the
   ;; initial locations of all packages and all vehicles:
   (at packet1 office1)
   (at packet2 office3)
   (at packet3 office1)
   (at packet4 office1)
   (at packet5 office3)
   (at truck1 airport1)
   (at truck2 office2)
   (at truck3 office3)
   (at truck4 airport1)
   (at truck5 office2)
   
   (at airplane1 airport1)
   (at train1 trainStation1)
   )

  ;; The goal is to have all packages delivered to their destinations:
  (:goal (and (at packet1 office2) (at packet2 office2)
  (at packet3 office2)(at packet4 office2)(at packet5 office2)))
  )