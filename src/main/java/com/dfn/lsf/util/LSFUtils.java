package com.dfn.lsf.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.text.DecimalFormat;
import com.dfn.lsf.model.RemainTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;

import lombok.extern.slf4j.Slf4j;
import com.dfn.lsf.model.GlobalParameters;


@Slf4j
public class LSFUtils {

    private static final DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
    private static Map<String, Long> ipActionMap = Collections.synchronizedMap(new HashMap<String, Long>());

    public static String getCurrentMiliSecondAsString() {
        return Long.toString(System.currentTimeMillis());
    }

    public static RemainTime getRemainTimeForGracePrd(Date inputDate, int gracePeriodMinutes) {
        if (inputDate == null) {
            return new RemainTime(0, 0, 0);
        }
        
        try {
            long currentTimeMillis = System.currentTimeMillis();
            long durationMinutes = TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis - inputDate.getTime());
            int remainingMinutes = gracePeriodMinutes - (int)durationMinutes;
            
            if (remainingMinutes <= 0) {
                return new RemainTime(0, 0, 0);
            }
            
            int days = remainingMinutes / (24 * 60);
            int hours = (remainingMinutes % (24 * 60)) / 60;
            int minutes = remainingMinutes % 60;
            
            return new RemainTime(days, hours, minutes);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating remaining time: " + e.getMessage());
        }
    }

    public static boolean validateAdminApproveAction(String ipAddress){
        
        if(!ipActionMap.containsKey(ipAddress)){ /*---If this is the first time for the given ip allow task---*/
            ipActionMap.put(ipAddress, System.currentTimeMillis());
            return true;
        }else{
            long timeDifference  = System.currentTimeMillis() - ipActionMap.get(ipAddress);
            if(timeDifference > LsfConstants.MINIMUM_TIME_GAP_BETWEEN_ADMIN_APPROVALS){ /*--If the action time difference is > 5s allow the task---*/
                ipActionMap.put(ipAddress, System.currentTimeMillis());
                return true;
            }else{
                return false;
            }
        }
    }
    
    public static double ceilTwoDecimals(double value){
        DecimalFormat f = new DecimalFormat("##.00");
        return Double.parseDouble(f.format(value));


    }

    public static int getDaysTillNowAfterSigned(Date signedDate){
        int dayCount = 0;
        Date date = new Date();
       
        if(date.equals(signedDate)){
            dayCount = 1;
        }else {
            dayCount = (int) ((date.getTime() - signedDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
        }
        return dayCount;

    }

    public static String getConfiguration(String propertyName) {
        // TODO: Implement this method with Spring Boot Config Server
        String jndi = null;
        //InputStream is = null;
        try {
            String sSettingsFile = "/LSF.properties";
            //Properties g_oProperties = new Properties();
           // is = LSFUtils.class.getResourceAsStream(sSettingsFile);
           // g_oProperties.load(is);
           // jndi = String.valueOf(g_oProperties.get(propertyName)).trim();
           return null;

        } catch (Exception e) {
            log.error(e.getMessage());
        } 
        return null;
    }

    public static Date formatStringToDate(String date){
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDateToString(Date date){
        return dateFormat.format(date);
    }

    public static Date dateAdd(Date baseDate,int noOfYears,int noOfMonths,int noOfDays){
        Calendar c = Calendar.getInstance();
        c.setTime(baseDate);

        c.add(Calendar.YEAR,noOfYears);
        c.add(Calendar.MONTH,noOfMonths);
        c.add(Calendar.DATE,noOfDays);

        return c.getTime();
    }

    public static double roundUpDouble(String value){
        double roundedValue = Double.parseDouble(value);
        DecimalFormat format_2Places = new DecimalFormat("0.00");
        roundedValue = Double.valueOf(format_2Places.format(roundedValue));
        return  roundedValue;
    }
    
    public static boolean isMarketOpened(){ /*---Check Whether Current time is in between market open time gap---*/
         boolean response = false;
        try {
            String openTime = GlobalParameters.getInstance().getMarketOpenTime();
            String closedTime = GlobalParameters.getInstance().getMarketClosedTime();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            java.util.Date d1 =(java.util.Date)format.parse(openTime);
            java.util.Date d2 =(java.util.Date)format.parse(closedTime);
            java.sql.Time open = new java.sql.Time(d1.getTime());
            java.sql.Time close = new java.sql.Time(d2.getTime());

            Calendar now = Calendar.getInstance();
            String currentTime = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
            java.util.Date d3 =(java.util.Date)format.parse(currentTime);
            java.sql.Time nw = new java.sql.Time(d3.getTime());

            if(nw.after(open) && nw.before(close) ){
               response = true;
            }
        } catch(Exception e) {
            System.out.println("Exception is " + e.toString());
            response = false;
        }
        return response;
    }

    public static boolean isPurchaseOrderCreationAllowed(boolean bypassUmessage){ /*----PO Submission should be allowed until 1h 15min before market close time---*/
        boolean response = false;
        try {
            String closedTime = GlobalParameters.getInstance().getMarketClosedTime();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            java.util.Date d2 =(java.util.Date)format.parse(closedTime);
            java.sql.Time close = new java.sql.Time(d2.getTime());

            Calendar now = Calendar.getInstance();
            String currentTime = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
            java.util.Date d3 =(java.util.Date)format.parse(currentTime);
            java.sql.Time nw = new java.sql.Time(d3.getTime());
            long timeGap = close.getTime() - nw.getTime();
            if(timeGap > 0 && timeGap > 1.25*LsfConstants.MILISECONDS_TO_HOUR){
                response = true;
            }

        } catch(Exception e) {
            System.out.println("Exception is " + e.toString());
            response = false;
        }
        if (bypassUmessage) {
            return true;
        }
        return response;
    }

    public static int getDaysToSettlement(String settlementDate)
    {
        SimpleDateFormat sm = new SimpleDateFormat("ddMMyyyy");
        Date date = new Date();
        return getDateDiff(settlementDate,sm.format(date));
    }

    public static int getDateDiff(String sdate1,String sdate2){
        DateFormat df = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat sm = new SimpleDateFormat("ddMMyyyy");
        int difference = 0;
        try {
            Date date1 =df.parse(sdate1);
            Date date2 = df.parse(sdate2);
            //Date curDate=df.parse(sm.format(date));
            long duration = date1.getTime() - date2.getTime();
            difference = (int) TimeUnit.MILLISECONDS.toDays(duration);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return difference;
    }

    public static boolean isABeforeSettlementJob(){

        boolean result = true;
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String date = df.format(today);
        String dateTime = date + GlobalParameters.getInstance().getSettlementClTimerString();
        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date1 = null;
        try {
            date1 = df1.parse(dateTime.replaceAll(" ",""));
            result = date1.after(new Date());
        } catch (ParseException e) {
            log.error("Error While Time Check");
        }

        return result;
    }
}
