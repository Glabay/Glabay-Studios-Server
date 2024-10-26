package io.xeros.model.tickable;

import lombok.Getter;

public class TickableContainer<T> {

    @Getter
    private boolean stopped;
    private final Tickable<T> tickable;
    @Getter
    private int ticks;

    public TickableContainer(Tickable<T> tickable) {
        this.tickable = tickable;
    }

    /**
     * Tick the container {@link Tickable}
     * @param t
     * @return <code>true</code> if still running
     */
    public boolean tick(T t) {
        if (isStopped()) return false;
        else {
            tickable.tick(this, t);
            ticks++;
            return !isStopped();
        }
    }

    public void stop() {
        stopped = true;
    }
}
