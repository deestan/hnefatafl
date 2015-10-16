var PORT = 8000;

var express = require('express');
var app = express();
var proxy = require('express-http-proxy');
var server = require('http').Server(app);
var io = require('socket.io')(server);

var apiProxy = proxy('localhost:8080/', {
    forwardPath: function (req, res) {
        return require('url').parse(req.url).path;
    }
});
app.use("/api", apiProxy);
app.use('/', express.static(__dirname + '/app'));

io.on('connection', function(socket) {
  socket.on('join', function(gameId) {
    socket.rooms.forEach(function (roomId) {
      socket.leave(roomId);
    });
    io.to(gameId).emit('request board');
    socket.join(gameId);
  });
  socket.on('board', function(board) {
    socket.rooms.forEach(function (roomId) {
      io.to(roomId).emit('board', board);
    });
  });
  socket.on('chat', function(message) {
    socket.rooms.forEach(function (roomId) {
      io.to(roomId).emit('chat', message);
    });
  });
});

server.listen(PORT);
console.log("Webserver listening on port " + PORT);
