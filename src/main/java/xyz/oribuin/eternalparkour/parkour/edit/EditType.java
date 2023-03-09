package xyz.oribuin.eternalparkour.parkour.edit;

public enum EditType {

    VIEWING, // Viewing the level
    ADD_CHECKPOINT, // Adding a checkpoint to the level
    ADD_START, // Setting the start region of the level
    ADD_FINISH, // Setting the finish area of the level
    ADD_REGION, // Adding a region to the level

    ;

    public static EditType fromString(String string) {
        for (EditType type : values()) {
            if (type.name().equalsIgnoreCase(string))
                return type;
        }

        return null;
    }

}
