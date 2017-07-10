package bgu.spl.app.passive;

import bgu.spl.mics.Broadcast;

public class NewDiscountBroadcast implements Broadcast {
	
	private final String shoeType;
	private final int amount;
	
	/**
	 * @param shoeType
	 * @param amount
	 */
	
	public NewDiscountBroadcast(String shoeType, int amount) {
		this.shoeType = shoeType;
		this.amount = amount;
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

}
