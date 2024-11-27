package io.xeros.model.entity.player;

import java.util.Comparator;

import org.apache.commons.lang3.ArrayUtils;

public class RightComparator implements Comparator<Right> {

	public static final RightComparator RIGHT_COMPARATOR = new RightComparator();

	@Override
	public int compare(Right o1, Right o2) {
		final int o1Index = ArrayUtils.indexOf(Right.PRIORITY, o1);
		final int o2Index = ArrayUtils.indexOf(Right.PRIORITY, o2);
		if (o1Index < o2Index) {
			return 1;
		} else if (o1Index > o2Index) {
			return -1;
		}
		return 0;
	}

}
