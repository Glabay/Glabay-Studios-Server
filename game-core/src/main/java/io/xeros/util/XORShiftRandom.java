package io.xeros.util;

/**
 * @author Arthur Behesnilian 5:41 PM
 */
public class XORShiftRandom {

    public static XORShiftRandom rand = new XORShiftRandom();

    private long last;
    private long inc;

    public XORShiftRandom() {
        this(System.currentTimeMillis());
    }

    public XORShiftRandom(long seed) {
        this.last = seed | 1;
        inc = seed;
    }

    public int nextInt(int max) {
        if (max == 0) return 0;
        this.last = System.currentTimeMillis();
        last ^= (last << 21);
        last ^= (last >>> 35);
        last ^= (last << 4);
        inc += 123456789123456789L;
        int out = (int) ((last+inc) % max);
        return (out < 0) ? -out : out;
    }

}
