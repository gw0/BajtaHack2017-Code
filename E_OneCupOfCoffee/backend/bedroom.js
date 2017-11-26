var io_utility = "./io";
qs = require('querystring');
var exec = require('child_process').exec;

var light = 8;
var bed = 9;
var motion = 7;

var state = {
  bed: false,
  light: false,
  motion: false,
  alarm: "06:00",
  ringing: false
}

module.exports = {
  init: function () {
    console.log("Bedroom initialization. (RPi)");
    configure();
  },
  poll: function () {
    exec(io_utility+" gpio "+light+" "+(state.light?1:0), function(error, stdout, stderr) { });
    exec(io_utility+" gpio "+bed, function(error, stdout, stderr) {
      state.bed = !(parseInt(stdout) === 1);
    });
    exec(io_utility+" gpio "+motion, function(error, stdout, stderr) {
      state.motion = parseInt(stdout) === 1;
    });
  },
  getState: function () {
    return state;
  },
  handle: function(data){
    if(data.light != undefined){
      state.light = data.light;
    }
    if(data.alarm != undefined){
      state.alarm = data.alarm;
    }
    console.log(state);
  }
};

var configure = function(){
  exec(io_utility+" gpio "+light+" out", function(error, stdout, stderr) { 
    exec(io_utility+" gpio "+motion+" in up", function(error, stdout, stderr) { 
      exec(io_utility+" gpio "+bed+" in up", function(error, stdout, stderr) { });
    });
  });
  
  
}
/*
process.on('SIGINT', function () {
  light.unexport();
  motion.unexport();
  bed.unexport();
});*/