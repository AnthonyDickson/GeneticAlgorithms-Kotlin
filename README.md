# Genetic Algorithms
Simulating evolution with genetic algorithms.

## The Game Engine
Large portions of the code for the game engine is based on code from the online book [3D Game Development with LWJGL 3](https://ahbejarano.gitbook.io/lwjglgamedev/).

## Getting Started
1.  Download and install [MySQL Server](https://dev.mysql.com/downloads/mysql/) if you do not have it installed already.

2.  Start up an instance and set the following environment variables appropriately:
    ```shell script
    export DB_URL=jdbc:mysql://<url-to-database>/
    export DB_USERNAME=<database-username>
    export DB_PASSWORD=<database-password>
    ```

2.  Run the app:
    ```shell script
    gradlew run
    ```
    This should also run any first time setup automatically.