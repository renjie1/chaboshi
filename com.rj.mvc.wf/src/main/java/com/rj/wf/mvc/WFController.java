package com.rj.wf.mvc;

import com.rj.wf.mvc.log.Logger;
import com.rj.wf.mvc.log.LoggerFactory;

public abstract class WFController {

	protected Logger _WFLOG = LoggerFactory.getLogger(WFController.class);
	
	protected BeatContext beat() {
		return BeatContext.current();
	}
	
}
