package bajtahack.database;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/notify")
public class NotifyServlet extends HttpServlet  {
    
    private static final long serialVersionUID = 1L;
    public static final java.util.logging.Logger logger = LoggingFactory.loggerForThisClass();
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);     
    }
    
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        
        final String deviceId = request.getParameter("device");
        final String serviceId = request.getParameter("service");
        final String type = request.getParameter("type");
        
        if (deviceId == null || serviceId == null || type == null) {
            logger.severe("Unknown device or service ");
            return;
        }
        
        logger.info("Notify received from device:" + deviceId + " service: " + serviceId + ", type: " + type);
        
        final BufferedReader reader = request.getReader();
        String line = null;
        final StringBuilder rslt = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            rslt.append(line);
        }
        
        logger.info("Payload : " + rslt.toString());
        
        final DeviceState st = DeviceState.fromNotify(serviceId, type, rslt.toString());
        st.setDevice(deviceId);
        
        Device device = DeviceRegistry.instance.getDevice(deviceId);
        if (device != null)
            device.addState(st);
        
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
