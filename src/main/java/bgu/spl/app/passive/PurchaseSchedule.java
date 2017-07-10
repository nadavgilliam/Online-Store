package bgu.spl.app.passive;

public class PurchaseSchedule {
	
	private String shoeType;
	private int tick;
	
	/**
	 * @param shoeType
	 * @param tick
	 */
	public PurchaseSchedule(String shoeType, int tick) {
		this.shoeType = shoeType;
		this.tick = tick;
	}
	
	public int getTick() {
		return this.tick;
	}
	
	public String getShoe() {
		return this.shoeType;
	}

}
