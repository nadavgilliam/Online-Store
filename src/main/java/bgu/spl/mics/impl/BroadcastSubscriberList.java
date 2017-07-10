package bgu.spl.mics.impl;

import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

/**
 * this class holds a type of Broadcast and a concurrent linked queue that is used as a list with all the subscribing micro services
 * @author lazardg
 *
 */

public class BroadcastSubscriberList {
	
	private Class<? extends Broadcast> _type;
	private ConcurrentLinkedQueue <MicroService> _SubscriberList = new ConcurrentLinkedQueue <MicroService>();	
	public BroadcastSubscriberList(Class<? extends Broadcast> other) {
		_type = other;
	}
	
	public void addSubscriber(MicroService m) {
		if (this._SubscriberList.contains(m) == false) {
			_SubscriberList.add(m);
		}
	}
	
	public Class<? extends Broadcast> getBroadcast() {
		return this._type;
	}
	
	public ConcurrentLinkedQueue <MicroService> getSubscriberList() {
		return this._SubscriberList;
	}
	
	public void unsubscribe(MicroService m) {
		this._SubscriberList.remove(m);
	}
}
	
