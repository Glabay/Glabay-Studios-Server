package io.xeros.model.entity.healthbar;

import dev.openrune.cache.CacheManager;
import dev.openrune.cache.filestore.definition.data.HealthBarType;
import lombok.Getter;

public abstract class HealthBarUpdate {

    @Getter
    protected final int id;
    protected HealthBarType template;

    public HealthBarUpdate(int id) {
        this.id = id;
        this.template = CacheManager.INSTANCE.getHealthBar(id);
    }

}