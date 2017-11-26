package bajtahack.services;

import bajtahack.database.SslClient;

public class gpio {
	
	SslClient client;
	
	public static final gpio instance = new gpio();
	
	public void configure(SslClient client) {
		this.client=client;
	}
	
	
	public String led(String root, String pin,String on) {
		final String file = "https://" + root + ":52300/phy/gpio/"+pin+"/value";
		final String makeRequest = client.payload(file, "text/plain", on, "PUT");
		
		return makeRequest;
	}
	
	
	
}
