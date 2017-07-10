package bgu.spl.mics.impl;

import java.util.concurrent.LinkedBlockingQueue;
import bgu.spl.mics.Message;			//We will use this type of queue in order to hold
import bgu.spl.mics.MicroService;		// the messages each microservice has.

/**
 * this class holds a micro service and a linked blocking queue(which knows to wait until there is an element in the queue) that serves as the micro service's message queue
 * @author lazardg
 *
 */

public class MsgQueue {
	
	private MicroService _m;
	private LinkedBlockingQueue <Message> _queue;
	
	/**
	 * Constructor
	 * @param m - the microservice added to the Message bus
	 */
	
	public MsgQueue (MicroService m){ 
		_m = m;
		_queue = new LinkedBlockingQueue <Message> ();
	}
	
	/**
	 * Add a new message to the Queue
	 * @param m - message to add
	 */
	
	public void Enqueue(Message m) { 
		try {
			_queue.put(m);
		} catch (InterruptedException ignored) {	}
	}
	
	/**
	 * Remove the oldest message from the message queue
	 * @return- Message
	 */
	
	public Message Dequeue() { 
		try {
			return _queue.take();
		} catch (InterruptedException ignored) {
			return null; // Need to prepare for null return
		}
	}
	
	/**
	 * Checks if the Message queue is empty
	 * @return - true is message queue is empty and false otherwise
	 */
	
	public boolean isEmpty() {
		return _queue.isEmpty();
	}
	
	/**
	 * Will check if the microservice is the same as "this" one
	 * @param m - other microservice
	 * @return - true is so, and false otherwise
	 */
	
	public boolean sameMicroService(MicroService m) { // Can we guarantee that we are deleting the proper MicroService?
		if (_m == m) {
			return true;	
		}
		return false;
	}
	
	/**
	 * Will return the Microservice we are looking for
	 * @return - The MicroService.
	 */
	
	public MicroService getMicroService() {
		return _m;
	}	
	
}
