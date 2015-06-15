package edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;


public class CategoryCollection {

	private List<Link> links;
	private List<Category> cat;
	
	
	public CategoryCollection() {
		super();
		cat = new ArrayList<Category>();
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public List<Category> getCat() {
		return cat;
	}
	public void setCat(List<Category> cat) {
		this.cat = cat;
	}
	
	public void addCategoria(Category cata) {
		cat.add(cata);
	}
	
}
