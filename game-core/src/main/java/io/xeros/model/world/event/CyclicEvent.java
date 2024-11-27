package io.xeros.model.world.event;

import io.xeros.Server;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

// TODO remove
public class CyclicEvent {

    private final ReentrantLock lock;
    private Optional<Callable<CyclicEventResult>> onCycle;
    private Optional<Runnable> onFinish;
    private Optional<Runnable> onStart;

    public CyclicEvent() {
        this.onCycle = Optional.empty();
        this.onFinish = Optional.empty();
        this.onStart = Optional.empty();
        this.lock = new ReentrantLock();
    }

    public CyclicEvent onCycle(Callable<CyclicEventResult> onCycle) {
        this.onCycle = Optional.ofNullable(onCycle);
        return this;
    }

    public CyclicEvent onFinish(Runnable onFinish) {
        this.onFinish = Optional.ofNullable(onFinish);
        return this;
    }

    public CyclicEvent onStart(Runnable onStart) {
        this.onStart = Optional.ofNullable(onStart);
        return this;
    }

    public void wake() {
        synchronized (lock) {
            lock.notify();
        }
    }

    public void begin() {
        if (onCycle.isEmpty())
            return;
        Server.getWorld().getCyclicEventManager().register(this);
        new Thread(() -> {

            boolean exitCycle = false;
            while (!exitCycle) {
                if (onStart.isPresent()) {
                    onStart.get().run();
                    onStart = Optional.empty();
                }
                Optional<CyclicEventResult> result;
                try {
                    result = Optional.ofNullable(onCycle.get().call());

                }
                catch (Exception e) {
                    e.printStackTrace(System.err);
                    result = Optional.of(CyclicEventResult.END);
                }

                if (result.isPresent()) {
                    if (result.get() == CyclicEventResult.END) {
                        this.onFinish.ifPresent(Runnable::run);
                        exitCycle = true;
                    }
                    else if (result.get() == CyclicEventResult.END_NO_FINISH) {
                        exitCycle = true;
                    }
                }
                synchronized (lock) {
                    try {
                        lock.wait(600);
                    }
                    catch (InterruptedException ignored) {}

                }
            }

            Server.getWorld().getCyclicEventManager().unregister(this);
        }, "raids-lobby").start();
    }

    public void destroy() {
        Callable<CyclicEventResult> destroyEvent = () -> CyclicEventResult.END_NO_FINISH;
        onCycle = Optional.of(destroyEvent);
    }
}
