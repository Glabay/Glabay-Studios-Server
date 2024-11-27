package dev.glabay.handler;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-26
 */
@FunctionalInterface
public interface FormModalAction {
    void handleFormSubmission(ModalInteractionEvent event);
}
