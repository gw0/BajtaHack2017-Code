import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class Server {

	static final int blue = 16;
	static final int red = 25;
	static final int green = 24;

	private static long currentTemperature;
	private static long currentLight;
	
	private static void sendColorToStrip(int color) {
		try {
			URL url = new URL("https://p2.srm.bajtahack.si:44200/phy/gpio/" + blue + "/value");
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			HttpsURLConnection.setDefaultHostnameVerifier(createTrustedConnection());
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("PUT");
			OutputStreamWriter out = new OutputStreamWriter(
					httpCon.getOutputStream());
			out.write(blue == color ? "\"1\"" : "\"0\"");
			out.close();
			httpCon.getInputStream();

			url = new URL("https://p2.srm.bajtahack.si:44200/phy/gpio/" + green + "/value");
			httpCon = (HttpURLConnection) url.openConnection();
			HttpsURLConnection.setDefaultHostnameVerifier(createTrustedConnection());
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("PUT");
			out = new OutputStreamWriter(
					httpCon.getOutputStream());
			out.write(green == color ? "\"1\"" : "\"0\"");
			out.close();
			httpCon.getInputStream();

			url = new URL("https://p2.srm.bajtahack.si:44200/phy/gpio/" + red + "/value");
			httpCon = (HttpURLConnection) url.openConnection();
			HttpsURLConnection.setDefaultHostnameVerifier(createTrustedConnection());
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("PUT");
			out = new OutputStreamWriter(
					httpCon.getOutputStream());
			out.write(red == color ? "\"1\"" : "\"0\"");
			out.close();
			httpCon.getInputStream();
		} catch (Exception e) {
		}
	}

	private static HostnameVerifier createTrustedConnection() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };
			// Install the all-trusting trust manager
			final SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			return allHostsValid;
		} catch (Exception e) {

		}
		return null;
	}

	public static long getLightData() {
		try {
			HostnameVerifier allHostsValid = createTrustedConnection();
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			URL url = new URL("https://p1.srm.bajtahack.si:44100/phy/i2c/1/slaves/41/value");
			URLConnection con = url.openConnection();
			final Reader reader = new InputStreamReader(con.getInputStream());
			final BufferedReader br = new BufferedReader(reader);
			String line = "";
			while ((line = br.readLine()) != null) {
				return Long.parseLong(line.replace("\"", ""), 16);
			}
			br.close();
		} catch (Exception e) {
		}
		return -1;
	}

	public static long getTemperature() {
		try {
			HostnameVerifier allHostsValid = createTrustedConnection();
			if (allHostsValid == null)
				return -1;
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			configureRegister(allHostsValid);
			TimeUnit.MICROSECONDS.sleep(333);

			URL url = new URL("https://p1.srm.bajtahack.si:44100/phy/i2c/1/slaves/64/value");
			URLConnection con = url.openConnection();
			final Reader reader = new InputStreamReader(con.getInputStream());
			final BufferedReader br = new BufferedReader(reader);
			String line = "";
			while ((line = br.readLine()) != null) {
				return Long.parseLong(line.replace("\"", ""), 16);
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return -1;
	}

	public static void configureRegister(HostnameVerifier allHostsValid) {
		try {
			URL url = new URL("https://p1.srm.bajtahack.si:44100/phy/i2c/1/slaves/64/value");
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("PUT");
			OutputStreamWriter out = new OutputStreamWriter(
					httpCon.getOutputStream());
			out.write("\"00\"");
			out.close();
			httpCon.getInputStream();
		} catch (Exception e) {
		}
	}

	private static boolean getMomentData() {
		try {
			HostnameVerifier allHostsValid = createTrustedConnection();
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			URL url = new URL("https://p1.srm.bajtahack.si:44100/phy/gpio/23/value");
			URLConnection con = url.openConnection();
			final Reader reader = new InputStreamReader(con.getInputStream());
			final BufferedReader br = new BufferedReader(reader);
			String line = "";
			while ((line = br.readLine()) != null) {
				return line.replace("\"", "").equals("1");
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}

	public static void main(String[] args) {
		//configureLEDStrip();

		new Thread() {
			public void run() {
				while (true) {
					long temp = getTemperature();
					double var = (currentTemperature / Math.pow(2, 16) * 165 - 40) - (temp / Math.pow(2, 16) * 165 - 40);
					if (currentTemperature == 0 || Math.abs(var) <= 1.5) {
						currentTemperature = temp;
						try {
							TimeUnit.SECONDS.sleep(5);
						} catch (Exception e) {
						}
					}
					//System.out.println("dad " + var + " "+ (temp / Math.pow(2, 16) * 165 - 40));
				}
			}
		}.start();

		new Thread() {
			public void run() {
				while (true) {
					long temp = getLightData();
					if (temp != 0) {
						currentLight = temp;
					}
					try {
						TimeUnit.SECONDS.sleep(5);
					} catch (Exception e) {
					}
				}
			}
		}.start();

		try {
			while (true) {
				double actualTemperature = ((currentTemperature) / Math.pow(2, 16) * 165 - 40);
				System.out.println(currentLight + " " + actualTemperature);
				if (getMomentData()) {
					if (actualTemperature < 17) {
						sendColorToStrip(blue);
					} else {
						if (actualTemperature < 24) {
							sendColorToStrip(green);

						} else {
							sendColorToStrip(red);
						}
					}
				} else {
					sendColorToStrip(0);
				}
				TimeUnit.SECONDS.sleep(5);
			}
		} catch (Exception e) {
		}
	}
}
