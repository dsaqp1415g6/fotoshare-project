package edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model;

public class Category {

	private String tagid;
	private String description;
	private String username;
	private String photoid;
	private String category;
	public String getTagid() {
		return tagid;
	}
	public void setTagid(String tagid) {
		this.tagid = tagid;
	}
	public String getDescripcion() {
		return description;
	}
	public void setDescripcion(String description) {
		this.description = description;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFotoid() {
		return photoid;
	}
	public void setFotoid(String photoid) {
		this.photoid = photoid;
	}
	public String getCategoria() {
		return category;
	}
	public void setCategoria(String category) {
		this.category = category;
	}
	
}
