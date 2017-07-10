package bgu.spl.mics.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Request;
import bgu.spl.mics.RequestCompleted;



public class MessageBusImpl implements MessageBus {

	private ConcurrentLinkedQueue <MsgQueue> _MsgQueueList = new ConcurrentLinkedQueue <MsgQueue>();
	private ConcurrentLinkedQueue <RequestSubscriberList> _RequestList = new ConcurrentLinkedQueue <RequestSubscriberList>();
	private ConcurrentLinkedQueue <BroadcastSubscriberList> _BroadcastList = new ConcurrentLinkedQueue <BroadcastSubscriberList>();
	private ConcurrentLinkedQueue <RequesterAndRequest> _RequesterAndRequestList = new ConcurrentLinkedQueue <RequesterAndRequest>();
	
	private static class MessageBusHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }
	
    private MessageBusImpl() { }

    public static MessageBusImpl getInstance() {
        return MessageBusHolder.instance;
    }
	
	@Override
	public void subscribeRequest(Class<? extends Request> type, MicroService m) {
		int index = findRequest((Class<? extends Request<?>>) type); // find the index of the request
		if (index != -1) { // in the case where the index exists
			Object [] temp = this._RequestList.toArray();
			((RequestSubscriberList) temp[index]).addSubscriber(m); // Add subscribing Microservice
		}
		else { // the index does NOT exist
			this._RequestList.add(new RequestSubscriberList((Class<? extends Request<?>>) type));
			index = findRequest((Class<? extends Request<?>>) type);
			Object [] temp = this._RequestList.toArray();
			((RequestSubscriberList) temp[index]).addSubscriber(m);// Add subscriber
		}
	}
	
	/**
	 * The function will find the necessary Request
	 * @param type - the request we are looking for
	 * @return - the index where the Request is located in the _RequestList
	 */
	
	private int findRequest(Class<? extends Request<?>> type) {
		Object [] temp = this._RequestList.toArray();
		for (int i = 0; i < temp.length; i++) {
			if (((RequestSubscriberList) temp[i]).getRequest() == type) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		int index = findBroadcast(type); // find the broadcast index
		if (index != -1) { // if the broadcast exists
			Object [] temp = this._BroadcastList.toArray();
			((BroadcastSubscriberList) temp[index]).addSubscriber(m); // add subscriber
		}
		else { // No one has subscribed to this broadcast yet, so we create a new node of type BroadcastSubscriberList
			this._BroadcastList.add(new BroadcastSubscriberList(type));
			index = findBroadcast(type);
			Object [] temp = this._BroadcastList.toArray();
			((BroadcastSubscriberList) temp[index]).addSubscriber(m); // add subscriber
		}	
	}
	/**
	 * The function will find the necessary Broadcast
	 * @param type - the broadcast we are looking for
	 * @return - the index where the broadcast is located in _Broadcastlist
	 */
	
	private int findBroadcast(Class<? extends Broadcast> type) {
		Object [] temp = this._BroadcastList.toArray();
		for (int i = 0; i < temp.length; i++) {
			if (((BroadcastSubscriberList) temp[i]).getBroadcast() == type) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public <T> void complete(Request<T> r, T result) { // synchronized because receipts werent issued at the correct time (early)
		RequestCompleted <T> completed = new RequestCompleted <T> (r, result); // Create a new "RequestCompleted" message
		MicroService temp;
		int index = findRequester(r); // find the requester
		if (index != -1){//there are scenrios where a micro service unregisters from the message bus before receiving a request completed message and we won't find him in the requester list 
			Object [] tempArray = this._RequesterAndRequestList.toArray();
			temp = ((RequesterAndRequest) tempArray[index]).getMicroService(); // Create a pointer to the requesting MicroService
			_RequesterAndRequestList.remove(index); // remove the Request and Requester from the list
			int index2 = findMicroService(temp); // Find the requesting MicroService in the _MsgqueueList
			Object [] tempArray2 = this.get_MsgQueueList().toArray();
			((MsgQueue) tempArray2[index2]).Enqueue(completed); // Add completed message to the queue
		}
	}
	
	/**
	 * Find the requester from the pending requests list (RequesterAndrequestList)
	 * @param r - request
	 * @return index of the request and its request
	 */
	
	public <T> int findRequester(Request<T> r) {
		Object [] temp = this._RequesterAndRequestList.toArray();
		for (int i = 0; i < temp.length; i++) {
			if (((RequesterAndRequest) temp[i]).getRequest() == r) {
				return i;
			}
		}
		return -1;
	}

	@Override
	/**
	 * Find the correct broadcast from the broadcastlist
	 * import the the microservices subscribed to the broadcast from the broadcastsubscriberlist.class
	 * for each microservice, add the broadcasted message to the msgqueue
	 */
	public synchronized void sendBroadcast(Broadcast b) { //synchronized because broadcasts must be enterd into each microservices msgQueue simultaneously (Tick broadcasts for example) 
		int index = findBroadcast(b.getClass()); // find the broadcast
		if (index == -1) { // If no one previously subscribed to this broadcast
			this._BroadcastList.add(new BroadcastSubscriberList(b.getClass()));
			index = findBroadcast(b.getClass());
		}
		Object [] BroadcastListArray = this._BroadcastList.toArray(); // An array that each cell holds the broadcast and all MS subscribed to it
		ConcurrentLinkedQueue <MicroService> MicroServiceList = ((BroadcastSubscriberList) BroadcastListArray[index]).getSubscriberList(); // get the queue of Microservices
		Object [] MicroServiceArray = MicroServiceList.toArray(); // Make an array Microservice containing all microservices
		Object [] MsgQueueArray = this.get_MsgQueueList().toArray();
		int temp = 0;
		for (int i = 0; i < MicroServiceArray.length; i++) { // for all of the Microservices subscribed to this broadcast... do:
			temp = findMicroService((MicroService) MicroServiceArray[i]); // find the microservice in the _msgQueue field
			if(temp != -1)
				((MsgQueue) MsgQueueArray[temp]).Enqueue(b); // add the broadcasted message to the specified microservice's queue
		}
	}

	@Override
	public synchronized boolean sendRequest(Request<?> r, MicroService requester) { // synchronized because ??????
		int index = findRequest((Class<? extends Request<?>>) r.getClass());
		if (index == -1) { // No one subscribed to  this request so it does not exist
			return false;
		}
		Object [] temp = this._RequestList.toArray();
		if (((RequestSubscriberList) temp[index]).getSize() == 0) { // Nobody is able to handle the request
			return false;
		}
		_RequesterAndRequestList.add(new RequesterAndRequest(requester, r));
		MicroService tempMS = ((RequestSubscriberList) temp[index]).yourTurn();
		int index2 = findMicroService(tempMS); // Find the MicroService
		temp = this.get_MsgQueueList().toArray();
		((MsgQueue) temp[index2]).Enqueue(r); // Add the request to the messages list
		return true;
	}

	@Override
	public void register(MicroService m) {
		get_MsgQueueList().add(new MsgQueue(m));
	}

	@Override
	public void unregister(MicroService m) { // Do we need to synchronize the iteration?
		int i = findMicroService(m);
		if(i != -1) {
			get_MsgQueueList().remove(i);
			Object [] temp = this._BroadcastList.toArray();
			for (int j = 0; j < temp.length; j++) { // delete the microservice from the broadcastlist
				((BroadcastSubscriberList) temp[j]).unsubscribe(m);
			}
			temp = this._RequestList.toArray();
			for (int j = 0; j < temp.length; j++) { // delete the microservice from the requestlist
				((RequestSubscriberList) temp[j]).unsubscribe(m);
			}
			temp = this._RequesterAndRequestList.toArray();
			for (int j = 0; j < temp.length; j++) { // delete the pair from the _RequesterAndRequestList
				if (((RequesterAndRequest) temp[j]).getMicroService() == m) {
					this._RequesterAndRequestList.remove(temp[j]);
				}
			}
		}
		else
			throw new IllegalStateException("MicroService Not Found");
	}
	
	/**
	 * The function will find the necessary MicroService
	 * @param m - Microservice we are looking for
	 * @return - The index of the MicroService in the _MsgQueueList
	 */
	
	private int findMicroService(MicroService m) {
		Object [] temp = this.get_MsgQueueList().toArray();
		for (int i = 0; i < temp.length; i++) {
			if (((MsgQueue) temp[i]).getMicroService() == m) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message ans; // create a message instance
		int index = findMicroService(m); // find the MicroService
		if (index == -1) { // if the MicroService was not instantiated...
			throw new IllegalStateException("MicroService Not Found");
		}
		else { // the microservice exists
			Object [] temp = this.get_MsgQueueList().toArray();
			ans = ((MsgQueue) temp[index]).Dequeue(); // read the next message from the queue of messages 
			while (ans == null) { // ans will be null if there is a illegal state exception and not if it is waiting for the next message 
				ans = ((MsgQueue) temp[index]).Dequeue();
			}
		}
		return ans;
	}

	public ConcurrentLinkedQueue <MsgQueue> get_MsgQueueList() {
		return _MsgQueueList;
	}

	

}
