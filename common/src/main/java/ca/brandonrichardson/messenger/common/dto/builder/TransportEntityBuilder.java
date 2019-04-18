package ca.brandonrichardson.messenger.common.dto.builder;

import ca.brandonrichardson.messenger.common.dto.Authentication;
import ca.brandonrichardson.messenger.common.dto.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class TransportEntityBuilder {

    private TransportEntityBuilder() {
        throw new IllegalStateException("Instantiation of TransportEntityBuilder disallowed.");
    }

    public static AuthenticationEntityBuilder auth() {
        Authentication auth = new Authentication();
        auth.setTimestamp(TransportEntityBuilder.generateUTCTimestamp());

        return new AuthenticationEntityBuilder(auth);
    }

    public static MessageEntityBuilder message() {
        Message message = new Message();
        message.setTimestamp(TransportEntityBuilder.generateUTCTimestamp());

        return new MessageEntityBuilder(message);
    }

    private static String generateUTCTimestamp() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);

        TimeZone tz = TimeZone.getDefault();
        int UTC_Offset = tz.getOffset(cal.getTimeInMillis());
        if(UTC_Offset / 1000 / 60 / 60 >= 0) {
            timestamp += "+";
        } else {
            timestamp += "-";
        }

        if(Math.abs(UTC_Offset / 1000 / 60 / 60) < 10) {
            timestamp += "0";
        }

        timestamp += Math.abs(UTC_Offset / 1000 / 60 / 60) + ":";
        timestamp += UTC_Offset % (1000*60*60) + "0";

        return timestamp;
    }
}
