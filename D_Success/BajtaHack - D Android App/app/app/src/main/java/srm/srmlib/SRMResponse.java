/**
 * SRM module library in Java on Android.
 *
 * @author Amela, Gregor <gw.2017@ena.one>
 * @version 0.1.0+ublox
 */
package srm.srmlib;

import java.net.HttpURLConnection;

/**
 * Holder for HTTP responses.
 */

public class SRMResponse {
    public final int statusCode;
    public final String content;
    public final HttpURLConnection conn;

    public SRMResponse(int statusCode, String content, HttpURLConnection conn) {
        this.statusCode = statusCode;
        this.content = content;
        this.conn = conn;
    }
}
