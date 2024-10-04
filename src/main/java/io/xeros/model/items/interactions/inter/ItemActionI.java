package io.xeros.model.items.interactions.inter;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-04
 */
public interface ItemActionI {

    default Boolean isEnabled() {
        return true;
    }
}
