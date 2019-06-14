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
    var connection = request.accept();
    connection.name = uuidv1(); 
    console.log((new Date()) + ' Connection accepted.');
    connections.push(connection); 
    connection.on('message', function(message) {
        console.log("er is een bericht binnengekomen", message);
        processMessage(connection, message);
    });
    connection.on('close', function(reasonCode, description) {
        console.log((new Date()) + ' Peer ' + connection.user || connection.remoteAddress + ' disconnected.');
        broadcastAll(connection, `${connection.user} has left the building`);
        connections.splice(connections.indexOf(connection),1);
        
    });
});

function processMessage(connection, message) {
    let messagedata = message.utf8Data;
    if (messagedata.startsWith("EXECUTEBAN2:")){
        let user = messagedata.replace("EXECUTEBAN2:", ""); 
        if (connection.user == "john")
        {
            let target = connections.find(c => c.user == user); 
            if (target) {
                console.log("GOODBYE: " + user);
                broadcast(connection, `${user} is kicked out of this building`);
                target.close();
            }
        }
    }
    if (messagedata.startsWith("EXECUTEBAN:")){
        let user = messagedata.replace("EXECUTEBAN:", ""); 
        if (connection.user == "john")
        {
            let target = connections.find(c => c.user == user); 
            if (target) {
                console.log("GOODBYE: " + user);
                broadcast(connection, `${user} is kicked out of this room`);
                target.room = undefined;
                target.sendUTF('you are kicked out, beware!!');
            }
        }
    }
    if (messagedata.startsWith("BAN:")){
        let user = messagedata.replace("BAN:", ""); 
        let adminconnection = connections.find(c => c.user = "john");
        if (adminconnection) {
            adminconnection.sendUTF("BAN REQUEST FROM " + connection.user + " FOR " + user);
        }
    }
    if (messagedata.startsWith("USER:")){
        let user = messagedata.replace("USER:", ""); 
        let existing = connections.find(c => c.user == user);
        if (!existing) {
            connection.user = user;
            broadcastAll(connection, `${connection.user} has entered the building`);
            connection.sendUTF("User accepted!");
        } else {
            connection.sendUTF("Invalid username, user already exists!");
        }
    }
    if (messagedata.startsWith("STATUS")){
        let rooms = connections.map(c => c.room);
        let ur = [...new Set(rooms)];
        let statusmessage = `There are ${connections.length} persons in ${ur.length} rooms:`;
        let orderedrooms= connections.sort((a, b) => a.room > b.room);
        let currentroom = -1;
        let people = 0;
        let names = [];
        for (let r of orderedrooms) {
            if (currentroom == -1) { currentroom = r.room; people = 1;  names.push(r.user);}
            else {
                if (currentroom != r.room) {
                    statusmessage = statusmessage + `\r\n\troom ${currentroom}: ${people} people (${names.join(", ")})`;
                    people = 1;
                    names = [];
                    names.push(r.user);
                    currentroom = r.room;
                } else {
                    people++;
                    names.push(r.user);
                }
            }
        }
        if (people > 0) statusmessage = statusmessage + `\r\n\troom ${currentroom}: ${people} people (${names.join(", ")})`;
        console.log("sending status", statusmessage);
        let yourconnection = connections.find(c => c.user == connection.user);
        if (yourconnection){
            statusmessage = statusmessage + `\r\nYou are in room ${yourconnection.room}`;
        }
        connection.sendUTF(statusmessage);
    }
    if (messagedata.startsWith("ROOM:")) {
        let room = messagedata.replace("ROOM:", "");
        console.log(`connection changed from room ${connection.room} to room ${room}`);
        if (connection.room) broadcast(connection, `${connection.user} has left room ${connection.room}`);
        connection.room = room;
        broadcast(connection, `${connection.user} has entered room ${connection.room}`);
    }
    if (messagedata.startsWith("MESSAGE:")) {
        let message = messagedata.replace("MESSAGE:", "");
        broadcast(connection, message, true);
    }
}
function broadcast(connection, message, saysprefix){
    connections.forEach(c => {
        //if (connection.name != c.name) {
            if (connection.room == c.room) {
                console.log("we gaan nu een bericht versturen naar room: " + connection.room + ", bericht: " + message);
                if (saysprefix) c.sendUTF(`${connection.user} says: ${message}`);
                else c.sendUTF(`${message}`);
            }
       // }
    });
}

function broadcastAll(connection, message, saysprefix){
    connections.forEach(c => {
        console.log("we gaan nu een bericht versturen naar room: " + connection.room + ", bericht: " + message);
        if (saysprefix) c.sendUTF(`${connection.user} says: ${message}`);
        else c.sendUTF(`${message}`);
    });
}
