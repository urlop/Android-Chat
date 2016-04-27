package com.github.nkzawa.socketio.androidchat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rubymobile on 27/04/16.
 */
public class UtilsMethods {

    public static Date parseDateFromString(String stringDate){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
            Date date=sdf.parse(stringDate);

            return date;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }
}
