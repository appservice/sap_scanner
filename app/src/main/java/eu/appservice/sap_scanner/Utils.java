package eu.appservice.sap_scanner;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.ClipboardManager;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lukasz on 2014-04-12.
 * ﹕ SAP Scanner
 */
public class Utils {

    private Utils(){}  //private Constructor avoid to initialized object Utils

//--------------------------------------------------------------------------------------------------
    /**
     *
     * @param num
     * @return if double is an integer will be showed like integer else like double (fe. 0.0->0 , 2.0 ->2, 2.3->2.3)
     */
    public static String parse(double num) {
        if((int) num == num) return Integer.toString((int) num); //for you, StackOverflowException
        return String.valueOf(num); //and for you
    }

//--------------------------------------------------------------------------------------------------
    /**
     *
     * @param date date as a String
     * @param dateFormatIn  String with date format in  fe. "E dd.MM.yy ' godz: 'HH.mm.ss"
     * @param dateFormatOut String with date format out fe. "dd.MM.yy'T'HH.mm.ss"
     * @return date as a String with new format
     */
    public static String reformatDate(String date,String dateFormatIn,String dateFormatOut){
        SimpleDateFormat simpleDateFormatIn = new SimpleDateFormat(dateFormatIn);//fe. "E dd.MM.yy ' godz: 'HH.mm.ss"
        SimpleDateFormat simpleDateFormatOut = new SimpleDateFormat(dateFormatOut);//fe. "dd.MM.yy'T'HH.mm.ss"
        try {
            Date encodedDate= simpleDateFormatIn.parse(date);
            String dateOut=simpleDateFormatOut.format(encodedDate);
            return dateOut;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


//--------------------------------------------------------------------------------------------------
    /**
     *  This should be call when date format in is like "yyyy-MM-dd HH:mm:ss")
     * @param date
     * @param dateFormatOut String with date format fe. "dd.MM.yy'T'HH.mm.ss"
     * @return date as a String with new format
     */
    public static String reformatDate(String date,String dateFormatOut){
       return reformatDate(date,"yyyy-MM-dd HH:mm:ss",dateFormatOut);
    }


//--------------------------------------------------------------------------------------------------
    public static String nowDate(){
        java.util.Calendar now = java.util.GregorianCalendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(now.getTime());
    }




//--------------------------------------------------------------------------------------------------
    /**
     *
     * @param date date as a String
     * @param dateFormatIn String with date format fe. "dd.MM.yy'T'HH.mm.ss"
     * @return date as a String with new format
     */
    public static Date encodeDate(String date,String dateFormatIn){
        SimpleDateFormat simpleDateFormatIn = new SimpleDateFormat(dateFormatIn); //fe. "dd.MM.yy'T'HH.mm.ss"
        try {
            Date encodedDate= simpleDateFormatIn.parse(date);
           // String dateOut=simpleDateFormatOut.format(encodedDate);
            return encodedDate;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

//--------------------------------------------------------------------------------------------------
    /**
     *  This should be call when date format in is like "yyyy-MM-dd HH:mm:ss")
     * @param date date as a String
     * @return date as a Java.util.Date
     */
    public static Date encodeDate(String date){
       return encodeDate(date,"yyyy-MM-dd HH:mm:ss" );
    }

//--------------------------------------------------------------------------------------------------
    /**
     * Checks if external storage is available for read and write
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


//--------------------------------------------------------------------------------------------------

    /**
     * This function is formatting the size of file (B, kB MB GB TB)
     * @param size-long number with size of file (Bytes)
     * @return string with size
     */
    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * This function is copy the data to clipboard
     */
//  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void copyTextToClipboard(Context context,String label, String text){



        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            @SuppressWarnings("deprecation")
            ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(label, text);

                clipboard.setPrimaryClip(clip);
            }
        }


    }
}
