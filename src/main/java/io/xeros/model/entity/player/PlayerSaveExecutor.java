package io.xeros.model.entity.player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.xeros.model.entity.player.save.PlayerSave;
import lombok.Getter;

public class PlayerSaveExecutor {

    private static final ExecutorService executor = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder().setNameFormat("player-save-%d").build());
    @Getter
    private final Player player;
    private Future<?> saveFuture;

    public PlayerSaveExecutor(Player player) {
        this.player = player;
    }

    public void request() {
        Preconditions.checkState(saveFuture == null, "Already requested.");
        saveFuture = executor.submit(() -> {
            if (player.isOnline()) {
                PlayerSave.saveGameInstant(player);
            }
        });
    }

    public boolean finished() {
        return saveFuture != null && saveFuture.isDone();
    }

}
