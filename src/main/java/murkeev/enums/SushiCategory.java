package murkeev.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SushiCategory {
    MAKI, NIGIRI, SASHIMI, ROLLS;

    @JsonCreator
    public static SushiCategory fromString(String value) {
        return SushiCategory.valueOf(value.toUpperCase());
    }
}

