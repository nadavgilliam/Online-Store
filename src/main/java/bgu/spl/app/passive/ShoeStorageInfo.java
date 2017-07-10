package bgu.spl.app.passive;

public class ShoeStorageInfo {
	
	private final String shoeType;
	private int amountOnStorage;
	private int discountedAmount;
	
	public ShoeStorageInfo(String shoeType, int amountOnStorage, int discountedAmount) {
		this.amountOnStorage = amountOnStorage;
		this.discountedAmount = discountedAmount;
		this.shoeType = shoeType;
	}

	/**
	 * @return the amountOnStorage
	 */
	public int getAmountOnStorage() {
		return amountOnStorage;
	}

	/**
	 * @param x the amountOnStorage to inc
	 */
	public void incAmountOnStorage(int x) {
		this.amountOnStorage += x;
	}
	
	/**
	 * 
	 * @param x the amountOnStorage to dec
	 */
	
	public void decAmountOnStorage(int x) {
		this.amountOnStorage -= x;
	}

	/**
	 * @return the discountedAmount
	 */
	public int getDiscountedAmount() {
		return discountedAmount;
	}

	/**
	 * @param x the discountedAmount to inc
	 */
	public void incDiscountedAmount(int x) {
		this.discountedAmount += x;
	}
	
	/**
	 * @param x the discountedAmount to dec
	 */
	
	public void decDiscountedAmount(int x) {
		this.discountedAmount -= x;
	}

	/**
	 * @return the shoeType
	 */
	public String getShoeType() {
		return shoeType;
	}
	
	public void setDiscountedAmount(int x) {
		this.discountedAmount = x;
	}

}
