package io.xeros.content.preset;

import java.util.ArrayList;

/**
 * 
 * @author Grant_ | www.rune-server.ee/members/grant_ | 9/30/19
 *
 */
public class DefaultPreset extends Set {

	private final PresetRequirements[] requirements;
	private final int cost;

	public DefaultPreset(String name, PresetRequirements[] requirements, int cost, PresetEquipment equipment, int ammoAmount, ArrayList<PresetItem> inventory, int spellBook) {
		super(equipment, inventory, ammoAmount, name, spellBook);
		this.requirements = requirements;
		this.cost = cost;
	}
	
	public PresetRequirements[] getRequirements() {
		return requirements;
	}

	public int getCost() {
		return cost;
	}
}
