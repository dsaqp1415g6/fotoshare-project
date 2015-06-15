package edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model;

public class Score {
	int scoreid;
	int photoid;
	String username;
	int score;
	public int getPuntuacionid() {
		return scoreid;
	}
	public void setPuntuacionid(int scoreid) {
		this.scoreid = scoreid;
	}
	public int getFotoid() {
		return photoid;
	}
	public void setFotoid(int photoid) {
		this.photoid = photoid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getPuntuacion() {
		return score;
	}
	public void setPuntuacion(int score) {
		this.score = score;
	}
}
