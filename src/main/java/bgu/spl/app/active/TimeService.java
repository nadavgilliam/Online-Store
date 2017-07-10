package bgu.spl.app.active;

import bgu.spl.app.passive.TickBroadcast;
import bgu.spl.mics.MicroService;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TimeService extends MicroService {
	
	private int currTick = 0;
	private Timer timer = new Timer();
	private final static Logger l = Logger.getLogger("TimeService");
	private CountDownLatch start;
	
	private TimerTask timerTask = new TimerTask() {
		public void run(){
			setTick(currTick + 1);
			if (currTick <= duration) { 
				l.log(Level.INFO, "Timer sent tick #" + currTick);
			}
			sendBroadcast(new TickBroadcast(currTick, duration)); // send new tick
			if (currTick > duration) { // time to terminate
				timer.cancel();
			}
	}};
	
	private int speed;
	private int duration;
	
	
	public TimeService(String name, int speed, int duration, CountDownLatch c) {
		super(name);
		this.speed = speed;
		this.start = c;
		this.duration = duration;
		
	}

	@Override
	protected void initialize() {
		try {
			l.log(Level.INFO, this.getName() + " is waiting for all Microservices to complete initiazlization.");
			this.start.await(); // waiting for count down to complete
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		l.log(Level.INFO, this.getName() + " started ticking...");
		timer.scheduleAtFixedRate(timerTask, 0, speed); // begin sending ticks
		this.terminate();
		
	}
	
	private void setTick(int t){
		this.currTick = t;
	}

}
