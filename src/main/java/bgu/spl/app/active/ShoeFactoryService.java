package bgu.spl.app.active;



import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import bgu.spl.app.passive.ManufacturingOrderRequest;
import bgu.spl.app.passive.Receipt;
import bgu.spl.app.passive.TickBroadcast;
import bgu.spl.mics.MicroService;

public class ShoeFactoryService extends MicroService {

	private int currTick = 0;
	private ArrayList <ManufacturingOrderRequest> _completionList = new ArrayList <ManufacturingOrderRequest>(); // all MOR's- null if work day
	private final static Logger l = Logger.getLogger("ShoeFactoryService");
	private CountDownLatch start;
	
	public ShoeFactoryService(String name, CountDownLatch c) {
		super(name);
		start = c;
	}
	
	private void setTick(int t) {
		this.currTick = t;
	}

	@Override
	protected void initialize() {
		
		l.log(Level.INFO, this.getName() + " began initialization.");
		this.subscribeBroadcast(TickBroadcast.class, tb -> {
			setTick(tb.getTick());
			l.log(Level.FINE, this.getName() + " received tick #" + this.currTick);
			if (this.currTick > tb.getDuration()) {
				l.log(Level.FINE, this.getName() + " will now terminate. Time is up!!");
				this.terminate();
			}
			
			if (this._completionList.size() > 0) { // will check if we need to return a MOR
				ManufacturingOrderRequest temp = this._completionList.remove(0);
				if (temp != null) {
					Receipt receipt = new Receipt(this.getName(), "Store", temp.getShoeType(), false, this.currTick, temp.getTick(), temp.getAmount());
					l.log(Level.INFO, this.getName() + " completed order for " + temp.getAmount() + " " + temp.getShoeType() + " that was requested at tick " + receipt.getRequestTick());
					this.complete(temp, receipt);
				}
			}
			
		});
		
		this.subscribeRequest(ManufacturingOrderRequest.class, MOR -> { // once received a MOR -> push nulls for work days and MOR for completion day
			int amount = MOR.getAmount();
			l.log(Level.INFO, this.getName() + " received new ManufacturingOrderRequest for " + MOR.getAmount() + " " + MOR.getShoeType());
			if (this._completionList.size() == 0) { // if no previous projects exist
				for (int i = 0; i < amount; i++) {
					this._completionList.add(null);
				}
				this._completionList.add(MOR);
			}
			else {
				for (int i = 0; i < amount-1; i++) {
					this._completionList.add(null);
				}
				this._completionList.add(MOR);
			}
		});
		l.log(Level.INFO, this.getName() + "- countdown.");
		this.start.countDown();
	}

}
