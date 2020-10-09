const express = require("express");
const app = express();
const port = 8000;

/*
  setup the JVM
*/

var java = require("java");
java.classpath.push(".");
var instance = java.newInstanceSync("javasrc.XmlData", "res/sample.xml");

/*
 * Routing
 */
app.get("/", (req, res) => {
  res.send("hello");
});

app.get("/car/:numberPlate", (req, res) => {
  // get data
  java.callMethod(
    instance,
    "detailsByNumberPlate",
    req.params.numberPlate,
    function(err, results) {
      if (err) console.log(err);
      res.send(
        `details for numberPlate: ${req.params.numberPlate}:\n\ <br> ${results}`
      );
    }
  );
});

app.get("/person/:id/car", (req, res) => {
  //get data
  java.callMethod(
    instance,
    "carDetailsByPersonId",
    parseInt(req.params.id),
    function(err, results) {
      if (err) console.log(err);
      res.send(results);
    }
  );
});

app.get("/getPersonsByCar", (req, res) => {
  java.callMethod(
    instance,
    "getPersonsByCarColor",
    req.query.color.replace(/"/g, ""),
    function(err, results) {
      if (err) console.log(err);
      res.send(results);
    }
  );
});

app.get("/getPersonsOlderThan", (req, res) => {
  java.callMethod(
    instance,
    "getPersonsOlderThan",
    parseInt(req.query.age),
    function(err, results) {
      if (err) console.log(err);
      res.send(results);
    }
  );
});

app.get("/getPersonsWithInsurance", (req, res) => {
  java.callMethod(instance, "getPersonsWithInsurance", function(err, results) {
    if (err) console.log(err);
    res.send(results);
  });
});

/*
 * start server on the specified port
 */
app.listen(port, () =>
  console.log(`Server running on port http://127.0.0.1:${port}`)
);
