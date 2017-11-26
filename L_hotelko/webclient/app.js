"use strict";

function App(){}

App.prototype = {

    renderDeviceState: function izpisi(jsonDef, jsonState) { // izpis enega service / naprave
         var output;
         //debugger;
         if (jsonDef.type === "TEMPERATURE") {
             output = '<div class="stateData" data-type="' +  jsonDef.type +'"><button data-device="' + jsonDef.device + '" data-service="' + jsonDef.service + '">TEMP</button><span></span></div>';
         } else if (jsonDef.type === "HUMIDTY") {
             output = '<div class="stateData" data-type="' +  jsonDef.type +'"><button data-device="' + jsonDef.device + '" data-service="' + jsonDef.service + '">HUMIDITY</button><span></span></div>';
         } else if (jsonDef.type === "BUTTON") {
             output = '<div class="stateData" data-type="' +  jsonDef.type +'"><button data-device="' + jsonDef.device + '" data-service="' + jsonDef.service + '">BUTTON</button><span></span></div>';
         } else if (jsonDef.type === "MOTION") {
             output = '<div class="stateData" data-type="' + jsonDef.type + '">';
             output += '<input type="hidden" value="' + jsonDef.service + '"">';
             output += '<span>GIBANJE</span>';
             if (jsonState){
                if (jsonState.value === "0") {
                     output += '<span>NE</span>';
                }
                else {
                     output += '<span>DA</span>';
                }
             }
             output += "</div>";
         } else if (jsonDef.type === "WATER") {
             output = '<div class="stateData" data-type="' + jsonDef.type +'">';
             output += '<input type="hidden" value="' + jsonDef.service + '"">';
             output += '<span>VODA</span>';

             if (jsonState){

                 if (jsonState.value === "0") {
                     output += '<span>DA</span>';
                 }
                 else {
                     output += '<span>NE</span>';
                 }
             }

             output += "</div>";
         } else if (jsonDef.type === "LIGHT") {
             output = '<div class="stateData" data-type="' + jsonDef.type +'">';
             output += '<input type="hidden" value="' + jsonDef.service + '">';
             if (jsonState){
                 if (jsonState.value === "0") {
                     output += '<span>NE GORI</span>';
                     output += '<button style="background-color:grey; color: white;">LIGHT</button>';
                 }
                 else {
                     output += '<span>GORI</span>';
                     output += '<button style="background-color:green; color: white;">LIGHT</button>';
                 }
             }else{
                 output +=  '<span>LUČ</span>';
             }
             
             output += "</div>";
         } else {
             output = "<div>&nbsp;</div>";
         }
         return output;
    },




    renderState: function(deviceId, jsonDef, jsonState){ // gre cez vse service
        // debugger;
        var that = this,
            jqFloor = $(".main .floor." + deviceId),
            buffAll = [],
            stateLookupTable = {},
            jqRoot;

        if($.isArray(jsonState)){

            $.each(jsonState, function(idx1, eltState){

                stateLookupTable[eltState.service] = eltState;

            });
        }    

        //debugger;
        $.each(jsonDef, function(idx, elt){

            if (stateLookupTable[elt.service]){
                buffAll.push(that.renderDeviceState(elt, stateLookupTable[elt.service]));    
            }else{
                buffAll.push(that.renderDeviceState(elt));
            }

        });

        jqRoot = $('.room1', jqFloor).html(buffAll.join(""));
        this.bindEvents(jqRoot);
    },

    bindEvents : function(jqRoot){

        var that = this;

       $('.stateData', jqRoot).on('click', function(){

            var ctx = $(this),
                type = $(this).data('type'),
                pin = $('input[type=hidden]', ctx).val(),
                valueStr = $('span', ctx).text(),
                parentFloor = ctx.parent().parent().parent(), //parentsUntil('.floor'),
                deviceId = parentFloor.data('deviceid');

            that.pushState(deviceId, pin, type, that.formateValue(type, valueStr));  


       });

    },

    formateValue : function(type, value){
        if (type==="LIGHT"){
            if (value === "NE GORI"){
                return "1";
            }else{
                return "0";
            }
        }
    },

    getState : function(url, cb){

        $.ajax({
            "dataType": "json",
            "type": 'POST',
            "url": window.serverUrl + url,
            "success": function(resp) {
                 console.log(resp);   
                 cb && cb(resp);
            }
        });

    },

    pushState : function(device, service, type, value){

        var objArr = {
            "device" : device,
            "service" : service,
            "type" : type,
            "value" : value
        };

        $.ajax({
            "dataType": "json",
            "type": 'POST',
            "data" : JSON.stringify(objArr),
            "url": window.serverUrl + "execute",
            "success": function(resp) {
                 console.log(resp);   
                 cb && cb(resp);
            }
        });

    }


};

var app = new App();
window.app = app;