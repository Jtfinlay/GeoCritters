package com.finlay.geomonsters.battle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.XMLParser;

public class Attack {
	
	private static final String TAG = "Attack";
	
	public String 	_name;
	public String 	_type;
	public int 		_animation;
	
	public Attack(Resources res, String name) {
		_name = name;
		init(res);
	}
	
	private void init(Resources res) {
		
		try {
			final String KEY		= "attack";
			final String KEY_NAME	= "name";
			final String KEY_TYPE 	= "type";
			final String KEY_ANIME 	= "animation";
			
			XMLParser parser = new XMLParser();
			InputStream resStream = res.openRawResource(R.raw.creatures);
			Document doc = parser.getDomElement(resStream);
			
			NodeList attacks = doc.getElementsByTagName(KEY);
			
			// cycle through creatures
			for (int i=0; i < attacks.getLength(); i++) {
				
				Element attack = (Element) attacks.item(i);
				
				// is this the attack?
				if (attack.getAttribute(KEY_NAME).equals(_name)) {
					
					// get attributes
					_type 		= attack.getAttribute(KEY_TYPE);
					_animation 	= Integer.parseInt(attack.getAttribute(KEY_ANIME));

					break;
				}
			}
			
			resStream.close();
		} catch(IOException e) {
			Log.v(TAG, e.getMessage());
		}
		
	}

}
