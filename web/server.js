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

app.get('/api/runs', (req, res) => {
    connection.query('SELECT id, create_time FROM runs', function (err, rows) {
        if (err) throw err;

        res.send({runs: rows});
    });
});

// TODO: Create endpoint to pull down all data for a given run.
app.get('/api/runs/:id', (req, res) => {
    res.status(501);
    res.send({message: 'Endpoint not implemented.'})
});

if (process.env.NODE_ENV === 'production') {
    // Serve any static files
    app.use(express.static(path.join(__dirname, 'client/build')));

    // Handle React routing, return all requests to React app
    app.get('*', function (req, res) {
        res.sendFile(path.join(__dirname, 'client/build', 'index.html'));
    });
}

app.listen(port, () => console.log(`Listening on port ${port}`));