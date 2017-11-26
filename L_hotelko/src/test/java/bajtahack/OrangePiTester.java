package bajtahack;

import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import bajtahack.database.SslClient;

public class OrangePiTester {
    
    private SslClient client;

    @Before
    public void setUp() throws Exception {
        try {
            this.client = new SslClient("C:\\bajtahack\\src\\test\\resources\\bajtahack.jks", "p");
        } catch (IllegalStateException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /*@Test
    public void testPut() {
        
        //this.client = new SslClient("/bajtahack.jks", "p", "https://l1.srm.bajtahack.si:52100");
            
        //final String makeRequest = client.payload("https://l1.srm.bajtahack.si:52100/phy/gpio/alloc", "text/plain", "17", "POST"); 
        
        
        final String makeRequest = client.payload("https://l2.srm.bajtahack.si:52200/phy/gpio/27/value", "text/plain", "1", "PUT");
        
        System.out.println(makeRequest);

    }*/
    
    //@Test
    public void testDiodaNaL3() {
        
        //this.client = new SslClient("/bajtahack.jks", "p", "https://l1.srm.bajtahack.si:52100");
            
        //final String makeRequest = client.payload("https://l1.srm.bajtahack.si:52100/phy/gpio/alloc", "text/plain", "17", "POST"); 
        
        
        final String makeRequest = client.payload("https://l3.srm.bajtahack.si:52300/phy/gpio/6/value", "text/plain", "1", "PUT");
        
        System.out.println(makeRequest);

    }
    
    @Test
    public void getTemperaturaNaL2(){
    	String makeRequest = client.payload("https://l2.srm.bajtahack.si:52200/phy/i2c/1/slaves/64/value", "text/plain", "\"00\"", "PUT");
    	makeRequest = client.get("https://l2.srm.bajtahack.si:52200/phy/i2c/1/slaves/64/value");
    	makeRequest = makeRequest.substring(1, makeRequest.length() - 1);
    	Integer temp = Integer.parseInt(makeRequest, 16);
    	float tempR = (((float)temp / 65535) * 165) - 40;
 
    	System.out.println(tempR);
    }
    
    @Test
    public void getVlagaNaL2(){
    	String makeRequest = client.payload("https://l2.srm.bajtahack.si:52200/phy/i2c/1/slaves/64/value", "text/plain", "\"01\"", "PUT");
    	makeRequest = client.get("https://l2.srm.bajtahack.si:52200/phy/i2c/1/slaves/64/value");
    	makeRequest = makeRequest.substring(1, makeRequest.length() - 1);
    	Integer temp = Integer.parseInt(makeRequest, 16);
    	float tempR = (((float)temp / 65535) * 100);
 
    	System.out.println(tempR);
    }

    @Test
    @Ignore
    public void testGet() {
        
        //this.client = new SslClient("/bajtahack.jks", "p", "https://l1.srm.bajtahack.si:52100");
        
        try {
            this.client = new SslClient("c:\\cygwin\\home\\HP\\work\\bajtahackgit\\src\\main\\resources\\bajtahack.jks", "p");
            
            final String makeRequest = client.get("https://l1.srm.bajtahack.si:52100/sys");
            
            System.out.println(makeRequest);
            
        } catch (IllegalStateException | FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }

}
