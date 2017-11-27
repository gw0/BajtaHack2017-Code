/**
 * SRM module library in Java on Android.
 *
 * @author Amela, Gregor <gw.2017@ena.one>
 * @version 0.1.0+ublox
 */
package srm.srmlib;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * REST interface for interacting with SRM modules.
 */

public class SRMClient {
    private static String LOG_TAG = "srm.srmlib.SRMClient";
    private static String ENCODING = "UTF-8";
    private static int MAX_RETRIES = 5;

    /* Levels of HTTPS security */
    public static final int HTTPS_PUBLIC_CA = 1;
    public static final int HTTPS_PRIVATE_CA = 2;
    public static final int HTTPS_DEVELOPMENT = 3;
    public static final int HTTPS_BASIC = 0;

    /* SRM service names */
    public static final Set<String> SERVICE_NAMES = new HashSet<String>(Arrays.asList("value", "save", "alloc", "auth", "observe", "ui"));

    /* SRM access rights */
    public static final long AUTH_ALL = 0xFFFFFFF;
    public static final long AUTH_NONE = 0x0000000;
    public static final int AUTH_LENGTH = 9;

    public static final long LISTING_GET = 0x8000000;
    public static final long LISTING_PUT = 0x4000000;
    public static final long LISTING_POST = 0x2000000;
    public static final long LISTING_DELETE = 0x1000000;
    public static final long LISTING_ALL = 0xF000000;

    public static final long ACCESS_GET = 0x0800000;
    public static final long ACCESS_PUT = 0x0400000;
    public static final long ACCESS_POST = 0x0200000;
    public static final long ACCESS_DELETE = 0x0100000;
    public static final long ACCESS_ALL = 0x0F00000;

    public static final long PERSISTENCY_GET = 0x0080000;
    public static final long PERSISTENCY_PUT = 0x0040000;
    public static final long PERSISTENCY_POST = 0x0020000;
    public static final long PERSISTENCY_DELETE = 0x0010000;
    public static final long PERSISTENCY_ALL = 0x00F0000;

    public static final long ALLOCATION_GET = 0x0008000;
    public static final long ALLOCATION_PUT = 0x0004000;
    public static final long ALLOCATION_POST = 0x0002000;
    public static final long ALLOCATION_DELETE = 0x0001000;
    public static final long ALLOCATION_ALL = 0x000F000;

    public static final long AUTHORIZATION_GET = 0x0000800;
    public static final long AUTHORIZATION_PUT = 0x0000400;
    public static final long AUTHORIZATION_POST = 0x0000200;
    public static final long AUTHORIZATION_DELETE = 0x0000100;
    public static final long AUTHORIZATION_ALL = 0x0000F00;

    public static final long OBSERVE_GET = 0x0000080;
    public static final long OBSERVE_PUT = 0x0000040;
    public static final long OBSERVE_POST = 0x0000020;
    public static final long OBSERVE_DELETE = 0x0000010;
    public static final long OBSERVE_ALL = 0x00000F0;

    public static final long UI_GET = 0x0000008;
    public static final long UI_PUT = 0x0000004;
    public static final long UI_POST = 0x0000002;
    public static final long UI_DELETE = 0x0000001;
    public static final long UI_ALL = 0x000000F;

    /* Defaults as class variables */
    public static URL urlDefault = null;
    public static Integer httpsCheckDefault = HTTPS_PUBLIC_CA;
    public static String caBundleDefault = CaCertPem.certPem;
    public static Integer connectTimeoutDefault = 15000;
    public static Integer readTimeoutDefault = 20000;
    public static Boolean verboseDefault = false;
//    static {
//        try {
//
//            SRMClient.urlDefault = new URL("https://193.2.177.172:16100");
////            SRMClient.urlDefault = new URL("https://193.2.177.172:16100");
//        } catch (MalformedURLException e) {
//            Log.wtf(LOG_TAG, "Exception: " + e.toString(), e);
//        }
//    }

    /* Parameters */
    public URL url;
    public int httpsCheck;
    public String caBundle;
    public int connectTimeout;
    public int readTimeout;
    public boolean verbose;

    public static SSLSocketFactory mTrustedSocketFactory;
    public static HostnameVerifier mHostnameVerifier;

