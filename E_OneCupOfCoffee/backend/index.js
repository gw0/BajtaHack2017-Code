const express = require('express');
const app = express();
var connect = require('connect');
var request = require('request');
var kitchen = require('./kitchen');
var bathroom = require('./bathroom');
var bedroom = require('./bedroom')
var exec = require('child_process').exec;
var update_interval = 2000;

var bodyParser = require('body-parser');
//app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json())

var user = {
  location: 'kitchen',
  water: false
};

function getTime(date = new Date()) {
  var hour = date.getHours();
  hour = (hour < 10 ? "0" : "") + hour;
  var min  = date.getMinutes();
  min = (min < 10 ? "0" : "") + min;
  return hour + ":" + min;
}

initialize();
function updateUser() {
  var bedroomState = bedroom.getState();
  var kitchenState = kitchen.getState();
  var bathroomState = bathroom.getState();

  if (bathroomState.motion) {
    user.location = "bathroom"
  }
  else if (bedroomState.motion || bedroomState.bed) {
    user.location = "bedroom"
  }
  else {
    user.location = "kitchen"
  }
  bathroom.handle({light: bathroomState.motion});
  bedroom.handle({light: bedroomState.motion && !bedroomState.bed});

  user.water = kitchenState.waterSensor;
}

//update();
setInterval(update , update_interval);

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

app.use('/', express.static('../frontend/dist/'));

app.get('/user', function(req, res) {
   res.send(user);
});

app.post('/kitchen', function(req, res){
  console.log(req.body);
  kitchen.handle(req.body);
  res.send("received");
});

app.get('/kitchen', function(req, res){
  res.send(kitchen.getState());
});

app.post('/bathroom', function(req, res){
  console.log(req.body);
  bathroom.handle(req.body);
  res.send("received");
});

app.get('/bathroom', function(req, res){
  res.send(bathroom.getState());
});

app.post('/bedroom', function(req, res){
  console.log(req.body);
  bedroom.handle(req.body);
  res.send("received");
});

app.get('/bedroom', function(req, res){
  res.send(bedroom.getState());
});

var port = 8080;
app.listen(port, () => console.log('API running on port ' + port))

function initialize(){
  bathroom.init()
  bedroom.init()
}
var ringing = false;
function update(){
  console.log("Update");
    kitchen.poll();
  bathroom.poll();
  bedroom.poll();
  var bedroomState = bedroom.getState();
  if(getTime() == bedroomState.alarm && ringing == false){
    console.log("ring");
    exec("omxplayer -o local OneCupOfCoffe.mp3", function(error, stdout, stderr) { 
      
    });
    bathroom.handle({heater: true});
    kitchen.handle({oven: true, coffee: true});
    ringing = true;
  }
  updateUser();
}