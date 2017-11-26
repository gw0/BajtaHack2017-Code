var srm = require('./srm');
var axios = require('axios');

var url = "https://e3.srm.bajtahack.si:29300";
var heater = url+"/phy/gpio/26";
var light = url+"/phy/gpio/27";
var motion = url+"/phy/gpio/5";
var state = {
  heater: false,
  light: false,
  motion: false
}

module.exports = {
  init: function () {
    console.log("Bathroom initialization: "+url);
    //allocate();
    configure();
  },
  poll: function () {
    axios.all([
      srm.put(heater, state.heater),
      srm.put(light, state.light),
      srm.value(motion)
    ])
    .then(function(results){
      state.motion = (results[2].data=='1');
    });
  },
  getState: function () {
    return state;
  },
  handle: function(data){
    if(data.light != undefined){
      state.light = data.light;
    }
    if(data.heater != undefined){
      state.heater = data.heater;
    }
    console.log(state);

  }
};

var allocate = function () {
  axios.all([
    srm.alloc(heater),
    srm.alloc(light),
    srm.alloc(motion)
  ])
  .then(function(results){
  });
}

var configure = function(){
  axios.all([
    srm.configure(heater, {dir: "out"}),
    srm.configure(light, {dir: "out"}),
    srm.configure(motion, {dir: "in", mode: "pullup"})
  ])
  .then(function(results){});
}