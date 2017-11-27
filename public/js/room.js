$(document).ready(function(){

  $.ajax({
      url:'/getRoomLight'+getRoomID(),
      type:'get',
      contentType: 'application/json',
      async: true,


      success:function(odgovor){

        var room1={
          id:getRoomID(),
          lights:odgovor
        };
        console.log(odgovor)

        var room = new Vue({
          el: '#room',
          data: {
            room:room1
          },
          methods: {
            redirect: function(room){
              console.log("redirect on /room",room.id)
              window.location.replace("/room"+room.id);
            },
            changeStateOfLight: function(room, lightID){
              console.log("Klik zaznan!")
              var slovar={}
              var Gstates={};
              room.lights[lightID].status=!room.lights[lightID].status;
              for(var i=0; i<room.lights.length; i++){
                var lightEl={
                  gpioPin:room.lights[i].gpioPin,
                  status:parseInt(room.lights[i].status*1)
                }

                Gstates[lightEl.gpioPin]=[lightEl.status, "out"];
                slovar["Gstates"]=Gstates;

              }

              console.log(JSON.stringify(Gstates));

              $.ajax({
                  url:'/getIPAddress',
                  type:'post',
                  contentType: 'application/json',
                  async: true,
                  data: JSON.stringify({
                    roomID: getRoomID()
                  }),

                  success:function(response){
                    console.log("IP--------",response)
                    $.ajax({
                        url:'/updateConfiguration',
                        type:'post',
                        contentType: 'application/json',
                        async: true,
                        data: JSON.stringify({
                          ip:response.ip,
                          configuration: Gstates
                        }),

                        success:function(){
                            console.log("Zahtevek za dodajanje sobe uspešno poslan!")
                        },
                        error: function(e){
                          console.log(e);
                        }
                    });
                  },
                  error: function(e){
                    console.log(e);
                  }
              });
            },
            roomMapPath: function(room){
              return "uploads/room_"+room.id+".map";
            },
            drawRoomMapOnCanvas: function(room){
              var svg=$('#tloris');

              var imageObj = new Image();
              imageObj.src = this.roomMapPath(room);

              var sirinaSlike= $(window).width()/2;
              var visinaSlike= parseInt((sirinaSlike*imageObj.height)/imageObj.width);

              svg.width(sirinaSlike);
              svg.height(visinaSlike);

              var roomMap=createImageElement(sirinaSlike, visinaSlike, imageObj.src, 0, 0 );
              svg.append(roomMap);

              var light = new Image();
              light.src = "images/light_OFF.png";
              sirinaSlike= 40;
              visinaSlike= 40;
              console.log("Stevilo luči, ki so že dodane", room.lights.length)

              for (var i=0; i< room.lights.length;i++){
                var x=room.lights[i].offsetX*svg.width()-sirinaSlike/2;
                var y=room.lights[i].offsetY*svg.height()-visinaSlike/2;

                var novaLuc=createImageElement(sirinaSlike, visinaSlike, "images/light_OFF.png", x, y);
                room.lights[i].objectPointer=novaLuc; // assing each light object pointer for easy manage
                room.lights[i].selected=false;
                svg.append(novaLuc);
              }


            },
            selectLight: function(room){
              var svg=$('#tloris');

              var svgAlt=document.getElementById('tloris');

              var elements = svgAlt.childNodes;
              for (var i=1;i<elements.length;i++){
                   elements[i].addEventListener('click', function(element){
                     for(var i = 0; i<room.lights.length; i++){

                        if(this==room.lights[i].objectPointer){
                          for(var j = 0; j<room.lights.length; j++){
                            room.lights[j].selected=false;
                          }
                          room.lights[i].selected=true;
                          console.log("Zaznan klik na zarnico")
                        }
                     }
                   });
               }

            }

          },
          computed: {

          }

        });

        room.drawRoomMapOnCanvas(room1);
        room.selectLight(room1);

      },
      error: function(e){
        console.log(e);
      }
  });



});



function getRoomID(){
  return parseInt((window.location+"").split("room")[1]);
}

function getMousePos(canvas, evt) {
    var rect = canvas.getBoundingClientRect();
    return {
        x: evt.clientX - rect.left,
        y: evt.clientY - rect.top
    };
}

function createImageElement(width, height, src, x, y ){
  var el = document.createElementNS('http://www.w3.org/2000/svg','image');
  el.setAttributeNS(null,'height',height);
  el.setAttributeNS(null,'width',width);
  el.setAttributeNS('http://www.w3.org/1999/xlink','href', src);
  el.setAttributeNS(null,'x',x);
  el.setAttributeNS(null,'y',y);
  el.setAttributeNS(null, 'visibility', 'visible');

  return el;
}
