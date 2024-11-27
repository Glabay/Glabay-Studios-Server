package dev.glabay.handler.impl;

import dev.glabay.handler.FormModalAction;
import dev.glabay.rest.DiscordClient;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-26
 */
public class RegistrationForm extends DiscordClient implements FormModalAction {
    @Override
    public void handleFormSubmission(ModalInteractionEvent event) {
        // Grab the info from the forum
        var fields = event.getInteraction().getValues();
        var playerName = fields.get(0).getAsString();
        var playerEmail = fields.get(1).getAsString();
        var playerPassword = fields.get(2).getAsString();

        // create the DTO to register the user with the system
    }
}
