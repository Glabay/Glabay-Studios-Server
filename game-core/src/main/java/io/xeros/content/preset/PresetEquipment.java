package io.xeros.content.preset;

/**
 * 
 * @author Grant_ | www.rune-server.ee/members/grant_ | 9/30/19
 *
 */
public class PresetEquipment {

	private int weapon;
	private int helmet;
	private int shield;
	private int ammo;
	private int plate;
	private int legs;
	private int boots;
	private int gloves;
	private int necklace;
	private int cape;
	private int ring;
	
	public PresetEquipment(int weapon, int helmet, int shield, int ammo, int plate, int legs, int boots, int gloves, int necklace, int cape, int ring) {
		this.setWeapon(weapon);
		this.setHelmet(helmet);
		this.setShield(shield);
		this.setAmmo(ammo);
		this.setPlate(plate);
		this.setLegs(legs);
		this.setBoots(boots);
		this.setGloves(gloves);
		this.setNecklace(necklace);
		this.setCape(cape);
		this.setRing(ring);
	}

	public int getWeapon() {
		return weapon;
	}

	public void setWeapon(int weapon) {
		this.weapon = weapon;
	}

	public int getHelmet() {
		return helmet;
	}

	public void setHelmet(int helmet) {
		this.helmet = helmet;
	}

	public int getShield() {
		return shield;
	}

	public void setShield(int shield) {
		this.shield = shield;
	}

	public int getAmmo() {
		return ammo;
	}

	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}

	public int getPlate() {
		return plate;
	}

	public void setPlate(int plate) {
		this.plate = plate;
	}

	public int getLegs() {
		return legs;
	}

	public void setLegs(int legs) {
		this.legs = legs;
	}

	public int getBoots() {
		return boots;
	}

	public void setBoots(int boots) {
		this.boots = boots;
	}

	public int getGloves() {
		return gloves;
	}

	public void setGloves(int gloves) {
		this.gloves = gloves;
	}

	public int getNecklace() {
		return necklace;
	}

	public void setNecklace(int necklace) {
		this.necklace = necklace;
	}

	public int getCape() {
		return cape;
	}

	public void setCape(int cape) {
		this.cape = cape;
	}

	public int getRing() {
		return ring;
	}

	public void setRing(int ring) {
		this.ring = ring;
	}

	public boolean isEmpty() {
		return (getRing() == -1 && getCape() == -1 && getWeapon() == -1 && getNecklace() == -1 && getShield() == -1
				&& getGloves() == -1 && getBoots() == -1 && getAmmo() == -1 && getHelmet() == -1 && getLegs() == -1 && getPlate() == -1);
	}
}
