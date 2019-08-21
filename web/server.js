const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');
const mysql = require('mysql');

const connection = mysql.createConnection({
    host: 'localhost',
    user: process.env.MYSQL_USER || 'root',
    password: process.env.MYSQL_PASSWORD || 'password',
    database: 'genetic_algorithms'
});

connection.connect();

const app = express();
const port = process.env.PORT || 5000;

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

app.get('/api', (req, res) => {
    res.send({message: 'Hello, World!'});
});

// TODO: Refactor routes into controller+router
app.get('/api/runs', (req, res) => {
    connection.query(
        'SELECT id, create_time FROM runs',
        (err, rows) => {
            if (err) throw err;

            res.send({runs: rows});
        }
    );
});

app.get('/api/runs/:run_id', (req, res) => {
    connection.query(
        `SELECT runs.id, runs.create_time FROM runs WHERE id = ${req.params.run_id}`,
        (err, rows) => {
            if (err) throw err;

            res.send({run: rows[0]})
        }
    )
});

const creatures_sql = `
    SELECT runs.id              AS run_id,
           creatures.id         AS creature_id,
           creatures.species_id AS species_id,
           creatures.age,
           creatures.replication_chance,
           creatures.death_chance,
           creatures.speed,
           creatures.size,
           creatures.colour_red,
           creatures.colour_green,
           creatures.colour_blue,
           creatures.metabolic_efficiency,
           creatures.sensory_range,
           creatures.greediness,
           creatures.thriftiness,
           creatures.shininess
    FROM runs
             JOIN
         creatures ON creatures.run_id = runs.id
`;

app.get('/api/runs/:run_id/creatures/', (req, res) => {
    connection.query(
        // language=MySQL
        creatures_sql + `WHERE runs.id = ${req.params.run_id}`,
        (err, rows) => {
            if (err) throw err;

            res.send({creature: rows})
        }
    )
});

app.get('/api/runs/:run_id/creatures/:creature_id', (req, res) => {
    connection.query(
        // language=MySQL
        creatures_sql + `
            WHERE 
                runs.id = ${req.params.run_id} 
                    AND 
                creatures.id = ${req.params.creature_id}
        `,
        (err, rows) => {
            if (err) throw err;

            res.send({creature: rows[0]})
        }
    )
});

// TODO: Add routes for getting species (should be under path /runs/:run_id/species/:species_id?).
// TODO: Add routes for getting censuses (should be under path /runs/:run_id/censuses/:census_id?).

if (process.env.NODE_ENV === 'production') {
    // Serve any static files
    app.use(express.static(path.join(__dirname, 'client/build')));

    // Handle React routing, return all requests to React app
    app.get('*', function (req, res) {
        res.sendFile(path.join(__dirname, 'client/build', 'index.html'));
    });
}

app.listen(port, () => console.log(`Listening on port ${port}`));