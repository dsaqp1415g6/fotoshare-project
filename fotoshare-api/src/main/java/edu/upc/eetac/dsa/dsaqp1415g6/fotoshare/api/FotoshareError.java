package edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api;

public class FotoshareError {
	public FotoshareError (int status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
 
	
	private int status;
	private String message;
 
	public FotoshareError() {
		super();
	}
 
	
	public int getStatus() {
		return status;
	}
 
	public void setStatus(int status) {
		this.status = status;
	}
 
	public String getMessage() {
		return message;
	}
 
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
