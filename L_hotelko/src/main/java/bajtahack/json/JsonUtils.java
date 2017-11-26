package bajtahack.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import bajtahack.database.LoggingFactory;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONStringer;

public final class JsonUtils {
	
	public static final String JSON_STR_QUOTES = "\"";
	
	private static final String JSON_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat jsonDateFormat = new SimpleDateFormat(JSON_TIMESTAMP_FORMAT);
    
    private static final String JSON_DATEONLY_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat jsonDateOnlyFormat = new SimpleDateFormat(JSON_DATEONLY_FORMAT);
    
    public static final Logger log = LoggingFactory.loggerForThisClass();
    
    public static boolean isNull(Map<String, ?> map, String property) {
        Object o = map.get(property);
        if(o == null) return true;
        return false;
    }
    
    public static String getStringFromMap(Map<String, ?> map, String property) {
        Object o = map.get(property);
        if (null == o) throw new IllegalArgumentException("Property \"" + property + "\" is null");
        if (o instanceof String) return (String)o;
        if (o instanceof JSONNull) return null;
        throw new IllegalArgumentException("Property \"" + property + "\" is not a String, but: " + o.getClass().getCanonicalName());
    }

    public static int getIntFromMap(Map<String, ?> map, String property) {
        Object o = map.get(property);
        if (null == o) throw new IllegalArgumentException("Property \"" + property + "\" is null");
        if (o instanceof Number) return ((Number)o).intValue();
        throw new IllegalArgumentException("Property \"" + property + "\" is not a Number, but: " + o.getClass().getCanonicalName());
    }
    
    public static Integer getIntegerFromMap(Map<String, ?> map, String property) {
        Object o = map.get(property);
        if (null == o) throw new IllegalArgumentException("Property \"" + property + "\" is null");
        if (o instanceof JSONNull) return null;
        if (o instanceof Number) return ((Number)o).intValue();
        throw new IllegalArgumentException("Property \"" + property + "\" is not a Number, but: " + o.getClass().getCanonicalName());
    }

    public static long getLongFromMap(Map<String, ?> map, String property) {
        Object o = map.get(property);
        if (null == o) throw new IllegalArgumentException("Property \"" + property + "\" is null");
        if (o instanceof Number) return ((Number)o).longValue();
        throw new IllegalArgumentException("Property \"" + property + "\" is not a Number, but: " + o.getClass().getCanonicalName());
    }

    public static Boolean getBooleanFromMap(Map<String, ?> map, String property) {
        Object o = map.get(property);
        if (null == o) return false; //this is optional property
        if (o instanceof Boolean) return ((Boolean)o).booleanValue();
        throw new IllegalArgumentException("Property \"" + property + "\" is not a Boolean, but: " + o.getClass().getCanonicalName());
    }

    public static List<?> getList(Map<String, ?> json, String property) {
        Object o = json.get(property);
        if (null == o) throw new IllegalArgumentException("Property \"" + property + "\" is null");
        if (o instanceof List) return (List<?>)o; 
        throw new IllegalArgumentException("Property \"" + property + "\" is not a List, but: " + o.getClass().getCanonicalName());
    }

    @SuppressWarnings("unchecked")
	public static Map<String, ?> getMap(Map<String, ?> json, String property) {
        Object o = json.get(property);
        if (null == o) throw new IllegalArgumentException("Property \"" + property + "\" is null");
        if (o instanceof Map) return (Map<String, ?>)o; 
        throw new IllegalArgumentException("Property \"" + property + "\" is not a Map, but: " + o.getClass().getCanonicalName());
    }

    public static Timestamp getTimestampFromMap(Map<String, ?> map, String property) {
        Object o = map.get(property);
        if (null == o) throw new IllegalArgumentException("Property \"" + property + "\" is null");
        if (o instanceof String) return parseJsonTimestamp((String)o);
        if (o instanceof Number) return new Timestamp(((Number)o).longValue());
        throw new IllegalArgumentException("Property \"" + property + "\" is not a Timestamp String or a Timestamp Number, but: " + o.getClass().getCanonicalName());
    }

    public static String toZeroPaddedString(int value, int places) {
        String strVal = Integer.toString(value, 10);
        if (strVal.length() >= places) return strVal;
        StringBuilder sb = new StringBuilder(places);
        for (int i = strVal.length(); i < places; i++) sb.append('0');
        sb.append(strVal);
        return sb.toString();
    }
  
