package bajtahack.database;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceRegistry {
    
    public static final java.util.logging.Logger logger = LoggingFactory.loggerForThisClass();
    
    public static final DeviceRegistry instance = new DeviceRegistry();
    
    public final Map<String, Device> DEVICE_MAP = new ConcurrentHashMap<String, Device>();
    
    
    public Device getDevice(String id) {
        synchronized (this) {
            if (DEVICE_MAP.containsKey(id.toLowerCase())) {
                final Device device = DEVICE_MAP.get(id.toLowerCase());
                return device;
            }
            return null;
        }
    }
    
    public void addDevice(Device d) {
        synchronized (this) {
            if (DEVICE_MAP.containsKey(d.id.toLowerCase())) {
                
                final Device device = DEVICE_MAP.get(d.id.toLowerCase());
                device.setLastModified(new Date());
                
                // logger.info("Device already exists " + d.id);
                return;
            }
            
            logger.info("Added device " + d.getId().toLowerCase());
            DEVICE_MAP.put(d.id.toLowerCase(), d);
        }
    }
    

}
