package io.xeros.content.skills.fletching;

import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Player;

public class MakeDartEvent extends Event<Player> {

    private FletchableDart d = null;

    public MakeDartEvent(Player att, FletchableDart a) {
        super("skilling", att, 2);
        this.d = a;
    }
    @Override
    public void execute() {
        if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
            stop();
            return;
        }
        if (d == null) {
            stop();
            return;
        }
        if (!attachment.getItems().playerHasItem(314, 10)) {
            attachment.sendMessage("You need at least 10 feathers to do this.");
            attachment.getPA().removeAllWindows();
            stop();
            return;
        }
        if (!attachment.getItems().playerHasItem(d.getId(), 10)) {
            attachment.sendMessage("You need at least 10 dart tips to do this.");
            attachment.getPA().removeAllWindows();
            stop();
            return;
        }
        if (attachment.getItems().freeSlots() < 1) {
            attachment.sendMessage("You need at least 1 free slot to do this.");
            attachment.getPA().removeAllWindows();
            stop();
            return;
        }
        attachment.getItems().deleteItem2(314, 10);
        attachment.getItems().deleteItem2(d.getId(), 10);
        attachment.getItems().addItem(d.getReward(), 10);
        attachment.getPA().addSkillXPMultiplied((int) (10 * d.getExperience()), Skill.FLETCHING.getId(), true);
    }
}
