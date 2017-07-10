package bgu.spl.app.passive;

public class Receipt {
	
	private final String seller;
	private final String customer;
	private final String shoeType;
	private final boolean discount;
	private final int issuedTick;
	private final int requestTick;
	private final int amountSold;
	
	public Receipt(String seller, String customer, String shoeType, boolean discount, int issuedTick, int requestTick, int amountSold) {
		this.seller = seller;
		this.customer = customer;
		this.shoeType = shoeType;
		this.discount = discount;
		this.issuedTick = issuedTick;
		this.requestTick = requestTick;
		this.amountSold = amountSold;
	}
	
	/**
	 * @return the seller
	 */
	public String getSeller() {
		return seller;
	}
	/**
	 * @return the customer
	 */
	public String getCustomer() {
		return customer;
	}
	/**
	 * @return the shoeType
	 */
	public String getShoeType() {
		return shoeType;
	}
	/**
	 * @return the discount
	 */
	public boolean isDiscount() {
		return discount;
	}
	/**
	 * @return the issuedTick
	 */
	public int getIssuedTick() {
		return issuedTick;
	}
	/**
	 * @return the requestTick
	 */
	public int getRequestTick() {
		return requestTick;
	}
	/**
	 * @return the amountSold
	 */
	public int getAmountSold() {
		return amountSold;
	}
	/**
	 * @param seller
	 * @param customer
	 * @param shoeType
	 * @param discount
	 * @param issuedTick
	 * @param requestTick
	 * @param amountSold
	 */

}
