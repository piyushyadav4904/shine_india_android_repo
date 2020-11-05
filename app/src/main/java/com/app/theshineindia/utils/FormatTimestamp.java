package com.app.theshineindia.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by Suvo on 26-May-16.
 */
public class FormatTimestamp {
    public static String getPersonName(String firstname, String lastname) {
        String fullname = firstname + " " + lastname;
        return fullname;
    }

    public static String getPostedTime(Context context, String stringTimeStamp) {
        //System.out.println("Inside getPostedTime method, the value of stringTimeStamp is: "+stringTimeStamp);
        if(stringTimeStamp.contains("-")) {
            String finalTimeString;
            String[] dateElement = null;
            String[] timeElement = null;
            String[] arrStringTimeStamp = stringTimeStamp.split(" ");
            dateElement = arrStringTimeStamp[0].split("-");
            try {
                timeElement = arrStringTimeStamp[1].split(":");
            } catch (ArrayIndexOutOfBoundsException ex) {
                //System.out.println("ArrayIndexOutOfBoundException thrown from getPostedTime method, component for time not available");
            }

            String year = dateElement[0];
            String month = dateElement[1];
            String day = dateElement[2];


            String monthInName;
            int intMonth = Integer.parseInt(month);
            //System.out.println("value of intMonth is: " + intMonth);
            switch (intMonth) {
                case 1:
                    monthInName = "January";
                    break;
                case 2:
                    monthInName = "February";
                    break;
                case 3:
                    monthInName = "March";
                    break;
                case 4:
                    monthInName = "April";
                    break;
                case 5:
                    monthInName = "May";
                    break;
                case 6:
                    monthInName = "June";
                    break;
                case 7:
                    monthInName = "July";
                    break;
                case 8:
                    monthInName = "August";
                    break;
                case 9:
                    monthInName = "September";
                    break;
                case 10:
                    monthInName = "October";
                    break;
                case 11:
                    monthInName = "November";
                    break;
                case 12:
                    monthInName = "December";
                    break;
                default:
                    monthInName = "Nomonth";
            }
            if (timeElement != null) {
                String hour = timeElement[0];
                String minute = timeElement[1];
                int intHr = Integer.parseInt(hour);
                if (!DateFormat.is24HourFormat(context)) {
                    if (intHr >= 12) {
                        int ampmHr = intHr - 12;
                        finalTimeString = day + " " + monthInName + ", " + year + " at " + ampmHr + ":" + minute + " pm";


                    } else {
                        finalTimeString = day + " " + monthInName + ", " + year + " at " + intHr + ":" + minute + " am";
                    }

                } else {
                    finalTimeString = day + " " + monthInName + ", " + year + " at " + intHr + ":" + minute;
                }

            } else {
                finalTimeString = day + " " + monthInName + ", " + year;
            }
            return finalTimeString;
        }else{
            return null;
        }

    }

    public static String getLikeAndCommentString(String likes, String comments){
        String likesandcomments=likes+" Likes  "+comments+" Comments";
        return likesandcomments;
    }

    public static String getRequiredTime(Calendar c){
        String timestring;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timestring=sdf.format(c.getTime());
        return timestring;
    }




}
