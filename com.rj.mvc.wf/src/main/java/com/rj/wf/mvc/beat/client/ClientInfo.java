package com.rj.wf.mvc.beat.client;

import com.rj.wf.mvc.BeatContext;

/**
 * 暂不支持上传文件功能
 * @author renjie
 *
 */
public class ClientInfo {

	private BeatContext beat;
	private CookieHandler cookie = null;
	
	public ClientInfo(BeatContext beat) {
		this.beat = beat;
	}

	public CookieHandler getCookies() {
		if (cookie == null) {
			cookie = new CookieHandler(beat);
		}

		return cookie;
	}
	
//	public WFHttpServletRequestWrapper getUploads() {
//		HttpServletRequest request = beat.getRequest();
//		return (request instanceof WFHttpServletRequestWrapper) ? (WFHttpServletRequestWrapper) request : null;
//	}
	
	
//	public boolean isUpload() {
//		return getUploads() == null ? false : true;
//	}
	
	
}
