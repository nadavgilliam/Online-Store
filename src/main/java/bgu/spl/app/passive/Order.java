package bgu.spl.app.passive;

/**
 * A class that holds the rr and the shoetype. The rr's are in an array the size of the amount of shoes ordered
 * @author gilliam
 *
 */

public class Order {
	
	private final String shoeType;
	private RestockRequest [] r;

	public Order(int x, String s) {
		this.shoeType = s;
		this.r = new RestockRequest[x];
	}

	public RestockRequest[] getR() {
		return r;
	}
	/**
	 * add rr to array
	 * @param i
	 * @param rr
	 */

	public void setR(int i, RestockRequest rr) {
		r[i] = rr;
	}

	public String getShoeType() {
		return shoeType;
	}
	
	/**
	 * find the empty index in array if exists
	 * @return
	 */
	
	public int getEmptySpace() {
		for (int i = 0; i < r.length; i++) {
			if (r[i] == null) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * in order to find out how many shoes we add to the store we count the "nulls". empty spaces
	 * @return amount of shoes to add to store
	 */
	public int countTheNulls() {
		int ans = 0;
		for (int i = 0; i < r.length; i++) {
			if (r[i] == null) {
				ans += 1;
			}
		}
		return ans;
	}

}
