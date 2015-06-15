package edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.FotoshareResource;
import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.MediaType;

public class Photo {
	@InjectLinks({
		@InjectLink(resource = FotoshareResource.class, style = Style.ABSOLUTE, rel = "photos", title = "Colecció de fotos", type = MediaType.FOTOSHARE_API_PHOTOS_COLLECTION),
		@InjectLink(resource = FotoshareResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "photos", type = MediaType.FOTOSHARE_API_PHOTOS, method = "getPhotoid", bindings = @Binding(name = "photoid", value = "${instance.photoid}")) }) 
	
	private List<Link> links;
	private int photoid;
	private String photoname;
	private String username;
	private Date data;
	private String url;
	
	public int getScore() {
		return score;
	}

	public void setScore(int puntuacion) {
		this.score = puntuacion;
	}
	private String filename;
	private int score;
	
    
    public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUrl() {
		return url;
	}

	public int getPhotoid() {
		return photoid;
	}

	public void setPhotoid(int photoid) {
		this.photoid = photoid;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	private List<Review> reviews = new ArrayList<Review>();
    private List<Category> categories = new ArrayList<Category>();
    private List<Score> scores = new ArrayList<Score>();
	

	public List<Review> getReviews() {

		return reviews;
	}
    
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	public void addReview(Review review) {
		reviews.add(review);
	}
  
	public void addCategoria(Category categoria) {
		categories.add(categoria);
	}
    public List<Category> getCategorias() {
		return categories;
	}
	public void setCategorias(List<Category> categorias) {
		this.categories = categorias;
	}
    
    public void addPuntuacion(Score puntuacion) {
		scores.add(puntuacion);
	}
    public List<Score> getPuntuaciones() {
		return scores;
	}
	public void setPuntuaciones(List<Score> puntuaciones) {
		this.scores = puntuaciones;
	}

    
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public String getPhotoname() {
		return photoname;
	}
	public void setPhotoname(String photoname) {
		this.photoname = photoname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getDate() {
		return data;
	}
	public void setDate(Date date) {
		this.data = date;
	}
	
}

