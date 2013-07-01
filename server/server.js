
var pg = require('pg'),
	io = require('socket.io');
var conString = "tcp://postgres:postgres@localhost:54321/geomonsters";

var socket;


function start(port) {
	
	socket = io.listen(port);
	console.log("Listening to port.");
	
	socket.sockets.on("connection", onConnected);
	
};
	
function onConnected(socket) {

	console.log("Client Connected");
	socket.emit("message", {message: "connected"});
	
	
	socket.on("user message", onMessage);
	socket.on("user position", onLocation);
	
};

function onMessage(data) {
	console.log("Message received: " + data.message);
}

function onLocation(data) {
	console.log("Position received. Longitude: " + data.longitude + ", latitude: " + data.latitude);
	
	client = this;
	
	// connect to spatial database
	var spatial_client = new pg.Client(conString);
	spatial_client.connect();
	
	// the query
	var theQuery = "SELECT objectid, geom, ST_Distance(ST_Transform(ST_GeomFromText('POINT(" + data.longitude + " " + data.latitude + ")', 4326), 3400), geom) As dist FROM watercourses ORDER BY dist ASC LIMIT 1;";
	var query = spatial_client.query(theQuery);
	
	//can stream row results back 1 at a time
	query.on('row', function(result) {
		console.log("Watercourse within " + result.dist + " metres");
		
		if (result.dist < 100)
			client.emit("result", {value: "w"});
		else
			client.emit("result", {value: "g"});
	});
	
	// on query completion, close the connection
	query.on('end', function() {
		console.log("Query ended");
		spatial_client.end();
	});

}
	
start(8000);