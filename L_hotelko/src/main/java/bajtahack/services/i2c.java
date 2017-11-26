package bajtahack.services;

import bajtahack.database.SslClient;

public class i2c {
		String root;
		SslClient client;
		
		public i2c(String root, SslClient client) {
			this.root=root;
			this.client=client;
		}
		
		public float temperature(String slave) {
			String file = this.root+"/phy/i2c/1/slaves/"+slave+"/value";
			String makeRequest = client.payload(file, "text/plain", "\"00\"", "PUT");
			
			makeRequest = client.get(file);
			makeRequest = makeRequest.substring(1, makeRequest.length() - 1);
			Integer temp = Integer.parseInt(makeRequest, 16);
			float tempR = (((float)temp / 65535) * 165) - 40;
	 
			//System.out.println(tempR);
			
			return tempR;
		}
		
		public float humidity( String slave) {
			String file=this.root+"/phy/i2c/1/slaves/"+slave+"/value";
			String makeRequest = client.payload(file, "text/plain", "\"01\"", "PUT");
			makeRequest = client.get(file);
			makeRequest = makeRequest.substring(1, makeRequest.length() - 1);
			Integer temp = Integer.parseInt(makeRequest, 16);
			float tempR = (((float)temp / 65535) * 165) - 40;
	 
			//System.out.println(tempR);
			
			return tempR;
			
		}
		
		
		
		
		
		
		
}
