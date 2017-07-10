package bgu.spl.app.passive;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StoreTest {

	private Store st;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		this.st = Store.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoad() {
		ShoeStorageInfo i = new ShoeStorageInfo("test Shoe", 0, 0);
		ShoeStorageInfo[] a = new ShoeStorageInfo[1];
		a[0] = i;
		this.st.load(a);
		
	}

	@Test
	public void testTake() {
	
		BuyResult r;
		r =  this.st.take("test Shoe", false);
		assertEquals(r, BuyResult.NOT_IN_STOCK);
	}

	@Test
	public void testAdd() {
		
		BuyResult r;
		this.st.add("test Shoe", 2);
		r = this.st.take("test Shoe", false);
		assertEquals(r, BuyResult.REGULAR_PRICE);
	}

	@Test
	public void testAddDiscount() {
		
		BuyResult r;
		this.st.addDiscount("test Shoe",1);
		r = this.st.take("test Shoe", true);
		assertEquals(r, BuyResult.DISCOUNTED_PRICE);
	}

}
