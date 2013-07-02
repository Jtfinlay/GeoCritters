package com.finlay.geomonsters.battle;

import android.content.res.Resources;

public class Action {

	protected String 	_name;
	protected String 	_type;
	protected int 		_animation;
	
	protected void init(Resources res) {}
	
	public String getName() {
		return _name;
	}
	public String getType() {
		return _type;
	}
	public int getAnimationType() {
		return _animation;
	}
}
