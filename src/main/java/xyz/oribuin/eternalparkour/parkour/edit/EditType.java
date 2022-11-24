package xyz.oribuin.eternalparkour.parkour.edit;

public enum EditType {

    // Checkpoint types
    CHANGE_CHECKPOINTS,

    // Spawn Area
    SET_SPAWN,

    // Set Start Region
    SET_START,
    REMOVE_START,

    // Finish Area
    SET_FINISH,
    REMOVE_FINISH,

    // Add regions to the level
    ADD_REGION,
    REMOVE_REGION,

    ;

    public static EditType fromString(String string) {
        for (EditType type : values()) {
            if (type.name().equalsIgnoreCase(string))
                return type;
        }

        return null;
    }

}
