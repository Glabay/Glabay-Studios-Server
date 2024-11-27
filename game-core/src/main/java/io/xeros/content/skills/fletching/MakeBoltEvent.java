package io.xeros.content.skills.fletching;

import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Player;

public class MakeBoltEvent extends Event<Player> {

    private FletchableBolt b = null;
    private int boltId, tipId;

    public MakeBoltEvent(Player att, FletchableBolt a, int bolt, int tip) {
        super("skilling", att, 2);
        this.b = a;
        this.boltId = bolt;
        this.tipId = tip;
    }
    @Override
    public void execute() {
        if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
            stop();
            return;
        }
        if (b == null) {
            stop();
            return;
        }
        if (!attachment.getItems().playerHasItem(tipId, 15)) {
            attachment.sendMessage("You need at least 15 tips to do this.");
            attachment.getPA().removeAllWindows();
            stop();
            return;
        }
        if (!attachment.getItems().playerHasItem(boltId, 15)) {
            attachment.sendMessage("You need at least 15 bolts to do this.");
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
        attachment.getItems().deleteItem2(boltId, 15);
        attachment.getItems().deleteItem2(tipId, 15);
        attachment.getItems().addItem(b.getBolt(), 15);
        attachment.getPA().addSkillXPMultiplied(b.getExperience(), Skill.FLETCHING.getId(), true);
    }
}
