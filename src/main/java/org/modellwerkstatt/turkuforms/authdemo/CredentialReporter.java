package org.modellwerkstatt.turkuforms.authdemo;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

public class CredentialReporter {
    private final static int CHECK_INTERVAL_MIN = 1440;
    private final static int TWO_WEEKS = 20160;
    private static DateTime lastCheck;


     public static boolean checkExpirationDateOnceInWindow(DateTime now, LocalDate target){

         if  (lastCheck == null) {
             lastCheck = now.minusMinutes(CHECK_INTERVAL_MIN + 1);
         }

         Duration diff = new Duration(lastCheck, now);
         if (diff.getStandardMinutes() >= CHECK_INTERVAL_MIN) {
             lastCheck = now;

             Duration toTarget = new Duration(now, target.toDateTimeAtStartOfDay());
             if (toTarget.getStandardMinutes() < TWO_WEEKS || now.isAfter(target.toDateTimeAtStartOfDay())) {
                return true;
             }

         }

         return false;

     }
}
