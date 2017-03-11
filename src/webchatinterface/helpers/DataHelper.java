package webchatinterface.helpers;

public class DataHelper
{
    public static String formatBytes(long bytes, int decPrecision)
    {
        if(bytes >= 1024*1024*1024)
        {
            if(decPrecision == 0)
                return (bytes / 1024*1024*1024) + "GB";

            double dataRead = Math.round((bytes / (1024.0*1024.0*1024.0))*decPrecision*10.0)/decPrecision/10.0;
            return dataRead + "GB";
        }
        else if(bytes >= 1024*1024)
        {
            if(decPrecision == 0)
                return (bytes / 1024*1024) + "MB";

            double dataRead = Math.round((bytes / (1024.0*1024.0))*decPrecision*10.0)/decPrecision/10.0;
            return dataRead + "MB";
        }
        else if(bytes >= 1024)
        {
            if(decPrecision == 0)
                return (bytes / 1024) + "kB";

            double dataRead = Math.round((bytes / (1024.0))*decPrecision*10.0)/decPrecision/10.0;
            return dataRead + "kB";
        }
        else
        {
            if(decPrecision == 0)
                return bytes + "B";

            double dataRead = Math.round(bytes*decPrecision*10.0)/decPrecision/10.0;
            return dataRead + "B";
        }
    }
}
