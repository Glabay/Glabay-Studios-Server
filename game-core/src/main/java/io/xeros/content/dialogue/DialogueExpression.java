package io.xeros.content.dialogue;


import io.xeros.model.Animation;

public enum DialogueExpression {
	HAPPY(588),
	SPEAKING_CALMLY(589),
	CALM_TALK(590),
	DEFAULT(591),
	EVIL(592),
	EVIL_2(593),
	DELIGHTED_EVIL(594),
	ANNOYED(595),
	DISTRESSED(596),
	DISTRESSED_2(597),
	ALMOST_CRYING(598),
	BOWS_WHILE_SAD(598),
	DRINK_LEFT(600),
	DRUNK_RIGHT(601),
	DISINTERESTED(602),
	SLEEPY(603),
	PLAIN_EVIL(604),
	LAUGH_1(605),
	LAUGH_2(606),
	LAUGH_3(607),
	LAUGH_4(608),
	EVIL_LAUGHH(609),
	SAD(610),
	MORE_SAD(611),
	ON_ONE_HAND(612),
	NEARLY_CRYING(613),
	ANGER_1(614),
	ANGER_2(615),
	ANGER_3(616),
	ANGER_4(617),
	CALM(591), 
	;

	private int id;

	DialogueExpression(int id) {
		this.id = id;
	}

	public Animation getAnimation() {
		return new Animation(id);
	}
	
	/**
	 * @return the anim
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the anim to set
	 */
	public void setId(int id) {
		this.id = id;
	}

}
