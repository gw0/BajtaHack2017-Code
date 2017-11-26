package bajtahack.database;

public class DeviceState {
    
    private String service; // 23
    private String type; //gib
    private String value;
    
    private String device;
    
    public DeviceState(String service, String type, String value) {
        super();
        this.service = service;
        this.type = type;
        this.value = value;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.device == null) ? 0 : this.device.hashCode());
        result = prime * result + ((this.service == null) ? 0 : this.service.hashCode());
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        
        DeviceState other = (DeviceState) obj;
        
        if (this.device == null) {
            if (other.device != null)
                return false;
        } else if (!this.device.equalsIgnoreCase(other.device))
            return false;
        
        if (this.service == null) {
            if (other.service != null)
                return false;
        } else if (!this.service.equalsIgnoreCase(other.service))
            return false;
        
        if (this.type == null) {
            if (other.type != null)
                return false;
        } else if (!this.type.equalsIgnoreCase(other.type))
            return false;
       
        
        return true;
    }

    public String getDevice() {
        return this.device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getService() {
        return this.service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public String toJsonString() {
        return "{ \"service\":\"" + this.service + "\", \"type\":\"" + this.type + "\", \"value\":\"" + this.value + "\", \"device\" : \"" + this.device + "\"}";
    }

    public static DeviceState fromNotify(String serviceId, String type, String value) {
        DeviceState ds = new DeviceState(serviceId, type, value);
        return ds;
        
    }
    
    
    
    
}