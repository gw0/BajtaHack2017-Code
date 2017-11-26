package bajtahack.database;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import javax.net.ssl.*;
import bajtahack.database.SslClient.SSLUtilities;

/**
 * POJO Ssl client surpasses java restrictions due to host name and CA not trusted. 
 * It load's our own trustore (or keystore)
 * @author HP
 *
 */
public class SslClient {
    
    public static final java.util.logging.Logger logger= LoggingFactory.loggerForThisClass();

    private final String certificatePassword;
    
    private KeyStore keyStore;
    private SSLSocketFactory socketFactory;
    
    public static final boolean IS_FILE = false;
    
    public SslClient(String jksPath, String jksPassword) throws IllegalStateException, FileNotFoundException {
 
		//logger.info(String.format("Initializing SSL client %s", jksPath));
    	
       this.certificatePassword = jksPassword;
       
       //logger.info("Keystore path is: " + jksPath);
       keyStore = createKeyStore(jksPath);
       socketFactory = createSocketFactory();
       //logger.info("Keystore je:" + keyStore);
       
       SSLUtilities.trustAllHostnames();
       SSLUtilities.trustAllHttpsCertificates();
  
    }
    
    public String payload(String url, String contentType, String payload, String method){
        final StringBuilder outputString = new StringBuilder();
        
        final HttpURLConnection connection = getConnection(url);
        if (connection==null){
            logger.severe("Nadaljevanje ni mogoče. Povezave z " + url + " ni mogoče vzpostaviti!");
            throw new IllegalStateException("Cannot continue with no connection");
        } 
        
        final byte[] buffer = payload.getBytes();
        // Set the appropriate HTTP parameters.
        connection.setRequestProperty("Content-Length", String.valueOf(buffer.length));
        connection.setRequestProperty("Content-Type", "Content-Type: " + contentType);
        try {
            connection.setRequestMethod(method);
        } catch (ProtocolException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        connection.setDoOutput(true);
        connection.setDoInput(true);

        try(OutputStream out = connection.getOutputStream()){
            out.write(buffer);
        }catch(IOException ioEx) {
            logger.log(Level.SEVERE, ioEx.getMessage(), ioEx);
            return "";
        }

        //Write the SOAP message response to a String.
        
        try(final InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            final BufferedReader in = new BufferedReader(isr)){;
            String responseString;
            while ((responseString = in.readLine()) != null) {
                outputString.append(responseString);
            }
                
            return outputString.toString();
        }catch(IOException ioEx) {
            logger.log(Level.SEVERE, ioEx.getMessage(), ioEx);
        }
        return null;
        
    }
    
    

    /**
     * Create a http get request to sodna praksa api
     * The base url is "https://www.sodnapraksa.si/api2/mainSearch/?apiKey=d5f0bfee8b3d"
     * @param url "&connection2=EZ+23a&page=0&itemsPerPage=20"
     */
    public String get(String urlStr)  {
        logger.info("server je:" + urlStr);
        HttpURLConnection connection = getConnection(urlStr);
        connection.disconnect();
          
        try {
            connection.setRequestMethod("GET");
            logger.info("connecting ... ");
            connection.setDefaultUseCaches(false);
            connection.setUseCaches(false);
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Expires", "-1");
            connection.setRequestProperty("max-age", "0");
            connection.connect();
            logger.info("connected ");
            String encoding = connection.getContentEncoding();
            if (null == encoding) encoding = "UTF-8";
            Charset charset = Charset.forName(encoding);
            InputStreamReader reader = new InputStreamReader(connection.getInputStream(), charset);
            char[] buffer = new char[1024];
            StringBuilder response = new StringBuilder();
            int i;
            while ((i = reader.read(buffer, 0, 1024)) >= 0) {
                response.append(buffer, 0, i);
            }
            connection.disconnect();
            return response.toString();
        }
        catch (Exception e) {
           e.printStackTrace();
        }
        return "\"hits\":0,";
        //return null;
    }
    
    private KeyStore createKeyStore(String jksPath) throws IllegalStateException, FileNotFoundException {
        final char[] password = certificatePassword.toCharArray();
        
        
        final InputStream is;
        if (IS_FILE){ // load from file
            is = new FileInputStream(jksPath);
        }else{       // load from EAR/WAR
            is = this.getClass().getClassLoader().getResourceAsStream(jksPath);
        }
        
        final KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(is, password);
        }
        catch (KeyStoreException e) {
            final String message = "Key store failed while opening it: " + e.toString();
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException(message, e);
        }
        catch (CertificateException e) {
            final String message = "A problem with a certificate: " + e.toString();
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException(message, e);
        }
        catch (NoSuchAlgorithmException e) {
            final String message = "A cryptographic algorithm is not available: " + e.toString();
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException(message, e);
        }
        catch (IOException e) {
            final String message = "Failed to open the client certificate file: " + e.toString();
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException(message, e);
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {}
        }

        return keyStore;
    }

