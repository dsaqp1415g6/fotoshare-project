package edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.FotoshareResource;
import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.MediaType;

public class PhotoCollection {
	@InjectLinks({ @InjectLink(resource = FotoshareResource.class, style = Style.ABSOLUTE, rel = "create", title = "Create photo", type = MediaType.FOTOSHARE_API_PHOTOS) })
	
	private List<Link> links;
	private List<Photo> photo;

	public PhotoCollection() {
		super();
		photo = new ArrayList<Photo>();
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<Photo> getPhoto() {
		return photo;
	}

	public void setPhoto(List<Photo> photo) {
		this.photo = photo;
	}

	public void addPhotos(Photo photos) {
		photo.add(photos);
	}
	 
	 
	 public void ordenar() {
	        for(int k=0;k < photo.size()-1; k++){
	        	Photo aux = null;
	        	
	        	if(photo.get(k).getScore() < photo.get(k+1).getScore()){
	        		aux = photo.get(k);
	        		
	        		
	        		photo.set(k, photo.get(k+1));
	        		photo.set(k, aux);
	        		
	        	}
	        }
	    }
}
