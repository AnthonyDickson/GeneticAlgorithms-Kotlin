SELECT species.name       AS species,
       COUNT(species.id)  AS population,
       AVG(creatures.age) AS avg_age,
       AVG(creatures.death_chance),
       AVG(creatures.greediness),
       AVG(creatures.metabolic_efficiency),
       AVG(creatures.replication_chance),
       AVG(creatures.sensory_range),
       AVG(creatures.shininess),
       AVG(creatures.size),
       AVG(creatures.speed),
       AVG(creatures.thriftiness)
FROM census_participants
         JOIN creatures ON creatures.id = census_participants.creature_id
         JOIN species ON species.id = creatures.species_id
WHERE census_id = 5
GROUP BY species.id;