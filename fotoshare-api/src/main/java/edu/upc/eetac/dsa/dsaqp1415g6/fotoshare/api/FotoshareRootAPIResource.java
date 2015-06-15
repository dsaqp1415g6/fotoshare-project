package edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model.FotoshareRootAPI;

@Path("/")
public class FotoshareRootAPIResource {
	
	@GET
	public FotoshareRootAPI getRootAPI() {
		FotoshareRootAPI api = new FotoshareRootAPI();
		return api;
	}

	private boolean admin;

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
