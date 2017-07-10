package bgu.spl.mics.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bgu.spl.app.active.TestService;
import bgu.spl.app.passive.TestBroadcast;
import bgu.spl.app.passive.TestRequest;
import bgu.spl.mics.Message;

public class MessageBusImplTest {
	
	TestService test = new TestService("test");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRegister() {
		MessageBusImpl.getInstance().register(test);
		MessageBusImpl.getInstance().unregister(test);
	}
	
	@Test
	public void testUnregister() {
		MessageBusImpl.getInstance().register(test);
		MessageBusImpl.getInstance().unregister(test);
	}
	
	@Test
	public void testSubscribeRequest() {
		MessageBusImpl.getInstance().register(test);
		MessageBusImpl.getInstance().subscribeRequest(TestRequest.class, test);
	}

	@Test
	public void testSubscribeBroadcast() {
		MessageBusImpl.getInstance().subscribeBroadcast(TestBroadcast.class, test);
	}

	@Test
	public void testSendBroadcast() {
		MessageBusImpl.getInstance().sendBroadcast(new TestBroadcast());
	}

	@Test
	public void testSendRequest() {
		TestService temp = new TestService("requester");
		MessageBusImpl.getInstance().register(temp);
		boolean ans = MessageBusImpl.getInstance().sendRequest(new TestRequest(), temp);
		MessageBusImpl.getInstance().unregister(temp);
		assertTrue(ans);
	}

	@Test
	public void testComplete() {

		
	}
	
	@Test
	public void testAwaitMessage() {
		MessageBusImpl.getInstance().register(test);
		MessageBusImpl.getInstance().subscribeBroadcast(TestBroadcast.class, test);
		MessageBusImpl.getInstance().sendBroadcast(new TestBroadcast());
		Message m=null;
		try {
			 m = MessageBusImpl.getInstance().awaitMessage(test);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean ans = false;
		if (m != null) ans = true;
		assertTrue(ans);
	}

}
