package webchatinterface.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DataHelper
{
    public static String formatBytes(long bytes, int decPrecision)
    {
       if(decPrecision < 0)
           decPrecision = 0;

        if(bytes >= 1024*1024*1024)
        {
            String dataRead = new BigDecimal(bytes/1024.0/1024.0/1024.0).setScale(decPrecision, RoundingMode.FLOOR).toString();
            return dataRead + "GB";
        }
        else if(bytes >= 1024*1024)
        {
            String dataRead = new BigDecimal(bytes/1024.0/1024.0).setScale(decPrecision, RoundingMode.FLOOR).toString();
            return dataRead + "MB";
        }
        else if(bytes >= 1024)
        {
            String dataRead = new BigDecimal(bytes/1024.0).setScale(decPrecision, RoundingMode.FLOOR).toString();
            return dataRead + "kB";
        }
        else
        {
            String dataRead = new BigDecimal(bytes).setScale(decPrecision, RoundingMode.FLOOR).toString();
            return dataRead + "B";
        }
    }
}
