package bajtahack.database;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/status")
public class DeviceStatusServlet extends HttpServlet  {
    
    private static final long serialVersionUID = 1L;
    public static final java.util.logging.Logger logger = LoggingFactory.loggerForThisClass();
    
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { // for debugging
        
        final String template = "<!DOCTYPE html>\r\n" + 
                "<html>\r\n" + 
                "<head>\r\n" + 
                "    <meta charset=\"utf-8\">\r\n" + 
                "    <title>bajtahack L status </title>\r\n" + 
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n" + 
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n" + 
                "    <meta http-equiv=\"refresh\" content=\"10\">\r\n" + 
                "</head>\r\n" + 
                "<body>\r\n" + 
                "<h1>Device registry</h1>\r\n" + 
                "%s\r\n" + 
                "<body>\r\n" + 
                "</body>\r\n" + 
                "</html>";
        
        
        try {
            final StringBuilder table = new StringBuilder("<table style=\"width: 100%; border: 1px solid black;\">"
                    + "<thead><tr>"
                    + "<th style=\"width:10%;\">DNS NAME</th>"
                    + "<th style=\"width:10%;\">Last active</th>"
                    + "</tr></thead>"
                    + "<tbody>");
            
            final Map<String, Device> userSessionMap = DeviceRegistry.instance.DEVICE_MAP;
            final Set<Entry<String, Device>> entrySet2 = userSessionMap.entrySet();
            for (Entry<String, Device> userEntry : entrySet2) {
                final Device user = userEntry.getValue();
                table.append("<tr>");
                table.append("<td>" + user.getId() + "</td>");
                table.append("<td>" + user.getLastModified() + "</td>"); // 2
                table.append("</tr>");
            }
            
            table.append("</tbody></table>");
            
            response.setContentType("text/html");
            response.getOutputStream().print(String.format(template, table.toString())); 
            response.getOutputStream().flush();
            
            // PoolDatabase.instance.get();
            
            return;
        }
        catch (Exception problem) {
            problem.printStackTrace();
        }
        
    }
    
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        final Map<String, Device> userSessionMap = DeviceRegistry.instance.DEVICE_MAP;
        final Set<Entry<String, Device>> entrySet2 = userSessionMap.entrySet();
        
        for (Iterator iterator = entrySet2.iterator(); iterator.hasNext();) {
            Entry<String, Device> entry = (Entry<String, Device>) iterator.next();
            final Device d = entry.getValue();
            sb.append(String.format("{ \"id\":\"%s\", \"last\": \"%s\"", d.getId(), d.getLastModified()));
            
            final Set<DeviceState> state = d.getState();
            if (!state.isEmpty()) {
                sb.append(",\"state\": [");
                for (Iterator iterator2 = state.iterator(); iterator2.hasNext();) {
                    DeviceState deviceState = (DeviceState) iterator2.next();
                    sb.append(deviceState.toJsonString());
                    if (iterator2.hasNext()) sb.append(",");
                }
                sb.append("]");
            }
            
            sb.append(", \"devTypes\": " + d.getSuportedDevices());
            
            sb.append("}");
            
            if (iterator.hasNext()) sb.append(",");
        }
        
        sb.append("]");
        response.setContentType("application/json");
        response.getOutputStream().print(sb.toString()); 
        response.getOutputStream().flush();
        return;
    }
    
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
    
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }
    

}
