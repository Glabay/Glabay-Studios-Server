package io.xeros.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.xeros.content.skills.Skill;
import lombok.Getter;

public record SkillLevel(Skill skill, int level) {
    public static String getLevelsAsString(SkillLevel... skillArray) {
        var string = new StringBuilder();
        for (int i = 0; i < skillArray.length; i++) {
            string.append(skillArray[i].getFormattedString());
            if (i != skillArray.length - 1)
                string.append(", ");
        }
        return string.toString();
    }

    /**
     * Gets the SkillLevel as a string (i.e. 99 attack, 99 strength);
     *
     * @return the SkillLevel as a string
     */
    @JsonIgnore
    public String getFormattedString() {
        return level + " " + skill.toString();
    }

}
