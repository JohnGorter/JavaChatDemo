var express = require('express');
var app = express(); 
var http = require('http');
var websocketserver = require('websocket').server;
const uuidv1 = require('uuid/v1');


var server = http.createServer(); 
app.use(express.static('.'));
server.listen(8085, () => { console.log("listening")});

var websocket = new websocketserver({httpServer:server});


const connections = [];
websocket.on('request', function(request) {

    console.log((new Date()) + ' Connection from origin ' + request.origin + ' rejected.');
    var connection = request.accept();
    connection.name = uuidv1(); 
    console.log((new Date()) + ' Connection accepted.');
    connections.push(connection); 

    connection.on('message', function(message) {
        console.log("er is een bericht binnengekomen", message);
        connections.forEach(c => {
            console.log(`testing connection ${c.name} with ${connection.name}`);
            if (connection.name != c.name) {
                console.log("we gaan nu een bericht versturen..");
                c.sendUTF(message.utf8Data);
            }
        });
    });
    connection.on('close', function(reasonCode, description) {
        console.log((new Date()) + ' Peer ' + connection.remoteAddress + ' disconnected.');
        connections.splice(connections.indexOf(connection),1);
    });
});
