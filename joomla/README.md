E2E Web Testing benchmark
=========================

Test suites for Joomla
----------------------

This directory contains test suites and Gherkin speficiations for Joomla. The Gherkin specifications refer to Joomla 3.10.11.

# Deployment instructions
A Docker image is available for both versions of the application (3.10.11 and 4.2.0).

## Deployment instructions for Joomla 3.10.11
The Docker containers for Joomla 3.10.11 can be created using the `docker-compose.yml` file contained in `joomla/baseline/joomla-3.10.11/docker-compose.yml`. Just move into the directory using a terminal and type:

```bash
docker compose up
```
The web application will be exposed on `localhost:8080`. After the containers are deployed, an installation wizard must be followed. Please refer to the further section **Installation instructions (only for Joomla 3.10.11)**

## Deployment instructions for Joomla 4.2.0
The Docker container for the application under test can be created using the following command:

```bash
docker run -i -t  --name=joomla -p "3000:80" -d olianasd/joomla4stile
```

The web application will be exposed on `localhost:3000`. To use the test suite with the RemoteWebDriver, the acutal IP address/domain name where the application resides must be used instead of `localhost`. The URL must be changed in the classes `test.BaseTest.app_url` and `test.Installer.app_url`.

These test suites has been executed 50 times on Google Chrome version 138 without failures. To deploy the browser in a Docker container, use the following command:

```bash
docker run -d -p 4444:4444 -p 7900:7900 --shm-size="2g" --name=browser selenium/standalone-chrome:138.0-chromedriver-138.0
```

# Installation instructions (only for Joomla 3.10.11)

The installation wizard can be executed automatically by running `tests.Installer` as a JUnit test. If, for any reason, the automatic installation fails, these are the parameters that you should set in the installation wizard:

*	Language: English (United States)
*	Site name: TestRigor joomla test
*	Email: olianas@fake.com
*	Username: administrator
*	Password: e2eW3Bt3s71nGB3nchM4rK or dodicicaratteri
* 	Database type: MySQLi
*	Host Name: joomladb
*	Username (database): root
* 	Password (database): example
*	Database name: joomla310
*	Install sample data: Blog English (GB) Sample Data
*	E-mail configuration: No
* 	Remove installation folder

After completing the installation wizard (either manually or automatically), you need to access to the administration area of the application (http://localhost:8080/administrator), login and close some notifications, that otherwise would change the expected layout of the page and make test scripts fail. In detail, you have to answer "Never" to the permission to collect statistics, and read all post installation messages. You can execute these actions automatically by running `tests.RemoveMessages`.


![First step](https://i.imgur.com/1e2D90G.png "Answer Never to the permission to collect statistics")

![Second step](https://i.imgur.com/wNhU1jN.png "Click Read messages")

![Third step](https://i.imgur.com/KtPDmyw.png "Click Hide all messages")