package bajtahack.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import bajtahack.database.Device.DeviceType;
import bajtahack.json.JsonUtils;
import bajtahack.services.gpio;


@WebServlet("/execute")
public class ExecuteServlet extends HttpServlet  {
    
    private static final long serialVersionUID = 1L;
    public static final java.util.logging.Logger logger = LoggingFactory.loggerForThisClass();
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);     
    }
    
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        
        final Map<String, ?> jsonRequest = JsonUtils.toJson(request.getReader());
        final String deviceId = JsonUtils.getStringFromMap(jsonRequest, "device").replace("\"", "").trim();
        final String serviceId = JsonUtils.getStringFromMap(jsonRequest, "service").replace("\"", "").trim();
        final String type = JsonUtils.getStringFromMap(jsonRequest, "type").replace("\"", "");
        final DeviceType typeEnum = DeviceType.fromString(type);
        final String value = JsonUtils.getStringFromMap(jsonRequest, "value").replace("\"", "");
        
        final Device device = DeviceRegistry.instance.getDevice(deviceId);
        
        if (deviceId == null || serviceId == null || type == null) {
            logger.severe("Unknown device or service ");
            return;
        }
        
        logger.info("Notify received from device:" + deviceId + " service: " + serviceId + ",type: " + type);
        
        switch(typeEnum) {
        
            case LIGHT : {
                                                  
                gpio.instance.led(device.getIp(), serviceId, value);
       
                break;
            }
       
        }     
        
        
        
        //final DeviceState st = DeviceState.fromNotify(serviceId, type, rslt.toString());
        //st.setDevice(deviceId);
        
        //Device device = DeviceRegistry.instance.getDevice(deviceId);
        //if (device != null)
        //    device.addState(st);
        
    }
    
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);     
    }
    
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);     
    }
    

}
