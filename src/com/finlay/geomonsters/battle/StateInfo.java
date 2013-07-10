package com.finlay.geomonsters.battle;


/** This class helps hold information for the GAME STATES. Many functions appear to be repeated, but are for different data setups **/
public class StateInfo {

	private static final String TAG = "StateInfo";

	private String _name;
	private Creature _creature1;
	private Creature _creature2;

	public String getName() {
		return _name;
	}

	/** ATTACK STATE METHODS **/

	private String _attackType;
	private int _animation;
	private int _rating;
	private boolean _critAmt; // TODO: Not completely ignore crits
	private int _damageDealt;

	public void setAsAttack(String attack_name, String attack_type, int animation,
			int rating, Creature attacker, Creature defender) {
		_creature1 = attacker;
		_creature2 = defender;
		_name =	attack_name;
		_attackType = attack_type;
		_animation = animation;
		_rating = rating;

		CalculateDamage();
	}
	public void CalculateDamage() {

		// TODO: Calculate base damage form creature info
		int damage = 10;

		//TODO: Crit stuff here

		// effectiveness multiplier
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
	public Creature getAttacker() {
		return _creature1;
	}
	public Creature getDefender() {
		return _creature2;
	}
	public String getType() {
		return _attackType;
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
		switch(_rating) {
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


	/** CHANGE CREATURE METHOD **/
	
	public void setAsCreatureSwitch(Creature outgoing, Creature incoming) {
		_creature1 = outgoing;
		_creature2 = incoming;
	}
	public Creature getOutgoing() {
		return _creature1;
	}
	public Creature getIncoming() {
		return _creature2;
	}
	public void setIncomingAsOutgoing() {
		_creature1.setAs(_creature2);
	}

}

