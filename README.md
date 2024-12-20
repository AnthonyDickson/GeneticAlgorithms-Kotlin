# Genetic Algorithms
Simulating evolution with genetic algorithms.

## The Game Engine
Large portions of the code for the game engine is based on code from the online book [3D Game Development with LWJGL 3](https://ahbejarano.gitbook.io/lwjglgamedev/).

## Getting Started
1.  Download and install [JDK 11.0.3](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
    if you do not have it installed already.
    
2.  Download and install [Node 10.16.3+](https://nodejs.org/en/download/) if you do not have it installed already.

2.  Download and install [Docker Desktop](https://www.docker.com/products/docker-desktop) if you do not have it installed already.

3.  Create the file `.env` and fill it in using the following format:
    ```.env
    MYSQL_JDBC_URI=jdbc:mysql://<url-to-mysql-db>/
    MYSQL_USER=<mysql-user>
    MYSQL_PASSWORD=<mysql-password>
    MYSQL_ROOT_PASSWORD=<mysql-root-password>
    ```
    You can use the file `.env.template` as a starting point.
    
4.  Start up the web services with Docker compose:
    ```shell script
    docker-compose -f docker-compose.yml up -d
    ```
    On Windows you may have to enable the option "Expose daemon on tcp://localhost:2375 without TLS" if you are having 
    issues connecting to the Docker daemon. Also, on Windows the Docker desktop installation the command 
    `docker-compose` can only be used from powershell, and not from Command Prompt.  

5.  Run the app:
    ```shell script
    gradlew run
    ```
    This should also run any first time setup automatically.
    
6.  You can stop the web services with:
    ```shell script
    docker-compose -f docker-compose.yml stop db
    ```
    which keeps the containers and images or
    ```shell script
    docker-compose -f docker-compose.yml down db
    ```
    which will remove the containers and images.
