var request = require('request');
var axios = require('axios');
var fs = require('fs');
process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

module.exports = {

  alloc: function (resource) {
    return axios.post(resource+"/alloc");
  },

  dealloc: function (resource) {
    return axios.delete(resource+"/alloc");
  },

  configure: function(resource, conf){
    return axios.put(resource+"/cfg/value", JSON.stringify(conf));
  },

  put: function(resource, value){
    return axios.put(resource+"/value", value?'1':'0');
  },

  value: function(resource){
    return axios.get(resource+"/value");
  }
};