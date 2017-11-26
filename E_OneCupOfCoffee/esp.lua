station_cfg={} station_cfg.ssid="BajtaHack" station_cfg.pwd="srmHekerji" wifi.sta.config(station_cfg) wifi.sta.connect() print(wifi.sta.getip()) 
adc.force_init_mode(adc.INIT_ADC) net.createServer(net.TCP, 30) ovenCmd = false coffeeCmd = false waterIn = true opin = 1 --d1 cpin = 2 --d2 wpin = 9 --d3 
print("Initializing gpio") gpio.mode(cpin, gpio.OUTPUT, gpio.PULLUP) gpio.mode(opin, gpio.OUTPUT, gpio.PULLUP) gpio.mode(wpin, gpio.INPUT, gpio.FLOAT) 
gpio.write(cpin, gpio.HIGH) gpio.write(opin, gpio.HIGH) temp = 0 mytimer = tmr.create() mytimer:register(1000, tmr.ALARM_AUTO, function()
    v = adc.read(0)/1024.0
    print(adc.read(0))
    temp = v*3.33*100.0
    print("update")
    print(temp)
    if gpio.read(wpin)==gpio.LOW then
        waterIn = false
    else
        waterIn = true
    end
    print(waterIn) --
    if temp < 40.0 then
        if ovenCmd == true then
            gpio.write(opin, gpio.LOW)
        end
    end
    if ovenCmd == false then
        gpio.write(opin, gpio.HIGH)
    end
    if temp > 50.0 then
        gpio.write(opin, gpio.HIGH)
    end --
    if coffeeCmd == true then
        if waterIn == false then
            gpio.write(cpin, gpio.LOW)
        else
            gpio.write(cpin, gpio.HIGH)
        end
    else
        gpio.write(cpin, gpio.HIGH)
    end
    
end) mytimer:start() function isempty(s)
  return s == nil or s == '' end function elSplit( value, inSplitPattern, outResults )
   if not outResults then
      outResults = { }
   end
   local theStart = 1
   local theSplitStart, theSplitEnd = string.find( value, inSplitPattern, theStart )
   while theSplitStart do
      table.insert( outResults, string.sub( value, theStart, theSplitStart-1 ) )
      theStart = theSplitEnd + 1
      theSplitStart, theSplitEnd = string.find( value, inSplitPattern, theStart )
   end
   table.insert( outResults, string.sub( value, theStart ) )
   return outResults end function createRequest(payload)
    local request = {}
    
    local splitPayload = elSplit(payload, "\r\n\r\n")
    local httpRequest = elSplit((splitPayload[1]), "\r\n")
    if not isempty((splitPayload[2])) then
        request.content = sjson.decode((splitPayload[2]))
    end
    
    local splitUp = elSplit((httpRequest[1]), "%s+")
    
    request.method = (splitUp[1])
    request.path = (splitUp[2])
    request.protocal = (splitUp[3])
    local pathParts = elSplit(request.path, "/")
    local maybeId = tonumber((pathParts[table.getn(pathParts)]))
    if maybeId ~= nil then
        request.fullPath = request.url
        request.path = string.sub(request.fullPath, 1, string.len(request.fullPath) - string.len("" .. maybeId))
        request.id = maybeId
    end
    --print(node.heap())
    httpRequest = nil
    splitUp = nil
    splitPayload = nil
    maybeId = nil
    collectgarbage()
    --print(node.heap())
    return request end srv=net.createServer(net.TCP) srv:listen(80,function(conn)
    conn:on("receive",function(conn,payload)
        --print("Got something...")
        local request = createRequest(payload)
        response = "HTTP/1.1 200 OK \n"
        response = response.."Content-Type:application/json\n\n"
        --print("Method: " .. request.method .. " Location: " .. request.path)
        if request.path == "/out" then
            print("out")
            c = request.content.coffee
            o = request.content.oven
            coffeeCmd = c
            ovenCmd = o
            --if coffeeCmd == true then
            -- gpio.write(cpin, gpio.LOW)
            --else
            -- gpio.write(cpin, gpio.HIGH)
            --end --
            --if ovenCmd == true then
            -- gpio.write(opin, gpio.LOW)
            --else
            -- gpio.write(opin, gpio.HIGH)
            --end
            
            print(c)
            print(o)
            
        elseif request.path == "/" then
            print("index")
            print(temp)
            print(gpio.read(wpin))
            vrednost = sjson.encode({temp=temp, water=waterIn}).."\n\n"
            print(vrednost)
            response = response..vrednost
        end
        conn:send(response,
                   function(sent) conn:close() end)
        request = nil
        collectgarbage()
    end) end)
