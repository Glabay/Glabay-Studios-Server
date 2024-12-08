package dev.glabay.rsps.plugin.world.npc.inter;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-09-17
 */
public interface NpcActionI {

    default Boolean isEnabled() {
        return true;
    }
}
