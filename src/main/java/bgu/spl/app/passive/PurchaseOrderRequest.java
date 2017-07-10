package bgu.spl.app.passive;

import bgu.spl.mics.Request;

public class PurchaseOrderRequest implements Request<Receipt> {
	
	private final String name;
	private final String shoeType;
	private final int amount;
	private final boolean onlyDiscount;
	private final int issuedTick;
	
	/**
	 * @param shoeType
	 * @param amount
	 */
	
	public PurchaseOrderRequest(String shoeType, int amount, boolean onlyDiscount, String name, int tick) {
		
		this.shoeType = shoeType;
		this.amount = amount;
		this.onlyDiscount = onlyDiscount;
		this.name = name;
		this.issuedTick = tick;
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
	
	public boolean getOnlyDiscount() {
		return this.onlyDiscount;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getTick() {
		return this.issuedTick;
	}

}
