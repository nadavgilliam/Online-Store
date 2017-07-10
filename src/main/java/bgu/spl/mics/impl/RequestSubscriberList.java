package bgu.spl.mics.impl;

import java.util.concurrent.ConcurrentLinkedQueue;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Request;

/**
 * this class holds a type of request and a queue of all its subscribers
 * @author lazardg
 *
 */
public class RequestSubscriberList {
	
	private Class<? extends Request<?>> _type;
	private ConcurrentLinkedQueue <MicroService> _SubscriberList = new ConcurrentLinkedQueue <MicroService>();
	private int _size_counter; // the index of the next subscriber to handle incoming request
	
	/**
	 * Constructor
	 * @param other - request
	 */
	
	public RequestSubscriberList(Class<? extends Request<?>> other) {
		_type = other;
		_size_counter = 0;
	}
	
	/**
	 * Add microservice to the _SubscriberList
	 * @param m - Microservice to be added
	 */
	
	public void addSubscriber(MicroService m) {
		if (this._SubscriberList.contains(m) == false) {
			_SubscriberList.add(m);
		}
	}
	
	public Class<? extends Request<?>> getRequest() {
		return this._type;
	}
	
	public ConcurrentLinkedQueue <MicroService> getSubscriberList() {
		return this._SubscriberList;
	}
	
	/**
	 * Remove the Microservice from the _SubscriberList
	 * @param m - MicroService to be removed
	 */
	
	public void unsubscribe(MicroService m) {
		if (this._SubscriberList.contains(m)) {
			Object [] temp = this._SubscriberList.toArray();
			boolean found = false;
			int index = 0;
			for (int i = 0; i < temp.length || found == false; i++) {
				if (m == temp[i]) {
					found = true;
					index = i;
				}
			}
			if (index < this._size_counter) {
				this._size_counter --;
				this._SubscriberList.remove(m);
			}
		}
	}
	
	public int getSize() {
		return this._SubscriberList.size();
	}
	
	/**
	 * will find the micro service whose turn it is to handle incoming request.
	 * maintains round robin by updating _size_counter
	 * @return the micro service whose turn it is to handle incoming request.
	 */
	
	public synchronized MicroService yourTurn() {//synchronized to maintain round robin (what if several requests of same type arrive??)
		if (this._size_counter < this._SubscriberList.size()) {//if the index of the next micro service to handle request is not greater than the queue size
			Object [] temp = this._SubscriberList.toArray();
			MicroService ans = (MicroService) temp [this._size_counter];
			_size_counter ++;
			return ans;
		}
		else {// we return the first micro service on the list and update _size_counter to point at the second MS
			MicroService ans = this._SubscriberList.peek();
			_size_counter = 1;
			return ans;
		}
	}
}
