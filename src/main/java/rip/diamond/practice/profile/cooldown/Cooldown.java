package rip.diamond.practice.profile.cooldown;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;


@Getter
@Setter
public class Cooldown {

    private static DecimalFormat SECONDS_FORMAT = new DecimalFormat("#0.0");

    private long start;
    private long expire;

    public Cooldown(int seconds) {
        long duration = 1000L * seconds;
        this.start = System.currentTimeMillis();
        this.expire = this.start + duration;
    }

    public Cooldown(long milliSeconds) {
        this.start = System.currentTimeMillis();
        this.expire = this.start + milliSeconds;
    }

    private static String formatSeconds(long time) {
        return SECONDS_FORMAT.format(time / 1000.0F);
    }

    public long getPassed() {
        return System.currentTimeMillis() - this.start;
    }

    public long getRemaining() {
        return this.expire - System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= this.expire;
    }

    public int getSecondsLeft() {
        return (int) getRemaining() / 1000;
    }

    public String getMilliSecondsLeft(boolean allowNegative) {
        if (!allowNegative && getRemaining() < 0) {
            return "0.0";
        }
        return formatSeconds(this.getRemaining());
    }

    public void cancelCountdown() {
        this.expire = 0;
    }

    public void run() {
        if (isExpired()) {
            cancelCountdown();
            runExpired();
        } else {
            runUnexpired();
        }
    }

    public void runUnexpired() {

    }

    public void runExpired() {

    }
}

