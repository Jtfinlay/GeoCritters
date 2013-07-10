package com.finlay.geomonsters.battle;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Creature {

	private static final String TAG = "Creature";
	private String 	_name;
	private String 	_nickName;
	private String 	_type;
	private Bitmap 	_image;
	private RectF	_drawRect;
	private RectF	_original_drawRect;
	
	private double 	_speed; 						// Time between attacks
	private double	_nextAttackCounter = _speed;	// Time until next attack
	private double	_lastTimeUpdate = 0;
	
	private ArrayList<String> 	_attacks;
	private Animation 			_animation;
	
	private float Max_HP = 100f;	// TODO: Should get from XML.
	private float HP = Max_HP;		// TODO: Get current HP from XML.

	
	public Creature(String name, Bitmap image, String type, int speed,  ArrayList<String> attacks) {
		_name = name;
		_nickName = name;
		_image = image;
		_attacks = attacks;
		_speed = speed;
		_type = type;
		
		resetNextAttackCounter();
	}
	// Called when replacing this instance with a new creature instance
	public void setAs(Creature other) {
		_name = other.getName();
		_nickName = other.getNickName();
		_image = other.getImage();
		_speed = other.getAttackSpeed();
		_type = other.getType();
		resetNextAttackCounter();
		this.setDrawRect(_original_drawRect);
	}
	public String getName() {
		return _name;
	}
	public String getNickName() {
		return _nickName;
	}
	public void setNickName(String nickname) {
		_nickName = nickname;
	}
	public ArrayList<String> getAttackList() {
		return _attacks;
	}
	public void Hurt(int amt) {
		HP -= amt;
		HP = (HP > 0 ? HP : 0);
	}	
	public double getAttackSpeed() {
		return _speed;
	}
	public double getNextAttackCounter() {
		return _nextAttackCounter;
	}
	public void resetNextAttackCounter() {
		_nextAttackCounter = _speed;
	}
	public void ResumeAttackCounter() {
		_lastTimeUpdate = System.currentTimeMillis();
	}
	public void idleUpdate() {
		_nextAttackCounter -= (System.currentTimeMillis() - _lastTimeUpdate);
		_lastTimeUpdate = System.currentTimeMillis();
	}
	public double getNextAttackPercent() {
		double amt = (_speed - _nextAttackCounter) / _speed;
		amt = (amt > 0) ? amt : 0;
		amt = (amt < 1) ? amt : 1;
		return amt;
	}
	
	public int getMaxHP() {
		return (int) Max_HP;
	}
	public int getHP() {
		return (int) HP;
	}
	public String getType() {
		return _type;
	}
	public float getHealthPercent() {
		return HP / Max_HP;
	}
	
	public Bitmap getImage() {
		return _image;
	}
	public int getWidth() {
		return _image.getWidth();
	}
	public int getHeight() {
		return _image.getHeight();
	}
	public void setDrawRect(RectF drawDest) {
		_original_drawRect = new RectF(drawDest);
		_drawRect = drawDest;
		
		// we want to align the rendering on the bottom-centre of the destination rectangle,
		// and we also need to scale the image to the given drawing area		
		
		// figure out scaling factor
		float rx = _drawRect.width() / _image.getWidth();
		float ry = _drawRect.height() / _image.getHeight();
		float scale = (rx < ry) ? rx : ry;
		
		// position offset in relation to scaling factor
		float dx = _drawRect.width()*(1f-scale)/2f;
		float dy = _drawRect.height()-scale*_image.getHeight();
		
		_drawRect.left += dx;
		_drawRect.right -= dx;
		_drawRect.top += dy;
		
		_animation = new Animation(_image, _drawRect);
	}
	
	public void render(Canvas c, Paint p) {		
		_animation.renderCreatureFrame(c, p);
	}
	public void performAnimation(int animID) {
		_animation.start(animID);
	}
	

}