    public static Timestamp parseJsonTimestamp(final String timestampJson) {
        final Date date;
        try {
            synchronized (jsonDateFormat) {
                date = jsonDateFormat.parse(timestampJson);
            }
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Wrong timestamp format for \"" + timestampJson + "\": " + e.toString(), e);
        }
        return new Timestamp(date.getTime());
    }


    public static String formatJsonTimestamp(final Timestamp timestamp) {
        if (timestamp == null) return "";
        synchronized (jsonDateFormat) {
            return jsonDateFormat.format(timestamp);
        }
    }
    
    public static String formatJsonDateTime(final Date dt) {
        if (dt==null) return "";
        synchronized (jsonDateFormat) {
            return jsonDateFormat.format(dt);
        }
    }

    
    public static String formatJsonDateonly(final Date dt) {
        if (dt==null) return "";
        synchronized (jsonDateOnlyFormat) {
            return jsonDateOnlyFormat.format(dt);
        }
    }

    public static SimpleDateFormat newJsonTimestampFormatter() {
        return new SimpleDateFormat(JSON_TIMESTAMP_FORMAT);
    }

    public static String getStringParameter(final HttpServletRequest request, final String parameterName, final boolean optional) throws IllegalArgumentException {
        final String value = request.getParameter(parameterName);
        if (null == value) {
            if (optional) return null;
            throw new IllegalArgumentException("The parameter " + parameterName + " has not been provided");
        }
        if (value.length() == 0) {
            if (optional) return null;
            throw new IllegalArgumentException("The parameter " + parameterName + " is empty");
        }
        return value;
    }

    public static Timestamp getTimestampParameter(final HttpServletRequest request, final String parameterName, final boolean optional) throws IllegalArgumentException {
        final String timestampString = getStringParameter(request, parameterName, optional);
        if (null == timestampString) return null;
        return parseJsonTimestamp(timestampString);
    }

    public static Integer getIntParameter(final HttpServletRequest request, final String parameterName, final boolean optional) throws IllegalArgumentException {
        final String intString = getStringParameter(request, parameterName, optional);
        if (null == intString) return null;
        return Integer.valueOf(intString, 10);
    }

    public static Long getLongParameter(final HttpServletRequest request, final String parameterName, final boolean optional) throws IllegalArgumentException {
        final String longString = getStringParameter(request, parameterName, optional);
        if (null == longString) return null;
        return Long.valueOf(longString, 10);
    }

    public static Boolean getBooleanParameter(final HttpServletRequest request, final String parameterName, final boolean optional) throws IllegalArgumentException {
        final String boolString = getStringParameter(request, parameterName, optional);
        if (null == boolString) return null;
        return Boolean.valueOf(boolString);
    }
       
    public static JSONObject toJson(final BufferedReader reader) throws IOException {
        final CharBuffer buffer = CharBuffer.allocate(2048);
        final StringBuilder sb = new StringBuilder();
        while (reader.read(buffer) > 0) {
            buffer.flip();
            sb.append(buffer.toString());
            buffer.clear();
        }
        //if (DEBUG_TRACE) log.fine(sb.toString());
        return JSONObject.fromObject(sb.toString());
    }
    
    
    /**
     * Jsonizes string, including html and other text that includes JSON lang strings like double quote, <, >
     * @param strToJsonize
     * @see net.sf.json.util.JSONStringer
     * @return jsonized string that can be used in HTTP transport layer to browser 
     */
    public static String jsonifyString(String strToJsonize) {
        
        if (strToJsonize==null || strToJsonize.isEmpty()) return "";
        
        // We use special object from our 3rd party json library, 
        final String myString1 = new JSONStringer().array().value(strToJsonize).endArray().toString();
        return myString1.substring(2, myString1.length() - 2);
        
    }
    
    
    
    public static void jsonizeString(Appendable sb, String value) throws IOException {
        if (null == value)
            sb.append("null");
        else {
            String[] parts = value.split("\"");
            sb.append("\"");
            sb.append(parts[0]);
            int l = parts.length;
            for (int i = 1; i < l; i++) {
                sb.append("\\u0022");
                sb.append(parts[i]);
            }
            sb.append("\"");
        }
    }

    public static void jsonizeString(StringBuilder sb, String value) {
        try {
            jsonizeString((Appendable) sb, value);
        } catch (IOException e) {
        } // this will never happen with StringBuilder
    }

    public static String jsonizeString(String value) {
        StringBuilder sb = new StringBuilder();
        jsonizeString(sb, value);
        return sb.toString();
    }
    
    public static String jsonizeString2(String value)
    {
    	if(value == null) return "";
    	
		CharSequence s1 = "\"";
    	CharSequence s2 = "\\u0022";
    	
    	return value.replace(s1, s2).replace("\r\n", "").replace("\n", "");    //ali lahko povsod zamenjamo preskoke v novo vrstico?
    }
    
    public static String jsonizeString3(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char         c = 0;
        int          i;
        int          len = string.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String       t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
//                if (b == '<') {
                    sb.append('\\');
//                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
               sb.append("\\r");
               break;
            default:
                if (c < ' ') {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    /** Naredi preprost JSON flat array
     * @param arr
     * @return
     */
    public static String jsonizeArray(List<String> arr){
        final StringBuilder jsonStr = new StringBuilder("[");
        for (Iterator<String> iterator = arr.iterator(); iterator.hasNext();) {
            jsonStr.append("\"" + iterator.next() + "\"");
            if (iterator.hasNext()) jsonStr.append(","); 
        }
        jsonStr.append("]");
        return jsonStr.toString();
        
    }
    
    /** Za vsakega člana map objekta naredi en json element, podlaga je elementFormat 
     * @param map seznam 
     * @param elementFormat Podlaga za 1ga člana map objekta. Primer:
     * <code>"{ \"key\" : \"%s\" , \"val\": \"%s\"}"</code>
     * @return
     */
    public static String jsonizeMap(Map<String, String> map, String elementFormat){
        final StringBuilder sb = new StringBuilder("[");
        for (final Iterator<Entry<String, String>> iterator2 = map.entrySet().iterator(); iterator2.hasNext();) {
            final Entry<String, String> entry = iterator2.next();
            sb.append(String.format(elementFormat, entry.getKey(), entry.getValue()));
            if(iterator2.hasNext()) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
	
}