    /**
     * Initialize a REST interface.
     *
     * @param url Base URL that gets prepended to all requests
     * @param httpsCheck Optional HTTPS security level
     * @param caBundle Optional Private CA certificate bundle
     * @param connectTimeout Optional connection timeout in milliseconds
     * @param readTimeout Optional read timeout in milliseconds
     * @param verbose Optional verbose mode
     */
    public SRMClient(URL url, Integer httpsCheck, String caBundle, Integer connectTimeout, Integer readTimeout, Boolean verbose) {
        if (url == null)
            url = SRMClient.urlDefault;
        if (httpsCheck == null)
            httpsCheck = SRMClient.httpsCheckDefault;
        if (caBundle == null)
            caBundle = SRMClient.caBundleDefault;
        if (connectTimeout == null)
            connectTimeout = SRMClient.connectTimeoutDefault;
        if (readTimeout == null)
            readTimeout = SRMClient.readTimeoutDefault;
        if (verbose == null)
            verbose = SRMClient.verboseDefault;

        this.url = url;
        this.httpsCheck = httpsCheck;
        this.caBundle = caBundle;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.verbose = verbose;

        if (this.httpsCheck == HTTPS_PUBLIC_CA) {
            // regular public CA verification
            mTrustedSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
            // regular hostname verification
            mHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();

        } else if (this.httpsCheck == HTTPS_PRIVATE_CA) {
            // with private CA bundle verification
            mTrustedSocketFactory = getSocketFactoryFromCert(caBundle);
            // regular hostname verification
            mHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();

        } else if (this.httpsCheck == HTTPS_DEVELOPMENT) {
            // with private CA bundle verification
            mTrustedSocketFactory = getSocketFactoryFromCert(caBundle);
            // turn off hostname verification
            mHostnameVerifier = getHostnameVerifierInsecure();

        } else if (this.httpsCheck == HTTPS_BASIC) {
            // without any verification
            mTrustedSocketFactory = getSocketFactoryInsecure();
            // turn off hostname verification
            mHostnameVerifier = getHostnameVerifierInsecure();
        }
    }

    /** Custom REST request. */
    public SRMResponse request(String method, URL url, String data) throws IOException {
        HttpsURLConnection conn = null;
        int statusCode = -1;
        String content = null;
        IOException lastException = null;

        for (int i=MAX_RETRIES-1; i >= 0; --i) {
            try {
                // initialize connection
                conn = (HttpsURLConnection) url.openConnection();
                conn.setConnectTimeout(this.connectTimeout);
                conn.setReadTimeout(this.readTimeout);
                conn.setSSLSocketFactory(mTrustedSocketFactory);
                conn.setHostnameVerifier(mHostnameVerifier);
                if (url.getUserInfo() != null)
                    conn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(url.getUserInfo().getBytes(), Base64.DEFAULT));
                if (data != null)
                    conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod(method);

                // send payload
                if (conn.getDoOutput()) {
                    OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                    out.write(data.getBytes(ENCODING));
                    out.close();
                }

                // receive response
                statusCode = conn.getResponseCode();
                if (conn.getDoInput()) {
                    InputStream isIn;
                    if (statusCode >= 400)
                        isIn = conn.getErrorStream();
                    else
                        isIn = conn.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(isIn, ENCODING));
                    StringBuilder builder = new StringBuilder();
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        builder.append(line);
                        builder.append("\n");
                    }
                    in.close();
                    content = builder.toString();
                }

