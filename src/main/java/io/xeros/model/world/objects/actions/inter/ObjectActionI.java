package io.xeros.model.world.objects.actions.inter;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-09-17
 */
public interface ObjectActionI {

    default Boolean isEnabled() {
        return true;
    }
}
