package edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model;

import java.sql.Date;

public class Review {
	int reviewid;
	public int getReviewid() {
		return reviewid;
	}
	public void setReviewid(int reviewid) {
		this.reviewid = reviewid;
	}
	public int getFotoid() {
		return photoid;
	}
	public void setFotoid(int fotoid) {
		this.photoid = fotoid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getReviewtext() {
		return reviewtext;
	}
	public void setReviewtext(String reviewtext) {
		this.reviewtext = reviewtext;
	}
	public Date getDate() {
		return data;
	}
	public void setDate(Date date) {
		this.data = date;
	}
	int photoid;
	String username;
	String reviewtext;
	Date data;
}
