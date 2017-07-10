package bgu.spl.app.passive;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
	
	private int tick;
	private final int duration;

	/**
	 * @param tick
	 */
	
	public TickBroadcast(int tick, int duration) {
		this.tick = tick;
		this.duration = duration;
	}
	
	public int getTick() {
		return this.tick;
	}
	
	public int getDuration() {
		return this.duration;
	}
}
