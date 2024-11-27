package io.xeros.model.world.objects;

import io.xeros.content.instances.InstancedArea;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Position;
import lombok.Getter;
import lombok.Setter;

/**
 * A global object is a visual model that is viewed by all players within a region. This class represents the identification value, x and y position, as well as the height of the
 * object.
 * <p>
 * A key factor is the ticks remaining. The ticksRemaining variable represents how many game ticks this object will remain visible for. If the value is negative the object will
 * remain indefinitly. On the flip side, if the value is positive then every tick the total remaining will reduce by one until it hits zero.
 * 
 * @author Jason MacKeigan
 * @since Dec 17, 2014, 6:18:20 PM
 */
public class GlobalObject {

	@Setter
    private int id;

	@Getter
    private final int x;

	@Getter
    private final int y;

	@Getter
    private final int height;

	@Getter
    private int face;

	@Getter
    private int ticksRemaining;

	@Getter
    private int restoreId;

	@Getter
    private int type;
	
	@Getter
    private InstancedArea instance;

	public GlobalObject(int id, Position position, int face, int type) {
		this.id = id;
		this.x = position.getX();
		this.y = position.getY();
		this.height = position.getHeight();
		this.type = type;
		this.face = face;
	}

	public GlobalObject(int id, int x, int y, int height) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.height = height;
		this.type = 10;
	}

	public GlobalObject(int id, int x, int y, int height, int face) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.height = height;
		this.face = face;
	}

	public GlobalObject(int id, int x, int y, int height, int face, int type) {
		this(id, x, y, height, face);
		this.type = type;
	}

	public GlobalObject(int id, int x, int y, int height, int face, int type, int ticksRemaining) {
		this(id, x, y, height, face, type);
		this.ticksRemaining = ticksRemaining;
	}

	public GlobalObject(int id, int x, int y, int height, int face, int type, int ticksRemaining, int restoreId) {
		this(id, x, y, height, face, type, ticksRemaining);
		this.restoreId = restoreId;
	}

	public GlobalObject withPosition(Position position) {
		return new GlobalObject(id, position.getX(), position.getY(), position.getHeight(), face, type, ticksRemaining, restoreId);
	}

	public GlobalObject withId(int objectId) {
		return new GlobalObject(objectId, x, y, height, face, type, ticksRemaining, restoreId);
	}

	public boolean samePositionAndType(WorldObject object) {
		return object.x == x && object.y == y && object.height == height && object.type == type;
	}

	public GlobalObject withHeight(int heightLevel) {
		return new GlobalObject(id, x, y, heightLevel, face, type, ticksRemaining, restoreId);
	}

	public void removeTick() {
		this.ticksRemaining--;
	}

	public int getObjectId() {
		return id;
	}

    public Position getPosition() {
		return new Position(x, y, height);
	}

    public GlobalObject setInstance(InstancedArea instance) {
		this.instance = instance;
		return this;
	}
	
	public RegionProvider getRegionProvider() {
		if (instance != null) {
			return instance;
		} else {
			return RegionProvider.getGlobal();
		}
	}

	@Override
	public String toString() {
		return "GlobalObject{" +
				"id=" + id +
				", x=" + x +
				", y=" + y +
				", height=" + height +
				", face=" + face +
				", ticksRemaining=" + ticksRemaining +
				", restoreId=" + restoreId +
				", type=" + type +
				", instance=" + instance +
				'}';
	}
}
