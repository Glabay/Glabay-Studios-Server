package io.xeros.content.bosses.nightmare;

/**
 * A nightmare attack.
 */
public abstract class NightmareAttack {

    private boolean stopped;
    private int ticks;

    public abstract void tick(Nightmare nightmare);

    public void ticked() {
        ticks++;
    }

    public void stop() {
        stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }

    public int getTicks() {
        return ticks;
    }

}
