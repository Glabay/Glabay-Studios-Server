package io.xeros.content.preset;

import java.util.ArrayList;

/**
 * 
 * @author Grant_ | www.rune-server.ee/members/grant_ | 9/30/19
 *
 */
public class Preset extends Set {

	Preset(PresetEquipment equipment, ArrayList<PresetItem> inventory, int ammoAmount, String name, int spellBook) {
		super(equipment, inventory, ammoAmount, name, spellBook);
	}

}
