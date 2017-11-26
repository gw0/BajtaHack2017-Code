package bajtahack.common;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import bajtahack.database.LoggingFactory;

public final class Utils {
    
    public static final Logger log = LoggingFactory.loggerForThisClass();
    
    private Utils() {
    } // a static-methods-only class

    // NULL VALUEed
    
    public static String nvl(Object s, String def) {
        return (s != null) ? String.valueOf(s) : def;
    }

    public static String nvl(Object s) {
        return nvl(s, "");
    }
    
    public static boolean isNullOrEmpty(Object s) {
        return nvl(s, "").length() < 1 ? true : false;
    }

    // Stream utils
    
    public static byte[] fileToStream(File f) {

        try (final InputStream is = new FileInputStream(f)) {
            
            return Utils.streamToByteArray(is);
             
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        
        throw new IllegalStateException("Napaka pri branju datoteke v stream!");
        
    }
    
    public static String readFully(InputStream inputStream) {
        byte[] buffer = new byte[1024];
        int length = 0;
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toString();    
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error reading stream.", e);
        }
        
        throw new IllegalStateException("Napaka pri branju streama!");
    }
    
    public static byte[] streamToByteArray(InputStream inputStream) {
        
        byte[] buffer = new byte[1024];
        int length = 0;
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error reading stream.", e);
        }
        throw new IllegalStateException("Napaka pri stream serializaciji!");
    }
    
    public static String readFully(Reader reader) {
        final StringBuilder sb = new StringBuilder();
        
        int b;
        try (final BufferedReader br = new BufferedReader(reader)){
            while((b = br.read())!= -1){
                sb.append((char)b);
            }
            return sb.toString();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error reading stream from reader.", e);
        }
        
        throw new IllegalStateException("Napaka pri branju streama!");
    }
    
    public static void copy(InputStream is, OutputStream os) {        
        try {
            byte buf[] = new byte[8192];
            int rd=-1;
            
            while ((rd = is.read(buf)) != -1){
                os.write(buf, 0, rd);
            } 
                
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Napaka pri pripravi dokumenta!", e);
        } finally {
            if (os!=null) try { os.close(); } catch(IOException e) {}
            if (is!=null) try { is.close(); } catch(IOException e) {}
        }
    }
    
    // Simple data structures
    
    public static class SimpleTuple<X, Y> { 
        public final X x; 
        public final Y y; 
        public SimpleTuple(X x, Y y) { 
          this.x = x; 
          this.y = y; 
        } 
    } 
    
    public static class SimpleTriple<X, Y, Z> { 
        public final X x; 
        public final Y y; 
        public final Z z;
        public SimpleTriple(X x, Y y, Z z) {
            super();
            this.x = x;
            this.y = y;
            this.z = z;
        }
         
    }
    
}
