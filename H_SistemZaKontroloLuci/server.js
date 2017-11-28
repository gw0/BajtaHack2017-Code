var util = require("util");
var express = require('express');
var path = require('path');
var bodyParser= require('body-parser');
var multer = require('multer');
var mysql = require('mysql');
var groupBy = require('group-by');
var request = require('request');

var streznik = express();

var storage = multer.diskStorage({
  destination: function (zahteva, file, callback) {
    callback(null, __dirname+'/public/uploads/')
  },
  filename: function(zahteva, file, callback) {
      callback(null, "room_"+zahteva.body.roomId+".map");
  }
});

var upload = multer({ storage: storage });

streznik.use(upload.single('image'));
streznik.use(bodyParser.json());
streznik.use(express.static(path.join(__dirname, 'public')));


var fs = require('fs');
var http = require('http');

var httpServer = http.createServer(streznik);

pool = mysql.createPool({
	    host: "192.168.0.101",
	    user: "root",
	    password: "root",
	    database: "bajtahack",
	    charset: "UTF8_GENERAL_CI"
	});



httpServer.listen(80, function(){
	console.log("Streznik posluša na vratih 80.");
});

streznik.get("/", function(zahteva, odgovor){
  console.log("Prejel sem zahtevo na /");
	odgovor.sendFile(path.join(__dirname, 'public', 'dashboard.html'));
})

streznik.get("/room:id", function(zahteva, odgovor){
  console.log("Prejel sem zahtevo na /room" +zahteva.params.id);
	odgovor.sendFile(path.join(__dirname, 'public', 'room.html'));
})

streznik.post("/uploadRoomMap", function(zahteva, odgovor){

  var idSobe=zahteva.body.roomId;
  var imeSlike=zahteva.file.originalname;
  console.log("Dobil sem zahtevek na /addRoomMap roomId=", zahteva.body.roomId," imeSlike=",imeSlike);
	odgovor.redirect("/");
})

streznik.post("/addRoom", function(zahteva,odgovor){
	pool.getConnection(function(napaka1, connection) {
		var name = zahteva.body.roomName;
		var roomDescri = zahteva.body.roomDescription;

		if (!napaka1) {
			console.log(name);
			//console.log(roomDescription);
			var post  = {roomName: name, roomDescription: roomDescri, ipAddress:zahteva.body.ipAddress};
			var query = connection.query('INSERT INTO room SET ?', post, function (error, results, fields) {
			if (error) throw error;
				//sth,

			});

			connection.release();

		} else {
			odgovor.json({
				uspeh:false,
				odgovor:"Napaka pri vzpostavitvi povezave z podatkovno bazo!"
			});
		}
	});
})

streznik.post("/addController", function(zahteva,odgovor){
	pool.getConnection(function(napaka1, connection) {
		var romID = zahteva.body.roomID;
		var ip = zahteva.body.ipAddress;
		if (!napaka1) {
			console.log(romID);
			console.log(ip);
			var post  = {ipAddress: ip};
			var query = connection.query('INSERT INTO controller SET ?', post, function (error, results, fields) {
				if (error) throw error;
				else{
					var conID = results.insertId;
					console.log(results)
					console.log(conID)
					var post1  = {roomID: romID};
					var query1 = connection.query('UPDATE room SET controllerID =' +conID +' WHERE roomID = '+ romID, function (error, results, fields) {
						if (error) throw error;
					});
				}
			});

			connection.release();

		} else {
			odgovor.json({
				uspeh:false,
				odgovor:"Napaka pri vzpostavitvi povezave z podatkovno bazo!"
			});
		}
	});
})

streznik.post("/saveConfiguration", function(zahteva,odgovor){
	pool.getConnection(function(napaka1, connection) {
		var tabela = zahteva.body.lightsConfiguration;
		if (!napaka1) {
			console.log(zahteva.body.lightsConfiguration[0])
			var query = connection.query('DELETE FROM room_lights WHERE roomID ='+zahteva.body.roomID, function (error, results, fields) {
				if (error) throw error;
				else{
					for(var i = 0; i< tabela.length; i++){
						var post  = {roomID: zahteva.body.roomID, offsetX: tabela[i].offsetX, offsetY: tabela[i].offsetY, gpioPin: tabela[i].gpioPin, lightStatus: tabela[i].status};
						var query = connection.query('INSERT INTO room_lights SET ?', post, function (error, results, fields) {
							if (error) throw error;

						});

					}
				}
			});
      connection.release();
		} else {
			odgovor.json({
				uspeh:false,
				odgovor:"Napaka pri vzpostavitvi povezave z podatkovno bazo!"
			});
		}
	});
})

streznik.post("/removeRoom", function(zahteva,odgovor){
	pool.getConnection(function(napaka1, connection) {
		if (!napaka1) {
			var query = connection.query('DELETE FROM room WHERE roomID ='+zahteva.body.roomID, function (error, results, fields) {
				if (error) throw error;

			});
      connection.release();
		} else {
			odgovor.json({
				uspeh:false,
				odgovor:"Napaka pri vzpostavitvi povezave z podatkovno bazo!"
			});
		}
	});
})

