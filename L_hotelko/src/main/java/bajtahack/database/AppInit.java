package bajtahack.database;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import bajtahack.common.Global;
import bajtahack.easysql.BajtaDatasource;
import bajtahack.easysql.Database;
import bajtahack.services.gpio;
import bajtahack.speech.BayesClassifier;

@WebListener
public class AppInit implements HttpSessionListener, ServletContextListener {
    
    public static final Logger log = LoggingFactory.loggerForThisClass();
    
    public static SslClient httpClient = null;
    
    @Override
    public void contextInitialized(ServletContextEvent ctx) {
        
        Database.instance.setConnectionFactory(new BajtaDatasource("java:jboss/datasources/bajtahack"));
        
        try {
            httpClient = new SslClient("/bajtahack.jks", "p");
        
            GoogleCredential credential = GoogleCredential.getApplicationDefault();
            
            gpio.instance.configure(httpClient);
            
            BayesClassifier.Train();
            
        } catch (IllegalStateException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            
    }
    
    @Override
    public void sessionCreated(HttpSessionEvent arg0) {
        
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {
       
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    
    }
 
}
