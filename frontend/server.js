var PORT = 8000;

var express = require('express');
var proxy = require('express-http-proxy');

var app = express();

var apiProxy = proxy('localhost:8080/', {
    forwardPath: function (req, res) {
        return require('url').parse(req.url).path;
    }
});
app.use("/api", apiProxy);
app.use('/', express.static(__dirname + '/app'));

app.listen(PORT);
console.log("Listening on port " + PORT);
