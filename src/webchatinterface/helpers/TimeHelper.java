package webchatinterface.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeHelper
{
    public static String formatTimestampUTC(Calendar cal)
    {
        TimeZone tz = TimeZone.getDefault();
        int UTC_Offset = tz.getOffset(cal.getTimeInMillis());

        String timestamp = TimeHelper.formatTimestamp(cal, "yyyy-MM-dd'T'HH:mm:ss");

        if(UTC_Offset / 1000 / 60 / 60 >= 0)
            timestamp += "+";
        else
            timestamp += "-";

        if(Math.abs(UTC_Offset /1000/60/60) < 10)
            timestamp += "0";

        timestamp +=Math.abs(UTC_Offset /1000/60/60) + ":";
        timestamp +=UTC_Offset %(1000*60*60) + "0";

		return timestamp;
    }

    public static String formatTimestamp(Calendar cal, String format)
    {
        Date date = cal.getTime();
        return new SimpleDateFormat(format).format(date);
    }
}
