package com.agent.musical;

import java.util.Locale;

public class TimeHelpers {

    public static String getDurationAsText(long duration, Locale curLocale) {
        //convert the song duration into string reading hours, mins seconds
        long hrs = (duration / 3600000);
        long mns = (duration / 60000) % 60000;
        long scs = (duration % 60000) / 1000;

        if(hrs == 0)
            return String.format(curLocale, "%02d:%02d", mns, scs);
        else
            return String.format(curLocale,"%02d:%02d:%02d", hrs,  mns, scs);
    }
}
