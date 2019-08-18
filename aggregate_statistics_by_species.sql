SELECT species.name       AS species,
       COUNT(species.id)  AS population,
       AVG(creatures.age) AS avg_age,
       AVG(chromosomes.death_chance),
       AVG(chromosomes.greediness),
       AVG(chromosomes.metabolic_efficiency),
       AVG(chromosomes.replication_chance),
       AVG(chromosomes.sensory_range),
       AVG(chromosomes.shininess),
       AVG(chromosomes.size),
       AVG(chromosomes.speed),
       AVG(chromosomes.thriftiness)
FROM census_participants
         JOIN creatures ON creatures.id = census_participants.creature_id
         JOIN species ON species.id = creatures.species_id
         JOIN chromosomes ON chromosomes.id = creatures.id
WHERE census_id = 5
GROUP BY species.id;