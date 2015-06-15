package edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.FotoshareResource;
import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.FotoshareRootAPIResource;
import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.MediaType;


public class FotoshareRootAPI {
	@InjectLinks({
            @InjectLink(resource = FotoshareRootAPIResource.class, style = Style.ABSOLUTE, rel = "self bookmark home", title = "Fotoshare Root API", method = "getRootAPI"),
            @InjectLink(resource = FotoshareResource.class, style = Style.ABSOLUTE, rel = "create", title = "photos", type = MediaType.FOTOSHARE_API_PHOTOS_COLLECTION)})
    	private List<Link> links;
 
	public List<Link> getLinks() {
		return links;
	}
 
	public void setLinks(List<Link> links) {
		this.links = links;
	}
}
