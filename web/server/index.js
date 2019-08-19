'use strict';

require('dotenv').config();
const express = require('express');
const bodyParser = require('body-parser');
const Sequelize = require('sequelize');

// Constants
const PORT = 8080;
const HOST = '0.0.0.0';

const sequelize = new Sequelize('genetic_algorithms',
    process.env.MYSQL_USER || 'root',
    process.env.MYSQL_PASSWORD || 'password',
    {
        host: 'localhost',
        dialect: 'mysql'
    });

// TODO: Fix issue with authentication. Currently getting
//  'Client does not support authentication protocol requested by server; consider upgrading MySQL client' } }
//  [nodemon] restarting due to changes...
//  [nodemon] starting `node server/index.js`
//
//  Could add this to docker-compose.yml:
//  # For compatibility with npm mysql2 package's authentication method.
//  command: --default-authentication-plugin=mysql_native_password
// sequelize
//     .authenticate()
//     .then(() => {
//         console.log('Connection to MySQL has been established successfully.');
//     })
//     .catch(err => {
//         console.error('Unable to connect to MySQL:', err);
//     });

// App
const app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));

app.get('*', (req, res) => {
    res.send('Hello world\n');
});

app.listen(PORT, HOST);
console.log(`Running on http://${HOST}:${PORT}`);