package io.xeros.model.entity.player.mode.group.contest;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ContestType {
    COLLECTION_LOG(0, 39_906),
    PETS_IN_BANK(1, 39_910),
    EARNED_EXCHANGE_POINTS(2, 39_914),
    TOB_COMPLETIONS(3, 39_918)
    ;

    private final int intValue;
    private final int listStartInterfaceId;

    ContestType(int intValue, int listStartInterfaceId) {
        this.intValue = intValue;
        this.listStartInterfaceId = listStartInterfaceId;
    }

    public static ContestType forInt(int value) {
        return Arrays.stream(ContestType.values()).filter(it -> it.intValue == value).findFirst().orElse(null);
    }

}
