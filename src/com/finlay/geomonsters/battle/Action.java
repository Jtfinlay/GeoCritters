package com.finlay.geomonsters.battle;

import android.content.res.Resources;

public class Action {

	public static final int ATTACKER_PLAYR = 0;
	public static final int ATTACKER_OTHER = 1;
	
	protected String 	_name;
	protected String 	_type;
	protected int 		_animation;
	protected int		_attacker;
	
	public double	time = 0;	// when to execute next step
	public int		step = 0;	// next step to execute
	
	public String getName() {
		return _name;
	}
	public String getType() {
		return _type;
	}
	public int getAnimationType() {
		return _animation;
	}
	public int getAttacker() {
		return _attacker;
	}
}
