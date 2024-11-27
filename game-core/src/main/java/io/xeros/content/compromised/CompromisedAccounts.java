package io.xeros.content.compromised;

import com.fasterxml.jackson.databind.JsonNode;
import io.xeros.Server;
import io.xeros.annotate.Init;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.model.entity.player.Player;
import io.xeros.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CompromisedAccounts {

    private static final Logger logger = LoggerFactory.getLogger(CompromisedAccounts.class);

    private static final Map<String, CompromisedAccountType> map = new HashMap<>();

    @Init
    public static void init() {
        try {
            if (!Server.isPublic())
                return;
            File file = new File(Server.getSaveDirectory() + "Xeros_Compromised_Accounts.json");
            if (!file.exists()) {
                System.err.println(file + " does not exist!");
                return;
            }

            JsonNode jsonNode = JsonUtil.fromJacksonJson(file);
            jsonNode.elements().forEachRemaining(node -> {
                String status = node.get("Status").toPrettyString().toLowerCase();
                if (!status.equals("\"match\"") && !status.equals("\"multiple matches\""))
                    return;

                String username = node.get("Username").toPrettyString().replace("\"", "").toLowerCase();
                boolean emailCompromised = node.get("Additional").toString().equalsIgnoreCase("\"Email +\"");
                map.put(username, emailCompromised ? CompromisedAccountType.PASSWORD_AND_EMAIL : CompromisedAccountType.PASSWORD);
                logger.debug("Added compromised user={}, email={}", username, emailCompromised);
            });
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public static void onLogin(Player player) {
        if (CompromisedPlayerSave.hasChangedPassword(player))
            return;
        if (message(player))
            return;
        CompromisedAccountType type1 = map.get(player.getLoginNameLower());
        CompromisedAccountType type2 = map.get(player.getDisplayName().toLowerCase());
        CompromisedAccountType nonNullType = type1 != null ? type1 : type2;
        if (nonNullType != null) {
            CompromisedPlayerSave.setCompromised(player, nonNullType);
            message(player);
        }
    }

    private static boolean message(Player player) {
        CompromisedAccountType type = CompromisedPlayerSave.getCompromisedType(player);
        if (type != null) {
            String leaked = type == CompromisedAccountType.PASSWORD_AND_EMAIL ? "password and email password" : "password";
            String[] message = {
                    "Although Xeros has not been compromised, it has been determined",
                    "from an outside source that your " + leaked + " was",
                    " likely compromised through leaks from other RSPS.",
                    "@dre@Change your password on any effected accounts and set a ::pin!",
                    "You will receive this message until you change your password."
            };

            new DialogueBuilder(player).statement(message).send();
            Arrays.stream(message).forEach(player::sendMessage);
            return true;
        }

        return false;
    }
}
