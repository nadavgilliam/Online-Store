package bgu.spl.app.active;

import java.util.List;
import java.util.logging.*;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import bgu.spl.app.passive.NewDiscountBroadcast;
import bgu.spl.app.passive.PurchaseOrderRequest;
import bgu.spl.app.passive.PurchaseSchedule;
import bgu.spl.app.passive.TickBroadcast;
import bgu.spl.mics.MicroService;

public class WebsiteClientService extends MicroService {
	
	private int currTick = 0;
	private List <PurchaseSchedule> purchaseSchedule;
	private Set <String> wishList;
	private final static Logger l = Logger.getLogger("WebsiteClientService");
	private CountDownLatch start;
	
	public WebsiteClientService(String name, List <PurchaseSchedule> l, Set <String> wl, CountDownLatch c) {
		super(name);
		start = c;
		purchaseSchedule = l;
		wishList = wl;
	}
	
	private void setTick(int t) {
		this.currTick = t;
	}

	@Override
	protected void initialize() {
		
		Logger.getLogger("WebsiteClientService").log(Level.INFO, this.getName() + " began initialization.");
		
		this.subscribeBroadcast(TickBroadcast.class, tb -> { // webclient subscribes to Ticks
			setTick(tb.getTick());
			Logger.getLogger("WebsiteClientService").log(Level.FINE, this.getName() + " received tick #" + this.currTick);
			if (this.currTick > tb.getDuration()) {
				Logger.getLogger("WebsiteClientService").log(Level.FINE, this.getName() + " will now terminate. Time is up!!");
				this.terminate();
			}

			for (int i = 0; i < purchaseSchedule.size(); i++) { // Check purchase schedule
				if (currTick == purchaseSchedule.get(i).getTick()) {
					Logger.getLogger("WebsiteClientService").log(Level.FINE, this.getName() + " found item " + purchaseSchedule.get(i).getShoe() + " in purchase schedule.");
					if (this.sendRequest(new PurchaseOrderRequest(purchaseSchedule.get(i).getShoe(), 1, false, this.getName(), currTick), onComplete -> { // if found, send a purchase request
						boolean found = false;
						for (int j = 0; j < this.purchaseSchedule.size() && found == false; j++) { // search for request in purchase schedule and delete
							if (onComplete != null) {
								if (this.purchaseSchedule.get(j).getShoe().equals(onComplete.getShoeType()) && this.purchaseSchedule.get(j).getTick() == tb.getTick()) {
									Logger.getLogger("WebsiteClientService").log(Level.INFO, this.getName() + " will now remove item " + purchaseSchedule.get(j).getShoe() + " from purchase schedule after completing purchase.");
									this.purchaseSchedule.remove(j);
									found = true;
									if (this.wishList.remove(onComplete.getShoeType())) // check if item is on wishlist as well
										l.log(Level.INFO, this.getName() + " will remove item " + onComplete.getShoeType() + " from wishlist as well.");
									if (this.purchaseSchedule.size() == 0 && this.wishList.isEmpty()) { // check if the websiteclient should terminate after completing all purchases
										Logger.getLogger("WebsiteClientService").log(Level.INFO, this.getName() + " will terminate since completed purchase schedule and wishlist.");
										this.terminate();
									}
								}
							}
						}
					}));
					else { // if the request returned false (no one able to accept)
						Logger.getLogger("WebsiteClientService").log(Level.INFO, this.getName() + " will now now remove item " + purchaseSchedule.get(i).getShoe() + " from purchase schedule because sent request returned false.");
						this.purchaseSchedule.remove(i);
						if (this.purchaseSchedule.size() == 0 && this.wishList.isEmpty()) {
							Logger.getLogger("WebsiteClientService").log(Level.INFO, this.getName() + " will terminate since completed purchase schedule and wishlist.");
							this.terminate();
						}
					}
				}
			}
		});
		
		this.subscribeBroadcast(NewDiscountBroadcast.class, ndb -> { // subscribe to discount broadcasts
			Logger.getLogger("WebsiteClientService").log(Level.FINE, this.getName() + " received a new discount broadcast: " + ndb.getShoeType());
			if (this.wishList.contains(ndb.getShoeType())) { // check wish list
				Logger.getLogger("WebsiteClientService").log(Level.INFO, this.getName() + " found item " + ndb.getShoeType() + " on wishlist.");
				if (this.sendRequest(new PurchaseOrderRequest(ndb.getShoeType(), 1, true, this.getName(), this.currTick), onComplete -> {// if item is on wishlist then send purchase request
					if(onComplete != null) {
						Logger.getLogger("WebsiteClientService").log(Level.INFO, this.getName() + " will remove " + ndb.getShoeType() + " from wishlist.");
						this.wishList.remove(ndb.getShoeType());
						boolean found = false;
						for (int i = 0; i < this.purchaseSchedule.size() && !found; i++) {
							if (this.purchaseSchedule.get(i).getShoe().equals(ndb.getShoeType())) {
								this.purchaseSchedule.remove(i);
								found = true;
							}
						}
						if (this.purchaseSchedule.size() == 0 && this.wishList.isEmpty()) { // if completed all purchases, terminate
							Logger.getLogger("WebsiteClientService").log(Level.INFO, this.getName() + " will terminate since completed purchase schedule and wishlist.");
							this.terminate();
						}
					}
				}));
			}
		});
		
		Logger.getLogger("WebsiteClientServer").log(Level.INFO, this.getName() + "- countdown.");
		this.start.countDown();
	}
}
