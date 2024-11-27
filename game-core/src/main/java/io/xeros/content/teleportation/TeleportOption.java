package io.xeros.content.teleportation;

public class TeleportOption {

	private final String name;
	private final int x;
    private final int y;
    private final int z;
	
	public TeleportOption(String name, int x, int y, int z) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String getName() {
		return name;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
}