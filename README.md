# BackendHW

## Technical Requierements
- [Python 2.x](https://www.python.org/downloads/)
- [nodejs](https://nodejs.dev/)
- [java 12 (or newer)](https://www.oracle.com/de/java/technologies/javase-downloads.html)

## Setup
1. Clone the repository 
2. Install the java - npm package:
      ```
      npm install java
      ```
   ### Troubleshoot
   A common error is a wrong python version (must be 2.x).
   Check with 
   ```
   python --version
   ```
   For further possible fixes check [here](https://www.npmjs.com/package/java)

## Usage
Ensure that the java classes are compiled by either compiling them manually or using the *start.bat* script while beeing in the projects' root directory.
The script will automatically start the server. In case you compiled manually start the server with
```
node index.js
```

Now you can access the server in your browser at http://127.0.0.1:8000

### Possible Queries
*All of the following are relative to http://localhost:8000*
1. Given a number plate, get the car's details\
   Rest API: /car/*NumberPlate*
2. Given a person'd ID, get details of all their cars\
   Rest API: /person/*id*/car
3. Given a car color, get names of all persons having a car of that color\
   Rest API: /getPersonsByCar?color="*color*"
4. Given an age, get names of all the people who are older than that age\
   Rest API: /getPersonsOlderThan?age=*age*
5. Get names of people who have "at least one of their cars insured"\
   Rest API: /getPersonsWithInsurance
 
