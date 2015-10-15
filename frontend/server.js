var PORT = 8000;
var WEBSOCKET_PORT = 8001;

var express = require('express');
var proxy = require('express-http-proxy');
var io = require('socket.io')(WEBSOCKET_PORT);

var app = express();

var apiProxy = proxy('localhost:8080/', {
    forwardPath: function (req, res) {
        return require('url').parse(req.url).path;
    }
});
app.use("/api", apiProxy);
app.use('/', express.static(__dirname + '/app'));

io.on('connection', function(socket) {
  socket.on('board', function(board) {
    io.emit('board', board);
  });
  socket.on('request-board', function(board) {
    io.emit('request-board', board);
  });
});

app.listen(PORT);
console.log("Webserver listening on port " + PORT);
console.log("Websockets listening on port " + WEBSOCKET_PORT);
