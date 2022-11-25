package xyz.oribuin.eternalparkour.util;

import java.util.List;

/**
 * This is used to store all the times a player has completed a level in a list.
 * We're turning this into a json to stop excessive database table columns.
 */
public class TimesCompleted {

    private List<Long> times;

    public TimesCompleted(List<Long> times) {
        this.times = times;
    }

    public List<Long> getTimes() {
        return times;
    }

    public void setTimes(List<Long> times) {
        this.times = times;
    }

}
