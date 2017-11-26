package bajtahack.database;

import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import bajtahack.easysql.Database;
import bajtahack.easysql.SqlQueryParam;
import bajtahack.easysql.StatementWithParams;

public class Device {
    
    public static enum DeviceType {
        TEMPERATURE,
        HUMIDTY,
        MOTION,
        LIGHT,
        BUTTON,
        WATER;
        
        public static DeviceType fromString(String name) {
            return DeviceType.valueOf(name);
        }
    }
    
    public static final java.util.logging.Logger logger = LoggingFactory.loggerForThisClass();
    
    final String id;
    final String ip;
    private Date lastModified;
    
    private Map<String, DeviceType> suportedDevices = new HashMap<String, DeviceType>();
    
    private Set<DeviceState> state = new HashSet<DeviceState>();
    
    public Device(String ip, Date lastModified) {
        super();
        this.id = ip.substring(0, 2);
        this.ip = ip;
        this.lastModified = lastModified;
        
        if (this.id.equalsIgnoreCase("l1")) {
            suportedDevices.put("26", DeviceType.BUTTON);
            suportedDevices.put("27", DeviceType.BUTTON);
            suportedDevices.put("17", DeviceType.BUTTON);
            suportedDevices.put("16", DeviceType.BUTTON);
            suportedDevices.put("2", DeviceType.LIGHT);
            suportedDevices.put("25", DeviceType.LIGHT);
            suportedDevices.put("18", DeviceType.LIGHT);
            suportedDevices.put("23", DeviceType.LIGHT);
        }
        else if (this.id.equalsIgnoreCase("l2")) {
            suportedDevices.put("26", DeviceType.LIGHT);
            suportedDevices.put("27", DeviceType.LIGHT);
            suportedDevices.put("24", DeviceType.BUTTON);
            suportedDevices.put("25", DeviceType.BUTTON);
            suportedDevices.put("16", DeviceType.BUTTON);
            suportedDevices.put("23", DeviceType.MOTION);
            suportedDevices.put("64", DeviceType.TEMPERATURE);   
            suportedDevices.put("64", DeviceType.HUMIDTY);   
        }
        else if (this.id.equalsIgnoreCase("l3")) {
            suportedDevices.put("5", DeviceType.LIGHT);
            suportedDevices.put("6", DeviceType.LIGHT);
            suportedDevices.put("2", DeviceType.LIGHT);
            suportedDevices.put("24", DeviceType.BUTTON);
            suportedDevices.put("26", DeviceType.WATER);
        }else {
            logger.warning("No default config for device: " + this.id);
        }
    }
    
    public String getId() {
        return this.id;
    }

    public String getIp() {
        return this.ip;
    }
    

    public String getSuportedDevices() {
        final StringBuilder sb = new StringBuilder("[");
        
        Set<Entry<String,DeviceType>> entrySet = suportedDevices.entrySet();
        Iterator<Entry<String, DeviceType>> iterator = entrySet.iterator();
        for (Iterator iterator2 = entrySet.iterator(); iterator2.hasNext();) {
            Entry<String, DeviceType> entry = (Entry<String, DeviceType>) iterator2.next();
            sb.append("{ \"service\":\"" + entry.getKey() + "\", \"type\":\"" + entry.getValue().name() + "\"}");
            if (iterator2.hasNext())
              sb.append(",");
            
        }

        sb.append("]");
        return sb.toString();

    }

    public Date getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
    public Set<DeviceState> getState() {
        return this.state;
    }

    public void setState(Set<DeviceState> state) {
        this.state = state;
    }

    public void addState(DeviceState newState) {
        if (state.contains(newState)) {
            state.remove(newState);
        }
        newState.setDevice(this.id);
        state.add(newState);
        
        final StatementWithParams st = new StatementWithParams("INSERT INTO devicestate (device, service, dtype, dvalue) VALUES (?, ?, ?, ?)");
        st.addParam(new SqlQueryParam(Types.VARCHAR, newState.getDevice()));
        st.addParam(new SqlQueryParam(Types.VARCHAR, newState.getService()));
        st.addParam(new SqlQueryParam(Types.VARCHAR, newState.getType()));
        st.addParam(new SqlQueryParam(Types.VARCHAR, newState.getValue()));
        
        try {
            Database.instance.execUpdateQuery(st);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
    }
}