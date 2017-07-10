package bgu.spl.app.passive;

import bgu.spl.mics.Request;

public class RestockRequest implements Request<Boolean> {
	
	private final String shoeType;

	/**
	 * @param shoeType
	 */
	public RestockRequest(String shoeType) {
		this.shoeType = shoeType;
	}
	
	public String getShoeType() {
		return this.shoeType;
	}

}
