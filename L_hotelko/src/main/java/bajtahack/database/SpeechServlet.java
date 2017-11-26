package bajtahack.database;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.NotImplementedException;

import bajtahack.services.gpio;
import bajtahack.services.gpio2;
import bajtahack.speech.BayesClassifier;
import bajtahack.speech.SpeechRecognition;

@WebServlet("/speechcommand")
//@MultipartConfig(fileSizeThreshold=1024*1024*10,    // 10 MB
//maxFileSize=1024*1024*50,          // 50 MB
//maxRequestSize=1024*1024*100)      // 100 MB
public class SpeechServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public static final java.util.logging.Logger logger = LoggingFactory.loggerForThisClass();
    
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { // for debugging
    	throw new NotImplementedException();
    }
    
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
    	InputStream input = request.getInputStream(); 
    	logger.info("speechcommand poklican");
    	
    	File temp;
        try
        {
           temp = File.createTempFile("audioTmp", ".flac");
    	
	    	//začasno shranimo na disk
	    	OutputStream os = new FileOutputStream(temp);
	        byte[] bufferFile = new byte[1024];
	        int bytesRead;
	        //read from is to buffer
	        while((bytesRead = input.read(bufferFile)) !=-1){
	            os.write(bufferFile, 0, bytesRead);
	        }
	        os.flush();
	        os.close();
	    	
	    	/*ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    	
	    	int nRead;
	    	byte[] data = new byte[1024 * 1024];
	
	    	while ((nRead = input.read(data, 0, data.length)) != -1) {
	    	  buffer.write(data, 0, nRead);
	    	}
	
	    	buffer.flush();*/
	
	    	String result = SpeechRecognition.Recognize2(temp.getAbsolutePath());
	    	
	    	result = BayesClassifier.Classify(result.split("\\s"));
	    	
	    	gpio2 lucka = new gpio2("https://l3.srm.bajtahack.si:52300", AppInit.httpClient);
	    	
	    	if(result.length() == 3){
	    		if(result.equals("000")){	//all lights off
	    			lucka.led("5", "0");
	    			lucka.led("6", "0");
	    			lucka.led("2", "0");
	    			
	    		}else if(result.equals("222")){	//all lights on
	    			lucka.led("5", "1");
	    			lucka.led("6", "1");
	    			lucka.led("2", "1");
	    		}else{
	    			String room = result.substring(1, 2);
		    		String onOff = result.substring(2, 3);
		    		
		    		switch (room) {
						case "1":
							room = "5";
							break;
						case "2":
							room = "6";
							break;
						case "3":
							room = "2";
							break;
					}
		    		
		    		lucka.led(room, onOff);
	    		}   		
	    	}	    	
	    	
	    	System.out.println(result);
	    	
        }
        catch(Exception ex){
        	System.out.println(ex.getMessage());
        }

    	response.setContentType("text/plain");
        response.getOutputStream().print("OK"); 
        response.getOutputStream().flush();
        return;
    }
    
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	throw new NotImplementedException();
    }
    
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	throw new NotImplementedException();
    }
	
	
}
