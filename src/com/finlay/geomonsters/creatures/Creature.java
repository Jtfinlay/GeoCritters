package com.finlay.geomonsters.creatures;

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
	
	public Creature(Resources res, String name) {
		_name = name;
		init(res);		
	}
	
	public ArrayList<String> getAttackList() {
		return _attacks;
	}
	
	public String getName() {
		return _name;
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
	public void render(Canvas c, Paint p, Matrix m) {
		c.drawBitmap(_image, m, p);
	}
	
	

	private void init(Resources res) {
		_attacks = new ArrayList<String>();
		
		try {
			final String KEY_CREATURE = "creature";
			final String KEY_NAME = "name";
			final String KEY_ATTACK = "attack";
			final String KEY_IMAGE = "image";
			
			XMLParser parser = new XMLParser();
			InputStream resStream = res.openRawResource(R.raw.creatures);
			Document doc = parser.getDomElement(resStream);
			
			NodeList creatures = doc.getElementsByTagName(KEY_CREATURE);
			
			// cycle through creatures
			for (int i=0; i < creatures.getLength(); i++) {
				
				Element creature = (Element) creatures.item(i);
				
				// is this the creature?
				if (creature.getAttribute(KEY_NAME).equals(_name)) {
					
					// load all attacks
					NodeList childs = creature.getElementsByTagName(KEY_ATTACK);
					for (int c = 0; c < childs.getLength(); c++)
						_attacks.add(parser.getElementValue(childs.item(c)));
					
					// image source
					String imageName = parser.getElementValue(creature.getElementsByTagName(KEY_IMAGE).item(0));
					int resID = res.getIdentifier(imageName, "drawable", "com.finlay.geomonsters");
					_image = BitmapFactory.decodeResource(res, resID);
					
					
					return;
				}
			}
			
			resStream.close();
		} catch(IOException e) {
			Log.v(TAG, e.getMessage());
		}
		
	}

}