    private SSLSocketFactory createSocketFactory() {
        try {
            final KeyManagerFactory keyFact = KeyManagerFactory.getInstance("SunX509");
            keyFact.init(keyStore, certificatePassword.toCharArray());
            
            final TrustManagerFactory trustFact = TrustManagerFactory.getInstance("SunX509");
            trustFact.init(keyStore);
            
            final SSLContext c = SSLContext.getInstance("TLS");
            c.init(keyFact.getKeyManagers(), trustFact.getTrustManagers(), null);
            
            return c.getSocketFactory();
        }
        catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpURLConnection getConnection(String url) {
        try {
        	
        	URL addr = new URL(url);
        	URLConnection openConnection = addr.openConnection();
        	openConnection.setAllowUserInteraction(true);
        	openConnection.setUseCaches(false);
        	
            final HttpURLConnection connection = (HttpURLConnection) openConnection;
            if (connection instanceof HttpsURLConnection) {
                //logger.info("SEtting SSL properties called");
                final HttpsURLConnection c = (HttpsURLConnection) connection;
                c.setSSLSocketFactory(socketFactory);
                c.setDefaultUseCaches(false);
                c.setUseCaches(false);
                
            }
            
            
            return connection;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    static TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

    /**
       SSL utilitites to overcome so of the problems with self signed certificate authorities
     */
    static final class SSLUtilities {

        /**
         * Hostname verifier for the Sun's deprecated API.
         *
         * @deprecated see {@link #_hostnameVerifier}.
         */
        private static com.sun.net.ssl.HostnameVerifier __hostnameVerifier;
        /**
         * Thrust managers for the Sun's deprecated API.
         *
         * @deprecated see {@link #_trustManagers}.
         */
        private static com.sun.net.ssl.TrustManager[] __trustManagers;
        /**
         * Hostname verifier.
         */
        private static HostnameVerifier _hostnameVerifier;
        /**
         * Thrust managers.
         */
        private static TrustManager[] _trustManagers;


        /**
         * Set the default Hostname Verifier to an instance of a fake class that
         * trust all hostnames. This method uses the old deprecated API from the
         * com.sun.ssl package.
         *
         * @deprecated see {@link #_trustAllHostnames()}.
         */
        private static void __trustAllHostnames() {
            // Create a trust manager that does not validate certificate chains
            if(__hostnameVerifier == null) {
                __hostnameVerifier = new _FakeHostnameVerifier();
            } // if
            // Install the all-trusting host name verifier
            com.sun.net.ssl.HttpsURLConnection.
                    setDefaultHostnameVerifier(__hostnameVerifier);
        } // __trustAllHttpsCertificates

        /**
         * Set the default X509 Trust Manager to an instance of a fake class that
         * trust all certificates, even the self-signed ones. This method uses the
         * old deprecated API from the com.sun.ssl package.
         *
         * @deprecated see {@link #_trustAllHttpsCertificates()}.
         */
        private static void __trustAllHttpsCertificates() {
            com.sun.net.ssl.SSLContext context;

            // Create a trust manager that does not validate certificate chains
            if(__trustManagers == null) {
                __trustManagers = new com.sun.net.ssl.TrustManager[]
                        {new _FakeX509TrustManager()};
            } // if
            // Install the all-trusting trust manager
            try {
                context = com.sun.net.ssl.SSLContext.getInstance("SSL");
                context.init(null, __trustManagers, new SecureRandom());
            } catch(GeneralSecurityException gse) {
                throw new IllegalStateException(gse.getMessage());
            } // catch
            com.sun.net.ssl.HttpsURLConnection.
                    setDefaultSSLSocketFactory(context.getSocketFactory());
        } // __trustAllHttpsCertificates

        /**
         * Return true if the protocol handler property java.
         * protocol.handler.pkgs is set to the Sun's com.sun.net.ssl.
         * internal.www.protocol deprecated one, false
         * otherwise.
         *
         * @return                true if the protocol handler
         * property is set to the Sun's deprecated one, false
         * otherwise.
         */
        private static boolean isDeprecatedSSLProtocol() {
            return("com.sun.net.ssl.internal.www.protocol".equals(System.
                    getProperty("java.protocol.handler.pkgs")));
        } // isDeprecatedSSLProtocol

        /**
         * Set the default Hostname Verifier to an instance of a fake class that
         * trust all hostnames.
         */
        private static void _trustAllHostnames() {
            // Create a trust manager that does not validate certificate chains
            if(_hostnameVerifier == null) {
                _hostnameVerifier = new FakeHostnameVerifier();
            } // if
            // Install the all-trusting host name verifier:
            HttpsURLConnection.setDefaultHostnameVerifier(_hostnameVerifier);
        } // _trustAllHttpsCertificates

        /**
         * Set the default X509 Trust Manager to an instance of a fake class that
         * trust all certificates, even the self-signed ones.
         */
        private static void _trustAllHttpsCertificates() {
            SSLContext context;

            // Create a trust manager that does not validate certificate chains
            if(_trustManagers == null) {
                _trustManagers = new TrustManager[] {new FakeX509TrustManager()};
            } // if
            // Install the all-trusting trust manager:
            try {
                context = SSLContext.getInstance("SSL");
                context.init(null, _trustManagers, new SecureRandom());
            } catch(GeneralSecurityException gse) {
                throw new IllegalStateException(gse.getMessage());
            } // catch
            HttpsURLConnection.setDefaultSSLSocketFactory(context.
                    getSocketFactory());
        } // _trustAllHttpsCertificates

        /**
         * Set the default Hostname Verifier to an instance of a fake class that
         * trust all hostnames.
         */
        public static void trustAllHostnames() {
            // Is the deprecated protocol setted?
            if(isDeprecatedSSLProtocol()) {
                __trustAllHostnames();
            } else {
                _trustAllHostnames();
            } // else
        } // trustAllHostnames

        /**
         * Set the default X509 Trust Manager to an instance of a fake class that
         * trust all certificates, even the self-signed ones.
         */
        public static void trustAllHttpsCertificates() {
            // Is the deprecated protocol setted?
            if(isDeprecatedSSLProtocol()) {
                __trustAllHttpsCertificates();
            } else {
                _trustAllHttpsCertificates();
            } // else
        } // trustAllHttpsCertificates

        /**
         * This class implements a fake hostname verificator, trusting any host
         * name. This class uses the old deprecated API from the com.sun.
         * ssl package.
         *
         * @author    Francis Labrie
         *
         * @deprecated see {@link SSLUtilities.FakeHostnameVerifier}.
         */
        public static class _FakeHostnameVerifier
                implements com.sun.net.ssl.HostnameVerifier {

            /**
             * Always return true, indicating that the host name is an
             * acceptable match with the server's authentication scheme.
             *
             * @param hostname        the host name.
             * @param session         the SSL session used on the connection to
             * host.
             * @return                the true boolean value
             * indicating the host name is trusted.
             */
            public boolean verify(String hostname, String session) {
                return(true);
            } // verify
        } // _FakeHostnameVerifier


        /**
         * This class allow any X509 certificates to be used to authenticate the
         * remote side of a secure socket, including self-signed certificates. This
         * class uses the old deprecated API from the com.sun.ssl
         * package.
         *
         * @author    Francis Labrie
         *
         * @deprecated see {@link SSLUtilities.FakeX509TrustManager}.
         */
        public static class _FakeX509TrustManager
                implements com.sun.net.ssl.X509TrustManager {

            /**
             * Empty array of certificate authority certificates.
             */
            private static final X509Certificate[] _AcceptedIssuers =
                    new X509Certificate[] {};


            /**
             * Always return true, trusting for client SSL
             * chain peer certificate chain.
             *
             * @param chain           the peer certificate chain.
             * @return                the true boolean value
             * indicating the chain is trusted.
             */
            public boolean isClientTrusted(X509Certificate[] chain) {
                return(true);
            } // checkClientTrusted

            /**
             * Always return true, trusting for server SSL
             * chain peer certificate chain.
             *
             * @param chain           the peer certificate chain.
             * @return                the true boolean value
             * indicating the chain is trusted.
             */
            public boolean isServerTrusted(X509Certificate[] chain) {
                return(true);
            } // checkServerTrusted

            /**
             * Return an empty array of certificate authority certificates which
             * are trusted for authenticating peers.
             *
             * @return                a empty array of issuer certificates.
             */
            public X509Certificate[] getAcceptedIssuers() {
                return(_AcceptedIssuers);
            } // getAcceptedIssuers
        } // _FakeX509TrustManager


        /**
         * This class implements a fake hostname verificator, trusting any host
         * name.
         *
         * @author    Francis Labrie
         */
        public static class FakeHostnameVerifier implements HostnameVerifier {

            /**
             * Always return true, indicating that the host name is
             * an acceptable match with the server's authentication scheme.
             *
             * @param hostname        the host name.
             * @param session         the SSL session used on the connection to
             * host.
             * @return                the true boolean value
             * indicating the host name is trusted.
             */
            public boolean verify(String hostname,
                                  javax.net.ssl.SSLSession session) {
                return(true);
            } // verify
        } // FakeHostnameVerifier


        /**
         * This class allow any X509 certificates to be used to authenticate the
         * remote side of a secure socket, including self-signed certificates.
         *
         * @author    Francis Labrie
         */
        public static class FakeX509TrustManager implements X509TrustManager {

            /**
             * Empty array of certificate authority certificates.
             */
            private static final X509Certificate[] _AcceptedIssuers =
                    new X509Certificate[] {};


            /**
             * Always trust for client SSL chain peer certificate
             * chain with any authType authentication types.
             *
             * @param chain           the peer certificate chain.
             * @param authType        the authentication type based on the client
             * certificate.
             */
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) {
            } // checkClientTrusted

            /**
             * Always trust for server SSL chain peer certificate
             * chain with any authType exchange algorithm types.
             *
             * @param chain           the peer certificate chain.
             * @param authType        the key exchange algorithm used.
             */
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) {
            } // checkServerTrusted

            /**
             * Return an empty array of certificate authority certificates which
             * are trusted for authenticating peers.
             *
             * @return                a empty array of issuer certificates.
             */
            public X509Certificate[] getAcceptedIssuers() {
                return(_AcceptedIssuers);
            } // getAcceptedIssuers
        } // FakeX509TrustManager
    } //
  

}
