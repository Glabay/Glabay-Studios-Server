package io.xeros.content.preset;

import java.util.ArrayList;

/**
 * 
 * @author Grant_ | www.rune-server.ee/members/grant_ | 9/30/19
 * An abstraction of set. Thus allowing sets to mimic this base set.
 */
public abstract class Set {

	private String name;
	private PresetEquipment equipment;
	private ArrayList<PresetItem> inventory;
	private int ammoAmount;
	private int spellBook;
	
	Set(PresetEquipment equipment, ArrayList<PresetItem> inventory, int ammoAmount, String name, int spellBook) {
		this.setEquipment(equipment);
		this.setInventory(inventory);
		this.setAmmoAmount(ammoAmount);
		this.setName(name);
		this.setSpellBook(spellBook);
	}

	public PresetEquipment getEquipment() {
		return equipment;
	}

	public void setEquipment(PresetEquipment equipment) {
		this.equipment = equipment;
	}

	public ArrayList<PresetItem> getInventory() {
		return inventory;
	}

	public void setInventory(ArrayList<PresetItem> inventory) {
		this.inventory = inventory;
	}

	public int getAmmoAmount() {
		return ammoAmount;
	}

	public void setAmmoAmount(int ammoAmount) {
		this.ammoAmount = ammoAmount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSpellBook() {
		return spellBook;
	}

	public void setSpellBook(int spellBook) {
		this.spellBook = spellBook;
	}
}
