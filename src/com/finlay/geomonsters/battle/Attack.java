package com.finlay.geomonsters.battle;

import android.content.res.Resources;
import android.util.Log;

public class Attack {

	private static final String TAG = "Attack";

	private String 	_name;
	private String 	_type;
	private int 	_animation;
	private int		_rating;
	
	private Creature _attacker;
	private Creature _defender;

	private boolean _criticalHit; //TODO: Not completely ignore this.
	private int _damageDealt;

	public Attack(String name, String type, int animation, int rating, 
					Creature attacker, Creature defender) {
		_attacker = attacker;
		_defender = defender;
		_name = name;
		_type = type;
		_animation = animation;
		_rating = rating;
		
		CalculateDamage();
	}
	public void CalculateDamage() {

		// TODO: Calculate base damage
		int damage = 10;

		//TODO: Crit stuff here

		// multiplier for effectiveness
		switch (_rating) {
		case 0:
			damage = 0;
			break;
		case 1:
			damage /= 2;
			break;
		case 3:
			damage *= 2; 
			break;
		default:
			break;
		}

		_damageDealt = damage;

	}

	public String getName() {
		return _name;
	}
	public String getType() {
		return _type;
	}
	public int getAnimationType() {
		return _animation;
	}
	public int getRating() {
		return _rating;
	}
	public int getDamageDealt() {
		return _damageDealt;
	}
	public String getEffectiveMessage() {
		switch (_rating) {
		case 0:
			return "It has no effect!";
		case 1:
			return "It is not very effective...";
		case 3:
			return "It's super effective!";
		default:
			return "";
		}
	}

}
