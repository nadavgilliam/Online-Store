package bgu.spl.mics.impl;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.Request;

/**
 * this class holds a pending request and the micro Service that sent the request
 * @author lazardg
 *
 */
public class RequesterAndRequest {
	
	private MicroService _MS;
	private Request <?> _request;
	
	/**
	 * Copy constructor
	 * @param m - microservice
	 * @param request - request
	 */
	
	public RequesterAndRequest(MicroService m, Request <?> request) {
		this._MS = m;
		_request = request;
	}
	
	public Request<?> getRequest() {
		return this._request;
	}
	
	public MicroService getMicroService() {
		return this._MS;
	}

}
