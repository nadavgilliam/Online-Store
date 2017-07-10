package bgu.spl.app.passive;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Store {
	
	private ConcurrentLinkedQueue <ShoeStorageInfo> _Inventory = new ConcurrentLinkedQueue <ShoeStorageInfo>();
	private ConcurrentLinkedQueue <Receipt> _Receipts = new ConcurrentLinkedQueue <Receipt>();
	private final static Logger l = Logger.getLogger("Store");

	private static class StoreHolder {
        private static Store instance = new Store();
    }
	
    private Store() { }

    public static Store getInstance() {
        return StoreHolder.instance;
    }
    
    public void load(ShoeStorageInfo [] storage) {
    	for (int i = 0; i < storage.length; i++) {
			this._Inventory.add(storage[i]);
		}
    }
    /**
     * 
     * @param shoeType
     * @param onlyDiscount
     * @return the buyresult of the shoe (status)
     */
    
    public synchronized BuyResult take(String shoeType, boolean onlyDiscount) {
    	Object [] temp = this._Inventory.toArray();
    	int index = findShoe(shoeType);
    	BuyResult r;
    	if (index == -1 && !onlyDiscount) { // If the shoe was never in stock and the order was not from discount
    		l.log(Level.INFO, "Store: " + shoeType + "-- NOT_IN_STOCK");
    		r = BuyResult.NOT_IN_STOCK;
			return r;
		}
    	if (index == -1 && onlyDiscount) { //If the shoe was never in stock and the order was from discount
    		r = BuyResult.NOT_ON_DISCOUNT;
			l.log(Level.INFO, "Store: " + shoeType + "-- NOT_ON_DISCOUNT.");
			return r;
    	}
    	ShoeStorageInfo t = (ShoeStorageInfo) temp[index];
		if(onlyDiscount) { // discount order
			if (t.getDiscountedAmount() > 0) { // shoes on discount in stock
				r = BuyResult.DISCOUNTED_PRICE;
				t.decAmountOnStorage(1);
				t.decDiscountedAmount(1);
				l.log(Level.INFO, "Store: " + shoeType + " for DISCOUNTED_PRICE.");
				return r;
			}
			r = BuyResult.NOT_ON_DISCOUNT; // shoes not on discount
			l.log(Level.INFO, "Store: " + shoeType + "-- NOT_ON_DISCOUNT.");
			return r;
		}
		if (t.getDiscountedAmount() > 0) { // shoes on discount in stock
			l.log(Level.INFO, "Store: " + shoeType + " for DISCOUNTED_PRICE.");
			r = BuyResult.DISCOUNTED_PRICE;
			t.decAmountOnStorage(1);
			t.decDiscountedAmount(1);
			return r;
		}
		if(t.getAmountOnStorage() == 0) {
			l.log(Level.INFO, "Store: " + shoeType + "-- NOT_IN_STOCK.");
			r = BuyResult.NOT_IN_STOCK;
			return r;
		}
		l.log(Level.INFO, "Store: " + shoeType + " for REGULAR_PRICE.");
		r = BuyResult.REGULAR_PRICE;
		t.decAmountOnStorage(1);
		return r;
    }
    /**
     * will add shoes to stock
     * @param shoeType
     * @param amount
     */
    
    public void add(String shoeType, int amount) {
    	int index = findShoe(shoeType);
    	if (index != -1) {
    		Object [] temp = this._Inventory.toArray();
        	((ShoeStorageInfo) temp[index]).incAmountOnStorage(amount);
		}
    	else {
    		this._Inventory.add(new ShoeStorageInfo(shoeType, amount, 0));
    	}
    }
    /**
     * will add discount to store
     * @param shoeType
     * @param amount
     */
    public void addDiscount(String shoeType, int amount) {
    	int index = findShoe(shoeType);
    	if (index != -1) { // if the shoe exists
    		Object [] temp = this._Inventory.toArray();
    		ShoeStorageInfo t = (ShoeStorageInfo) temp[index];
    		int min = Math.min(amount, t.getAmountOnStorage());
        	((ShoeStorageInfo) temp[index]).incDiscountedAmount(min); // make sure that the amount on storage is greater or equal to the amount on discount
        	if (t.getAmountOnStorage() < t.getDiscountedAmount()) {
				t.setDiscountedAmount(t.getAmountOnStorage());// make sure that the amount on storage is greater or equal to the amount on discount
			}
		}
    }
    
    public void file(Receipt receipt) {
    	this._Receipts.add(receipt);
    }
    
    public void print() {
    	System.out.println("Inventory:");
    	while(this._Inventory.size() > 0) {
    		ShoeStorageInfo temp = this._Inventory.poll();
			System.out.println("	Shoe type: " + temp.getShoeType() + ", Amount on storage: " + temp.getAmountOnStorage() + ", Discounted amount on storage: " + temp.getDiscountedAmount());
			System.out.println();
    	}
    	System.out.println();
    	System.out.println("Receipts:");
    	int i = 1;
    	while(this._Receipts.size() > 0) {
    		System.out.println("	Receipt " + i + ":");
    		Receipt temp = this._Receipts.poll();
    		System.out.println("		Seller: " + temp.getSeller());
    		System.out.println("		Customer: " + temp.getCustomer());
    		System.out.println("		Shoe type: " + temp.getShoeType());
    		System.out.println("		Bought on discount: " + temp.isDiscount());
    		System.out.println("		Requested at: " + temp.getRequestTick());
    		System.out.println("		Issued at: " + temp.getIssuedTick());
    		System.out.println("		Amount sold: " + temp.getAmountSold());
    		System.out.println();
    		i ++;
    	}
    }
    /**
     * find the shoe in inventory
     * @param shoeType
     * @return - the index where the shoe is located
     */
    
    private int findShoe(String shoeType) {
    	Object [] temp = this._Inventory.toArray();
    	for (int i = 0; i < temp.length; i++) {
			if (((ShoeStorageInfo) temp[i]).getShoeType().equals(shoeType)) {
				return i;
			}
		}
    	return -1;
    }
}
