package io.github.ztmark.start;

import java.util.Date;

/**
 * Author: Mark
 * Date  : 2017/12/26
 */
public class UnixTime {

    private long time;


    public UnixTime() {
        this(System.currentTimeMillis() / 1000L + 2208988800L);
    }

    public UnixTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return new Date((getTime() - 2208988800L) * 1000L).toString();
    }
}
