package edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model;

import java.util.ArrayList;
import java.util.List;

public class ScoreCollection {
	List<Score> punt;

	public ScoreCollection() {
		super();
		punt = new ArrayList<Score>();
	}
	public void addScore(Score p){
		punt.add(p);
	}
	public List<Score> getScore() {
		return punt;
	}
	public void setScore(List<Score> punt) {
		this.punt = punt;
	}
}
