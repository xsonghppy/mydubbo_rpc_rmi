package com.mydubbo.http.sender.httpclient;

/**
 * 
 */
public class HttpsRequestException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public HttpsRequestException(){
		
	}
	
	public HttpsRequestException(String message){
		super(message);
	}
	
	public HttpsRequestException(String message,Throwable cause){
		super(message,cause);
	}

}
