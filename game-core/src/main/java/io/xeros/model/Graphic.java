package io.xeros.model;

import lombok.Getter;

@Getter
public class Graphic {

	public enum GraphicHeight {
		LOW,
		MIDDLE,
		HIGH
	}

	/**
	 * The graphic's id.
     * -- GETTER --
     *  Gets the graphic's id.
     *
     * @return	id.

     */
	private final int id;

	/**
	 * The delay which the graphic must wait before being performed.
     * -- GETTER --
     *  Gets the graphic's wait delay.
     *
     * @return	delay.

     */
	private final int delay;

	/**
	 * The graphic's height level to display in.
	 */
	private final int height;

	/**
	 * The animation priority.
	 */
	private final AnimationPriority animationPriority;

	public Graphic(int id) {
		this(id, 0, GraphicHeight.LOW, AnimationPriority.LOW);
	}

	public Graphic(int id, int delay) {
		this(id, delay, GraphicHeight.LOW, AnimationPriority.LOW);
	}

	public Graphic(int id, GraphicHeight graphicHeight) {
		this(id, 0, graphicHeight, AnimationPriority.LOW);
	}

	public Graphic(int id, int delay, GraphicHeight graphicHeight) {
		this(id, delay, graphicHeight, AnimationPriority.LOW);
	}

	/**
	 * Legacy way of producing the height value.
	 */
	public Graphic(int id, int delay, int height) {
		this.id = id;
		this.delay = delay;
		this.height = 65536 * height;
		this.animationPriority = AnimationPriority.LOW;
	}

	public Graphic(int id, int delay, GraphicHeight height, AnimationPriority animationPriority) {
		this.id = id;
		this.delay = delay;
		this.height = (height.ordinal() * 100) << 16;
		this.animationPriority = animationPriority;
	}

}