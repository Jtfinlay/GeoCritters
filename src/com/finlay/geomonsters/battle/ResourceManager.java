package com.finlay.geomonsters.battle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.Weather;
import com.finlay.geomonsters.XMLParser;


public class ResourceManager {

	private static final String TAG = "ResourceManager";

	// return new Creature instance from name
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

	// return new Attack instance
	public static StateInfo getAttack(Resources res, 
			String attack_name,
			Creature attacker,
			Creature defender) {

		StateInfo result = null;

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
					result = new StateInfo();
					result.setAsAttack(attack_name, type, animation, rating, attacker, defender);
					break;
				}
			}

			resStream.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}


		return result;
	}

	// gets the colour attribute of given attack
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

	// gets the colour attribute of given type
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

	// checks how well aggroType can hit defendType
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

	// Load list of creatures from user_creatures
	public static ArrayList<Creature> getUserCreatures(Resources res, int limit) {

		ArrayList<Creature> result = new ArrayList<Creature>();

		try {
			final String KEY			= "creature";
			final String KEY_NAME		= "name";
			final String KEY_NICKNAME	= "nickname";

			XMLParser parser = new XMLParser();
			InputStream resStream = res.openRawResource(R.raw.user_creatures);
			Document doc = parser.getDomElement(resStream);

			NodeList creatures = doc.getElementsByTagName(KEY);

			// cycle through creatures
			for (int i=0; i < creatures.getLength(); i++) {

				Element creature = (Element) creatures.item(i);
				String name = creature.getAttribute(KEY_NAME);
				String nickName = creature.getAttribute(KEY_NICKNAME);

				Creature theCreature = newCreature(res, name);
				theCreature.setNickName(nickName);

				result.add(theCreature);

				// Check limit
				if (limit > 0)
					if (result.size() >= limit)
						return result;
			}

			resStream.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return result;
	}

	// Get creature from user_creatires from given unique nickname
	public static Creature getUserCreatureByNickName(Resources res, String nickname) {

		try {
			final String KEY			= "creature";
			final String KEY_NAME		= "name";
			final String KEY_NICKNAME	= "nickname";

			XMLParser parser = new XMLParser();
			InputStream resStream = res.openRawResource(R.raw.user_creatures);
			Document doc = parser.getDomElement(resStream);

			NodeList creatures = doc.getElementsByTagName(KEY);

			// cycle through creatures
			for (int i=0; i < creatures.getLength(); i++) {

				Element creature = (Element) creatures.item(i);

				if (creature.getAttribute(KEY_NICKNAME).equals(nickname)) {
					String name = creature.getAttribute(KEY_NAME);
					Creature theCreature =  newCreature(res, name);
					theCreature.setNickName(nickname);
					return theCreature;
				}
			}

			resStream.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return null;
	}





	// Get creature encounter. TODO: Return more than just critter name
	public static String getCreatureEncounter(Resources res, String serverReturn, Weather weatherData) {

		try {
			final String KEY		= "creature";
			final String NAME		= "name";
			final String ENCOUNTER	= "encounter";
			final String WEIGHT		= "weight";
			final String LOCATION	= "location";
			final String CLOUDS		= "clouds";
			final String WEATHER 	= "weather";

			XMLParser parser = new XMLParser();
			InputStream resStream = res.openRawResource(R.raw.creatures);
			Document doc = parser.getDomElement(resStream);

			// Hold creatureNames and weights. Creature names should be unique.
			Dictionary<String, Integer> possibles = new Hashtable<String, Integer>();

			// cycle through creatures
			NodeList creatures = doc.getElementsByTagName(KEY);
			for (int i=0; i < creatures.getLength(); i++) {

				Element creature = (Element) creatures.item(i);

				// cycle through encounters
				NodeList encounters = creature.getElementsByTagName(ENCOUNTER);
				for (int j=0; j < encounters.getLength(); j++) {

					Element encounter = (Element) encounters.item(j);
					String location = encounter.getAttribute(LOCATION);
					String clouds = encounter.getAttribute(CLOUDS);
					String weather = encounter.getAttribute(WEATHER);
					int weight = Integer.parseInt(encounter.getAttribute(WEIGHT));

					// only add if encounter meets requirements
					if (!location.equals(serverReturn) && !location.equals(""))
						continue;
					if (!clouds.equals("") && !clouds.equals(weatherData.getCloudCover()))
						continue;
					if (!weather.equals("") && !weather.equals(weatherData.getWeatherType()))
						continue;
					
					possibles.put(creature.getAttribute(NAME), weight);
					Log.v(TAG, creature.getAttribute(NAME) + " is option, with weight: " + weight);
				}

			}
			
			// Get Total weight of all possibilities
			int weight_sum = 1;
			Enumeration<Integer> weight = possibles.elements();
			while (weight.hasMoreElements())
				weight_sum += weight.nextElement();
			// Generate number inside total weight
			int choice = (int)(Math.random() * (weight_sum));

			// Figure out which creature was decided.
			Enumeration<String> enumElement = possibles.keys();
			while (enumElement.hasMoreElements()) {
				String name = enumElement.nextElement();
				choice -= possibles.get(name);
				
				if (choice <= 0) return name;
			}
			
			resStream.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return null;
	}

}
