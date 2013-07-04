package com.finlay.geomonsters.battle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.XMLParser;

public class Creature {

	private static final String TAG = "Creature";
	private String 	_name;
	private String 	_type;
	private Bitmap 	_image;
	
	private double 	_speed; 						// Time between attacks
	private double	_nextAttackCounter = _speed;	// Time until next attack
	private double	_lastTimeUpdate = 0;
	
	private ArrayList<String> 	_attacks;
	private Animation 			_animation;
	
	private float Max_HP = 100f;	// TODO: Should get from XML.
	private float HP = Max_HP;

	
	public Creature(String name, Bitmap image, String type, int speed,  ArrayList<String> attacks) {
		_name = name;
		_image = image;
		_attacks = attacks;
		_speed = speed;
		_type = type;
		_animation = new Animation(_image);
		
		resetNextAttackCounter();

	}
	public ArrayList<String> getAttackList() {
		return _attacks;
	}
	public void Hurt(int amt) {
		HP -= amt;
		HP = (HP > 0 ? HP : 0);
	}	
	public String getName() {
		return _name;
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
		return Math.max((_speed - _nextAttackCounter) / _speed, 0);
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
	
	
	public void render(Canvas c, Paint p) {
		_animation.renderFrame(c, p);
	}
	public void performAnimation(int animID) {
		_animation.start(animID);
	}
	

}
