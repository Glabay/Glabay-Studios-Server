package io.xeros.model.entity.player;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RightGroup {

	private final Player player;
	@Getter
    private Right primary;
	private final Set<Right> rights;

	public RightGroup(Player player, Right primary) {
		this.player = player;
		this.primary = primary;
		this.rights = new LinkedHashSet<>();
		rights.add(primary);
	}

	public RightGroup(Player player, List<Right> rights) {
		this.player = player;
		this.primary = findHighestPriority(rights);
		this.rights = new LinkedHashSet<>(rights);
	}

	public RightGroup(Player player, Right primary, List<Right> rights) {
		this.player = player;
		this.primary = primary;
		this.rights = new LinkedHashSet<>(rights);
		this.rights.add(primary);
	}

	public boolean hasStaffPosition() {
		return rights.stream().anyMatch(Right::isStaff);
	}

	public boolean ignoreNewPlayerRestriction() {
		return hasStaffPosition() || contains(Right.YOUTUBER);
	}

	public String buildCrownString() {
		StringBuilder builder = new StringBuilder();
		getDisplayedRights().forEach(right -> {
			builder.append("<img=");
			builder.append(right.getValue() - 1);
			builder.append(">");
		});
		return builder.toString();
	}

	public List<Right> getDisplayedRights() {
		List<Right> rights = new ArrayList<>();
		Set<Right> set = getSet();

		for (Right right : set) {
			if (Right.DISPLAY_GROUPS[0].contains(right)) {
				rights.add(right);
				break; // Only displaying one crown from this group!
			}
		}

		for (Right right : set) {
			if (Right.DISPLAY_GROUPS[1].contains(right)) {
				if (rights.size() < 2) {
					rights.add(right);
				}
			}
		}

		return rights;
	}

	public String getCrowns() {
		StringBuilder builder = new StringBuilder();
		for (Right right : getDisplayedRights()) {
			if (right.getValue() > 0) {
				builder.append("<img=").append(right.getValue() - 1).append("> ");
			}
		}
		return builder.toString();
	}

	public void add(Right toAdd) {
		if (rights.stream().anyMatch(r -> r.isOrInherits(toAdd))) {
			return;
		}
		Right prevPrimary = primary;
		Set<Right> remove = rights.stream().filter(toAdd::isOrInherits).collect(Collectors.toSet());
		remove.forEach(this::remove);
		if (prevPrimary != primary) {
			updatePrimary();
		}
		rights.add(toAdd);
	}

	public void reset() {
		rights.clear();
		rights.add(Right.PLAYER);
	}

	public void remove(Right toRemove) {
		if (toRemove == Right.PLAYER) {
			return;
		}
		rights.remove(toRemove);
		if (toRemove == primary) {
			updatePrimary();
		}
	}

	public void removeIfInherits(Right toRemove) {
		rights.stream().filter(r -> r.isOrInherits(toRemove)).forEach(this::remove);
	}

    public void setPrimary(Right primary) {
		this.primary = primary;
		rights.add(primary);
		player.getPA().requestUpdates();
	}

	public void updatePrimary() {
		primary = findHighestPriority(rights);
		player.getPA().requestUpdates();
	}

	public boolean contains(Right right) {
		return rights.contains(right);
	}

	public boolean isNotAdmin() {
		return !isOrInherits(Right.ADMINISTRATOR);
	}

	public boolean isIronman() {
		return Right.IRONMAN_SET.stream().anyMatch(rights::contains);
	}

	public boolean isOrInherits(Right right) {
		return rights.stream().anyMatch(r -> r.isOrInherits(right));
	}

	public boolean isOrInherits(Right... rights) {
		return Arrays.stream(rights).anyMatch(this::isOrInherits);
	}

	public boolean isNot(Right...rights) {
		return !isOrInherits(rights);
	}

	private Right findHighestPriority(Collection<Right> rights) {
		if (rights.size() <= 0) {
			return Right.PLAYER;
		} else if (rights.size() == 1) {
			return rights.iterator().next();
		} else {
			return rights.stream().min(RightComparator.RIGHT_COMPARATOR).get();
		}
	}

	public Set<Right> getSet() {
		return rights;
	}
}
