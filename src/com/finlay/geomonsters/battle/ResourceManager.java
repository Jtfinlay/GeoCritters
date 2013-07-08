package com.finlay.geomonsters.battle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.XMLParser;

public class ResourceManager {

	private static final String TAG = "ResourceManager";


	public static Creature newCreature(Resources res, String name) {

		Creature result = null;

		final String KEY_CREATURE = "creature";
		final String KEY_NAME = "name";
		final String KEY_ATTACK = "attack";
		final String KEY_IMAGE = "image";
		final String KEY_TYPE = "type";
		final String KEY_SPEED = "speed";

		XMLParser parser = new XMLParser();
		InputStream resStream = res.openRawResource(R.raw.creatures);
		Document doc = parser.getDomElement(resStream);

		NodeList creatures = doc.getElementsByTagName(KEY_CREATURE);

		// cycle through creatures
		for (int i=0; i < creatures.getLength(); i++) {

			Element creature = (Element) creatures.item(i);

			// is this the creature?
			if (creature.getAttribute(KEY_NAME).equals(name)) {

				// load all attacks
				ArrayList<String> attacks = new ArrayList<String>();
				NodeList childs = creature.getElementsByTagName(KEY_ATTACK);
				for (int c = 0; c < childs.getLength(); c++)
					attacks.add(parser.getElementValue(childs.item(c)));

				// image source
				String imageName = creature.getAttribute(KEY_IMAGE);
				int resID = res.getIdentifier(imageName, "drawable", "com.finlay.geomonsters");
				Bitmap image = BitmapFactory.decodeResource(res, resID);

				// type
				String type = creature.getAttribute(KEY_TYPE);

				// speed
				int speed = Integer.parseInt(creature.getAttribute(KEY_SPEED));

				result = new Creature(name, image, type, speed, attacks);
				break;
			}
		}
		try {
			resStream.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}


		return result;

	}

	public static Attack getAttack(Resources res, 
			String attack_name,
			Creature attacker,
			Creature defender) {

		Attack result = null;

		try {
			final String KEY		= "attack";
			final String KEY_NAME	= "name";
			final String KEY_TYPE	= "type";
			final String KEY_ANIME	= "animation";

			XMLParser parser = new XMLParser();
			InputStream resStream = res.openRawResource(R.raw.attacks);
			Document doc = parser.getDomElement(resStream);

			NodeList attacks = doc.getElementsByTagName(KEY);

			// cycle through creatures
			for (int i=0; i < attacks.getLength(); i++) {

				Element attack = (Element) attacks.item(i);

				// is this the attack?
				if (attack.getAttribute(KEY_NAME).equals(attack_name)) {

					// get attributes
					String type 	= attack.getAttribute(KEY_TYPE);
					int animation 	= Integer.parseInt(attack.getAttribute(KEY_ANIME));
					int rating = ResourceManager.getTypeRating(res, type, defender.getType());
					result = new Attack(attack_name, type, animation, rating, attacker, defender);
					break;
				}
			}

			resStream.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}


		return result;
	}

	public static int getColorOfAttack(Resources res, String attack_name) {

		int result = 0;

		final String KEY 		= "attack";
		final String KEY_NAME 	= "name";
		final String KEY_TYPE 	= "type";

		XMLParser parser = new XMLParser();
		InputStream resStream = res.openRawResource(R.raw.attacks);
		Document doc = parser.getDomElement(resStream);

		NodeList attacks = doc.getElementsByTagName(KEY);

		// cycle through attacks
		for (int i=0; i < attacks.getLength(); i++) {

			Element attack = (Element) attacks.item(i);

			// is this the attack?
			if (attack.getAttribute(KEY_NAME).equals(attack_name)) {

				String type = attack.getAttribute(KEY_TYPE);

				result =  getColorOfType(res, type);
				break;
			}
		}
		try {
			resStream.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return result;
	}

	public static int getColorOfType(Resources res, String type_name) {

		int result = 0;

		final String KEY 		= "element";
		final String KEY_NAME 	= "name";
		final String KEY_COLOR 	= "color";

		XMLParser parser = new XMLParser();
		InputStream resStream = res.openRawResource(R.raw.type);
		Document doc = parser.getDomElement(resStream);

		NodeList types = doc.getElementsByTagName(KEY);

		// cycle through attacks
		for (int i=0; i < types.getLength(); i++) {

			Element type = (Element) types.item(i);

			// is this the attack?
			if (type.getAttribute(KEY_NAME).equals(type_name)) {

				String color = type.getAttribute(KEY_COLOR);

				result = Color.parseColor(color);
				break;
			}
		}
		try {
			resStream.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return result;
	}

	public static int getTypeRating(Resources res, String aggroName, String defendName) {

		int result = 0;

		final String KEY 		= "element";
		final String KEY_NAME 	= "name";

		XMLParser parser = new XMLParser();
		InputStream resStream = res.openRawResource(R.raw.type);
		Document doc = parser.getDomElement(resStream);

		NodeList types = doc.getElementsByTagName(KEY);

		// cycle through attacks
		for (int i=0; i < types.getLength(); i++) {

			Element type = (Element) types.item(i);

			// is this the attack?
			if (type.getAttribute(KEY_NAME).equals(aggroName)) {

				result = Integer.parseInt(type.getAttribute(defendName));
				break;
			}
		}
		try {
			resStream.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return result;
	}

	public static ArrayList<String> getUserCreatures(Resources res) {

		ArrayList<String> result = new ArrayList<String>();

		try {
			final String KEY		= "creature";
			final String KEY_NAME	= "name";

			XMLParser parser = new XMLParser();
			InputStream resStream = res.openRawResource(R.raw.user_creatures);
			Document doc = parser.getDomElement(resStream);

			NodeList creatures = doc.getElementsByTagName(KEY);

			// cycle through creatures
			for (int i=0; i < creatures.getLength(); i++) {

				Element creature = (Element) creatures.item(i);
				result.add(creature.getAttribute(KEY_NAME));
			}

			resStream.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}


		return result;
	}

}
