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
import android.util.Log;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.XMLParser;

public class Creature {

	private static final String TAG = "Creature";
	private String _name;
	private Bitmap _image;
	private ArrayList<String> _attacks;
	private Animation _animation;
	private float Max_HP = 100f;
	private float HP = Max_HP;
	
	public Creature(String name, Bitmap image, ArrayList<String> attacks) {
		_name = name;
		_image = image;
		_attacks = attacks;
		_animation = new Animation(_image);
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
	public int getMaxHP() {
		return (int) Max_HP;
	}
	public int getHP() {
		return (int) HP;
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
		// 0 - Nothing
		// 1 - Hurt
		// 2 - Strike
		_animation.start(animID);
	}
	

}