streznik.get("/getRooms", function(zahteva,odgovor){
	pool.getConnection(function(napaka1, connection) {
		if (!napaka1) {

			var query = connection.query('SELECT * FROM room r', function (error, results, fields) {
				if (error) throw error;
				//console.log(results[0]);

				var tabela_data = [];

				console.log(results);
				if(results.length==0){
					console.log("RESULTS JE PRAZNA!!!!!!!!!!!!!!!!!!!!!!")
					odgovor.send([]);
				} else{
					var objekt0 = {
  					id:results[0].roomID,
  					name:results[0].roomName,
            controller:{ip:results[0].ipAddress, connection:results[0].connected, configuration: results[0].configured},
            lights:[]
					};

					for(var i=1; i<results.length; i++){
						if( results[i-1].roomID !=  results[i].roomID){
							tabela_data.push(objekt0);
							var objekt1={
								id:results[i].roomID,
								name:results[i].roomName,
                controller:{ip:results[i].ipAddress, connection:results[i].connected, configuration: results[i].configured},
                lights:[]
							}
							objekt0=objekt1;
						}
					}
					tabela_data.push(objekt0);
					odgovor.send(tabela_data);
					console.log(tabela_data);
				}
			});
      connection.release();
		} else {
			odgovor.json({
				uspeh:false,
				odgovor:"Napaka pri vzpostavitvi povezave z podatkovno bazo!"
			});
		}
	});
})

streznik.get("/getRoomLight:id", function(zahteva,odgovor){

  tableLights=[];

  pool.getConnection(function(napaka1, connection) {
		if (!napaka1) {
			var query = connection.query('SELECT l.roomID,l.offsetX,l.offsetY,l.gpioPin,l.lightStatus  FROM room_lights l WHERE l.roomID='+zahteva.params.id, function (error, results, fields) {
				if (error) throw error;

        for( var i=0; i<results.length;i++){
          var lights={
						offsetX:results[i].offsetX,
						offsetY:results[i].offsetY,
						gpioPin:results[i].gpioPin,
						status:results[i].lightStatus,
            selected:false
					};
          tableLights.push(lights);
        }
        console.log(tableLights);
        odgovor.send(tableLights);
			});
      connection.release();
		} else {
			odgovor.json({
				uspeh:false,
				odgovor:"Napaka pri vzpostavitvi povezave z podatkovno bazo!"
			});
		}
	});
})

streznik.post("/updateConfiguration", function(zahteva,odgovor){
  var ip=zahteva.body.ip;
  var controllerConfiguration=zahteva.body.configuration;
  console.log("Posiljam na IP ", ip ," konfiguracija ", controllerConfiguration);

  try{
    request({
  		url: "http://"+ip+":40005/updateConf",
  		json: true,
  		method: 'POST',
  		body: controllerConfiguration

    	}, function(error, response, body) {
    		//console.log(response);
    		console.log(body);
    		//console.log(error)
        });
    }catch(e){

    }

});

/*setInterval(function(){
	var options = {
	  host: '192.168.0.130',
	  path: '/status',
	  port: '40005'
	};

  try{
    callback = function(response) {
  	  var str = ''
  	  response.on('data', function (chunk) {
  		str += chunk;
  	  });

      response.on('error', function(error) {
        console.log("Ni povezave z ",options.host);
      });

  	  response.on('end', function () {
  		console.log(str);

  		var conf = 0;
  		var conn = 0;
        var a=JSON.parse(str);
    		if(a.configured == true) { conf = 1; conn = 1}
    		pool.getConnection(function(napaka1, connection) {
    			if (!napaka1) {
    				var query = connection.query('UPDATE room SET connected = '+conn+' , configured = '+conf+ ' WHERE ipAddress LIKE ?',options.host,  function (error, results, fields) {
    					if (error) throw error;

    				});
            connection.release();
    			} else {

    			}
    		})

  	  });
  	}
  }catch(e){
    console.log(e);
  }
  try{
      var req = http.request(options, callback);

    req.on('error', function(error) {
      console.log("Ni povezave z ",options.host);
    });
    req.end();

  }catch(e){
    console.log(e);
  }


}, 5000);*/

streznik.post("/getIPAddress", function(zahteva,odgovor){
  pool.getConnection(function(napaka1, connection) {
		if (!napaka1) {
			var query = connection.query('SELECT r.ipAddress FROM room r WHERE r.roomID ='+zahteva.body.roomID, function (error, results, fields) {
				if (error) throw error;
        odgovor.json({ip:results[0].ipAddress});
			});
      connection.release();
		} else {
			odgovor.json({
				uspeh:false,
				odgovor:"Napaka pri vzpostavitvi povezave z podatkovno bazo!"
			});
		}
	});

});
