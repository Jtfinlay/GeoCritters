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
import android.util.Log;

import com.finlay.geomonsters.R;

public class Creature {

	private static final String TAG = "Creature";

	public ArrayList<String> getAttackList(Resources res, String name) {
		ArrayList<String> attacks = new ArrayList<String>();
		
		try {
			final String KEY_CREATURE = "creature";
			final String KEY_NAME = "name";
			final String KEY_ATTACK = "attack";
			
			XMLParser parser = new XMLParser();
			InputStream resStream = res.openRawResource(R.raw.creatures);
			Document doc = parser.getDomElement(resStream);
			
			NodeList creatures = doc.getElementsByTagName(KEY_CREATURE);
			Log.v(TAG, "Creatures: " +creatures.getLength());
			
			// cycle through creatures
			for (int i=0; i < creatures.getLength(); i++) {
				
				Element creature = (Element) creatures.item(i);
				Log.v(TAG, "Name is " + creature.getAttribute(KEY_NAME));
				
				// is this the creature?
				if (creature.getAttribute(KEY_NAME).equals(name)) {
					
					// add all attacks to the list
					NodeList childs = creature.getElementsByTagName(KEY_ATTACK);
					for (int c = 0; c < childs.getLength(); c++)
						attacks.add(parser.getElementValue(childs.item(c)));
					return attacks;
				}
			}
			
			resStream.close();
		} catch(IOException e) {
			Log.v(TAG, e.getMessage());
		}
		
		return attacks;
	}
}
