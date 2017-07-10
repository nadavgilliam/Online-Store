package bgu.spl.app.active;


import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import bgu.spl.app.passive.DiscountSchedule;
import bgu.spl.app.passive.ManufacturingOrderRequest;
import bgu.spl.app.passive.NewDiscountBroadcast;
import bgu.spl.app.passive.Order;
import bgu.spl.app.passive.RestockRequest;
import bgu.spl.app.passive.Store;
import bgu.spl.app.passive.TickBroadcast;
import bgu.spl.mics.MicroService;

public class ManagementService extends MicroService {
	
	private int currTick = 0; 
	private List <DiscountSchedule> _discountSchedule;
	private CountDownLatch start;
	private HashMap <RestockRequest, Order> Orders = new HashMap <RestockRequest, Order>(); // A Hashmap that jolds the rr that made a MOR as a key and Order class as a field


	public ManagementService(String name, List<DiscountSchedule> dS, CountDownLatch c) { // constructor
		super(name);
		this._discountSchedule = dS;
		start = c;
	}
	/**
	 * sets currTick
	 * @param t - New Tick
	 */
	
	private void setTick(int t) { 
		this.currTick = t;
	}

	@Override
	protected void initialize() {
		Logger.getLogger("ManagementService").log(Level.INFO, this.getName() + " began initialization.");
		this.subscribeBroadcast(TickBroadcast.class, tb -> { // Tick broadcast + callback
			setTick(tb.getTick()); // update tick
			Logger.getLogger("ManagementService").log(Level.FINE, this.getName() + " received tick #" + this.currTick);
			if (this.currTick > tb.getDuration()) { // If time to terminate (time is up)
				Logger.getLogger("ManagementService").log(Level.FINE, this.getName() + " will now terminate. Time is up!!");
				this.terminate();
			}
			
			for (int i = 0; i < _discountSchedule.size(); i++) { // Check if discount needs to be broadcasted
				if (this._discountSchedule.get(i).getTick() == this.currTick) {
					Store.getInstance().addDiscount(this._discountSchedule.get(i).getShoeType(), this._discountSchedule.get(i).getAmount());
					Logger.getLogger("ManagementService").log(Level.INFO, this.getName() + " will send a NewDiscountBroadcast for " + this._discountSchedule.get(i).getShoeType());
					this.sendBroadcast(new NewDiscountBroadcast(this._discountSchedule.get(i).getShoeType(), this._discountSchedule.get(i).getAmount())); // If so - send broadcast message for new discount
				}
			}
		});
		
		this.subscribeRequest(RestockRequest.class, RR -> {
			Logger.getLogger("ManagementService").log(Level.INFO, this.getName() + " received a RestockRequest for " + RR.getShoeType());
			
			boolean found = false;
			
			for (Order temp : Orders.values()) { // check if previous order for specific shoe contains enough for current rr
				if (temp.getShoeType().equals(RR.getShoeType())) {
					int index = temp.getEmptySpace(); // find index for empty space in the array
					if (index != -1) {
						temp.setR(index, RR);
						found = true;
					}
				}
			}
			
			if (!found) { // none found
				if (sendRequest(new ManufacturingOrderRequest(RR.getShoeType(), (this.currTick%5) + 1, this.currTick), MOR -> {  // 
					Order o = Orders.get(RR);
					Store.getInstance().add(o.getShoeType(), o.countTheNulls());
					Store.getInstance().file(MOR);
					for (int i = 0; i < o.getR().length; i++) { // once completed, complete all the rr
						if (o.getR()[i] != null) {
							this.complete(o.getR()[i], true);
						}
					}
					Orders.remove(RR);
				})) {
					Order o = new Order((currTick%5)+1, RR.getShoeType()); // add the key to hashmap
					o.setR(0, RR);
					Orders.put(RR, o);
				}
				else { // no factories
					Logger.getLogger("ManagementService").log(Level.INFO, this.getName() + " responded that no factories are available. Cannot restock.");
					this.complete(RR, false);
				}
			}
			
			
		});

		Logger.getLogger("ManagementService").log(Level.INFO, this.getName() + "- countdown.");
		this.start.countDown();
	}	
}