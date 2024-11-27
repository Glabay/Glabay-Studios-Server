package io.xeros.content.miniquests.magearenaii;

public enum Capes {

    ZAMORAK(2414, 21795),
    GUTHIX(2413, 21793),
    SARADOMIN(2412, 21791);

    public int capeId, imbueCapeId;

    Capes(int capeId, int imbueCapeId) {
        this.capeId = capeId;
        this.imbueCapeId = imbueCapeId;
    }
    ;
}
