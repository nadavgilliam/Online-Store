/**
 * 
 */
package bgu.spl.app.active;

import java.util.concurrent.CountDownLatch;

import bgu.spl.app.passive.BuyResult;
import bgu.spl.app.passive.PurchaseOrderRequest;
import bgu.spl.app.passive.Receipt;
import bgu.spl.app.passive.RestockRequest;
import bgu.spl.app.passive.Store;
import bgu.spl.app.passive.TickBroadcast;
import bgu.spl.mics.MicroService;
import java.util.logging.*;
/**
 * @author gilliam
 *
 */
public class SellingService extends MicroService {
	
	private int currTick = 0;
	private CountDownLatch start;
	
	public SellingService(String name, CountDownLatch c) {
		super(name);
		this.start = c;
	}

	@Override
	protected void initialize() {
        Logger.getLogger("SellingService").log(Level.INFO, this.getName() + " began initialization.");

		this.subscribeBroadcast(TickBroadcast.class, tb -> { // subscribe to ticks
			setTick(tb.getTick());
			Logger.getLogger("SellingService").log(Level.FINE, this.getName() + " received tick #" + this.currTick);
			if (this.currTick > tb.getDuration()) {
				Logger.getLogger("SellingService").log(Level.FINE, this.getName() + " will now terminate. Time is up!!");
				this.terminate();
			}
		});
        
		this.subscribeRequest(PurchaseOrderRequest.class, por -> {
			
			String customerName = por.getName();
			String shoeType = por.getShoeType();
			int issuedTick = por.getTick();
			boolean onlyDiscount = por.getOnlyDiscount();
			Logger.getLogger("SellingService").log(Level.INFO, this.getName() + " received a PurchaseOrderRequest from " + customerName + " for " + shoeType);
			BuyResult r;
			r = Store.getInstance().take(shoeType, onlyDiscount); // according to the buyresult enum we decide how to act
			if (r == BuyResult.NOT_ON_DISCOUNT) {
				Logger.getLogger("SellingService").log(Level.INFO, this.getName() + " responded to " + customerName + " that " + shoeType + " is NOT_ON_DISCOUNT.");
				this.complete(por, null);
			}
			else if (r == BuyResult.REGULAR_PRICE) {
				Receipt receipt = new Receipt(this.getName(), customerName, shoeType, false, currTick, issuedTick, 1);
				Store.getInstance().file(receipt);
				Logger.getLogger("SellingService").log(Level.INFO, this.getName() + " sold " + shoeType + " to " + customerName + " for REGULAR_PRICE.");
				this.complete(por, receipt);
			}
			else if (r == BuyResult.DISCOUNTED_PRICE) {
				Receipt receipt = new Receipt(this.getName(), customerName, shoeType, true, currTick, issuedTick, 1);
				Store.getInstance().file(receipt);
				Logger.getLogger("SellingService").log(Level.INFO, this.getName() + " sold " + shoeType + " to " + customerName + " for DISCOUNTED_PRICE.");
				this.complete(por, receipt);
			}
			else {
				if (this.sendRequest(new RestockRequest(shoeType), oc -> {
					if (oc) {
					Receipt receipt = new Receipt(this.getName(), customerName, shoeType, false, currTick, issuedTick, 1);
					Store.getInstance().file(receipt);
					Logger.getLogger("SellingService").log(Level.INFO, this.getName() + " told " + customerName + " that RestockRequest for" + shoeType + " returned true.");
					this.complete(por, receipt);
					}
				
				else {
					Logger.getLogger("SellingService").log(Level.INFO, this.getName() + " told " + customerName + " that " + shoeType + " is NOT_IN_STOCK. Nobody can manufacture the RestockRequest.");
					this.complete(por, null);
				}	
				}));
			}
		});
		Logger.getLogger("SellingService").log(Level.INFO, this.getName() +"- countdown.");
		this.start.countDown();
	}
	
	private void setTick(int t) {
		this.currTick = t;
	}

}
