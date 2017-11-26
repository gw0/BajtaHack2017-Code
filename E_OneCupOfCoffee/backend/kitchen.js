var srm = require('./srm');
var axios = require('axios');

var url = "http://193.2.179.211";
var outputs = url+"/out";
var inputs = url+"/";
var state = {
  coffee: false,
  oven: false,
  temperature: 0,
  water: false
}

module.exports = {
  init: function () {
    console.log("Kitchen initialization: "+url);
  },
  poll: function () {
    axios.all([
      axios.get(inputs),
      axios.post(outputs, state)
    ])
    .then(function(results){
      state.water = results[0].data.water;
      state.temperature = results[0].data.temp;
    });
  },
  getState: function () {
    return state;
  },
  handle: function(data){
    console.log(state);
    if(data.oven != undefined){
      state.oven = data.oven;
      
    }
    if(data.coffee != undefined){
      state.coffee = data.coffee;
    }
  }
};