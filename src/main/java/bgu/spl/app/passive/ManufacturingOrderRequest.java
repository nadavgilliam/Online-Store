package bgu.spl.app.passive;

import bgu.spl.mics.Request;

public class ManufacturingOrderRequest implements Request<Receipt> {
	
	private final String shoeType;
	private final int amount;
	private final int tick;
	
	/**
	 * @param shoeType
	 * @param amount
	 */
	
	public ManufacturingOrderRequest(String shoeType, int amount, int tick) {
		this.shoeType = shoeType;
		this.amount = amount;
		this.tick = tick;
	}

	/**
	 * @return the shoeType
	 */
	
	public String getShoeType() {
		return shoeType;
	}

	/**
	 * @return the amount
	 */

	public int getAmount() {
		return amount;
	}
	
	public int getTick() {
		return this.tick;
	}

}