                // no exception
                lastException = null;
                break;

            } catch (IOException e) {
                Log.e(LOG_TAG, "Exception (retrying " + i + "): " + e.toString());
                lastException = e;
            }
        }
        if (lastException != null) {  // raise last exception
            throw lastException;
        }

        // verify status code
        if (this.verbose)
            Log.d(LOG_TAG, String.format("  = %d: %s", statusCode, content));
        if(statusCode == 409){
            Log.d(LOG_TAG, String.format(" Sensor already allocated = %d: %s", statusCode, content));
        }
        else if (statusCode < 200 || statusCode >= 300)
            throw new ProtocolException(String.format("Unsuccessful HTTP status code: %s from %s %s", statusCode, method, url.toString()));
        return new SRMResponse(statusCode, content, conn);
    }

    /**
     * REST GET wrapper.
     *
     * @param path Path where REST GET request is sent
     */
    public SRMResponse get(String path) throws IOException {
        URL url = this.url;
        if (path != null) {
            try {
                url = new URL(this.url.toString() + path);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Exception: " + e.toString(), e);
                return null;
            }
        }
        if (this.verbose)
            Log.d(LOG_TAG, String.format("  GET('%s')", url));

        return this.request("GET", url, null);
    }

    /**
     * REST PUT wrapper.
     *
     * @param path Path where REST PUT request is sent
     * @param data Data to be sent
     */
    public SRMResponse put(String path, String data) throws IOException {
        URL url = this.url;
        if (path != null) {
            try {
                url = new URL(this.url.toString() + path);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Exception: " + e.toString(), e);
                return null;
            }
        }
        if (this.verbose)
            Log.d(LOG_TAG, String.format("  PUT('%s', '%s')", url, data));

        return this.request("PUT", url, data);
    }

    /**
     * REST POST wrapper.
     *
     * @param path Path where REST POST request is sent
     * @param data Data to be sent
     */
    public SRMResponse post(String path, String data) throws IOException {
        URL url = this.url;
        if (path != null) {
            try {
                url = new URL(this.url.toString() + path);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Exception: " + e.toString(), e);
                return null;
            }
        }
        if (this.verbose)
            Log.d(LOG_TAG, String.format("  POST('%s', '%s')", url, data));

        return this.request("POST", url, data);
    }

    /**
     * REST DELETE wrapper.
     *
     * @param path Path where REST DELETE request is sent
     * @param data Data to be sent
     */
    public SRMResponse delete(String path, String data) throws IOException {
        URL url = this.url;
        if (path != null) {
            try {
                url = new URL(this.url.toString() + path);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Exception: " + e.toString(), e);
                return null;
            }
        }
        if (this.verbose)
            Log.d(LOG_TAG, String.format("  DELETE('%s', '%s')", url, data));

        return this.request("DELETE", url, data);
    }

    /** Function for deallocation of all subresources under a path. */
    public void deallocateAll(String path) throws IOException, JSONException {
        SRMResponse r = this.get(path);
        JSONArray listing = new JSONArray(r.content);

        int i;
        for (i=0; i < listing.length(); ++i) {
            String sub = listing.getString(i);
            if (!SERVICE_NAMES.contains(sub))
                this.delete(String.format("%s/%s/alloc", path, sub), null);  // deallocate
        }
    }

    /** Reboot. */
    public void reboot(boolean wait) throws IOException {
        this.put("/sys/reboot/value", "reboot");

        // wait until ready
        try {
            if (wait)
                Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        while (wait) {
            try {
                if (this.get("/") != null)
                    break;
            } catch (IOException e) {
            }
        }
    }

    /**
     * Helper function for building URLs.
     *
     * `scheme://user:password@hostname:port/path?query#fragment`
     */
    public static URL urlBuilder(URL url, String schema, String user, String password, String hostname, Integer port, String path, String query, String fragment) {
        if (url != null) {
            // parse given URL
            Matcher m = Pattern.compile("^([hH][tT][tT][pP][sS]?)://(?:(\\w+):(\\w+)@)?((?:\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(?:[\\w-]+(?:\\.[\\w-]+)+))(?::(\\d+))?(/?.*?)(?:\\?(.*?))?(?:#(.*?))?$").matcher(url.toString());
            if (!m.matches())
                return null;

            // update given componenets
            if (schema == null)
                schema = m.group(1);
            if (user == null)
                user = m.group(2);
            if (password == null)
                password = m.group(3);
            if (hostname == null)
                hostname = m.group(4);
            if (port == null && m.group(5) != null)
                port = Integer.parseInt(m.group(5));
            if (path == null)
                path = m.group(6);
            if (query == null)
                query = m.group(7);
            if (fragment == null)
                fragment = m.group(8);
        }

        // rebuild URL
        StringBuilder sb = new StringBuilder();
        sb.append(schema).append("://");
        if (user != null && !user.isEmpty() && password != null && !password.isEmpty())
            sb.append(user).append(":").append(password).append("@");
        sb.append(hostname);
        if (port != null)
            sb.append(':').append(String.valueOf(port));
        if (path != null && !path.isEmpty())
            sb.append(path);
        if (query != null && !query.isEmpty())
            sb.append('?').append(query);
        if (fragment != null && !fragment.isEmpty())
            sb.append('#').append(fragment);
        try {
            return new URL(sb.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Exception: " + e.toString(), e);
            return null;
        }
    }

    /** Helper function for SSL/TLS sockets trusting only certificates from a given private CA. */
    public static SSLSocketFactory getSocketFactoryFromCert(String certPem) {
        try {
            // parse CA certificate in DER format from PEM
            String[] certTokens = certPem.split("-----BEGIN CERTIFICATE-----");
            certTokens = certTokens[1].split("-----END CERTIFICATE-----");
            byte[] certDer = Base64.decode(certTokens[0], Base64.DEFAULT);

            // generate Certificate from DER format
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate cert = factory.generateCertificate(new ByteArrayInputStream(certDer));

            // create a KeyStore containing our trusted CAs
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", cert);

            // create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            return context.getSocketFactory();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception: " + e.toString(), e);
        }
        return null;
    }

    /** Helper function for SSL/TLS sockets that insecurely trusts all certificates. */
    public static SSLSocketFactory getSocketFactoryInsecure() {
        try {
            // create a TrustManager that trusts all certificates
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
            };

            // create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustAllCerts, null);
            return context.getSocketFactory();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception: " + e.toString(), e);
        }
        return null;
    }

    /** Ignore hostname in certificate check. */
    public static HostnameVerifier getHostnameVerifierInsecure() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }
}
