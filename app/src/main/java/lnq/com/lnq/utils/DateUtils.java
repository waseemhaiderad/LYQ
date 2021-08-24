package lnq.com.lnq.utils;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import lnq.com.lnq.model.gson_converter_models.activity.ActivityData;

public class DateUtils {

    static String[] suffixes =
            //    0     1     2     3     4     5     6     7     8     9
            {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
                    //    10    11    12    13    14    15    16    17    18    19
                    "th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
                    //    20    21    22    23    24    25    26    27    28    29
                    "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
                    //    30    31
                    "th", "st"};

    public static String getMyPrettyDate(long neededTimeMilis) {
        Calendar nowTime = Calendar.getInstance();
        Calendar neededTime = Calendar.getInstance();
        neededTime.setTimeInMillis(neededTimeMilis);
        String[] days = new String[]{"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        if ((neededTime.get(Calendar.YEAR) == nowTime.get(Calendar.YEAR))) {
            if ((neededTime.get(Calendar.MONTH) == nowTime.get(Calendar.MONTH))) {
                if (neededTime.get(Calendar.DATE) + nowTime.get(Calendar.DATE) == 2) {
                    return "Tomorrow";
                } else if (nowTime.get(Calendar.DATE) == neededTime.get(Calendar.DATE)) {
                    return "Today";
                } else if (nowTime.get(Calendar.DATE) - neededTime.get(Calendar.DATE) == 1) {
                    return "Yesterday";
                } else if ((nowTime.get(Calendar.WEEK_OF_YEAR) == neededTime.get(Calendar.WEEK_OF_YEAR) && nowTime.get(Calendar.YEAR) == neededTime.get(Calendar.YEAR))) {
                    return days[neededTime.get(Calendar.DAY_OF_WEEK)];
                } else {
                    /*SimpleDateFormat format = new SimpleDateFormat("d", Locale.US);
                    String date = format.format(new Date());*/
                    int day = neededTime.get(Calendar.DAY_OF_MONTH);
                    String dayStr = day + suffixes[day];
                    return days[neededTime.get(Calendar.DAY_OF_WEEK)] + ", " + DateFormat.format("MMMM ", neededTime).toString() + dayStr;
                }
            } else {

                int day = neededTime.get(Calendar.DAY_OF_MONTH);
                String dayStr = day + suffixes[day];
                return days[neededTime.get(Calendar.DAY_OF_WEEK)] + ", " + DateFormat.format("MMMM ", neededTime).toString() + dayStr;

                /*SimpleDateFormat format = new SimpleDateFormat("d", Locale.US);
                String date = format.format(new Date());

                if(date.endsWith("1") && !date.endsWith("11"))
                    return days[neededTime.get(Calendar.DAY_OF_WEEK)] + ", " + DateFormat.format("MMMM d'st'", neededTime).toString();
                else if(date.endsWith("2") && !date.endsWith("12"))
                    return days[neededTime.get(Calendar.DAY_OF_WEEK)] + ", " + DateFormat.format("MMMM d'nd'", neededTime).toString();
                else if(date.endsWith("3") && !date.endsWith("13"))
                    return days[neededTime.get(Calendar.DAY_OF_WEEK)] + ", " + DateFormat.format("MMMM d'rd'", neededTime).toString();
                else
                    return days[neededTime.get(Calendar.DAY_OF_WEEK)] + ", " + DateFormat.format("MMMM d'th'", neededTime).toString();
*/
            }
        } else {
            return DateFormat.format("MMMM dd, yyyy", neededTime).toString();
        }
    }


//    public static String getTimeForConversation(long neededTimeMilis) {
//        Calendar nowTime = Calendar.getInstance();
//        Calendar neededTime = Calendar.getInstance();
//        neededTime.setTimeInMillis(neededTimeMilis);
//        String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
//        if ((neededTime.get(Calendar.YEAR) == nowTime.get(Calendar.YEAR))) {
//            if ((neededTime.get(Calendar.MONTH) == nowTime.get(Calendar.MONTH))) {
//                if (nowTime.get(Calendar.DATE) == neededTime.get(Calendar.DATE)) {
//                    return DateFormat.format("hh:mm a", neededTime).toString();
//                } else if (nowTime.get(Calendar.DATE) - neededTime.get(Calendar.DATE) == 1) {
//                    return "Yesterday";
//                } else if ((nowTime.get(Calendar.WEEK_OF_YEAR) == neededTime.get(Calendar.WEEK_OF_YEAR) && nowTime.get(Calendar.YEAR) == neededTime.get(Calendar.YEAR))) {
//                    return days[neededTime.get(Calendar.DAY_OF_WEEK)];
//                } else {
//                    return days[neededTime.get(Calendar.DAY_OF_WEEK)] + ", " + DateFormat.format("MMMM dd", neededTime).toString();
//                }
//            } else {
//                return DateFormat.format("M/dd/yy", neededTime).toString();
//            }
//        } else {
//            return DateFormat.format("MMMM dd, yyyy", neededTime).toString();
//        }
//    }

    public static String getTimeForConversation(long neededTimeMilis) {
        Calendar nowTime = Calendar.getInstance();
        Calendar neededTime = Calendar.getInstance();
        neededTime.setTimeInMillis(neededTimeMilis);
        String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        if ((neededTime.get(Calendar.YEAR) == nowTime.get(Calendar.YEAR))) {
            if ((neededTime.get(Calendar.MONTH) == nowTime.get(Calendar.MONTH))) {
                if (nowTime.get(Calendar.DATE) == neededTime.get(Calendar.DATE)) {
                    return DateFormat.format("hh:mm a", neededTime).toString();
                } else if (nowTime.get(Calendar.DATE) - neededTime.get(Calendar.DATE) == 1) {
                    return "Yesterday";
                } else if ((nowTime.get(Calendar.WEEK_OF_YEAR) == neededTime.get(Calendar.WEEK_OF_YEAR) && nowTime.get(Calendar.YEAR) == neededTime.get(Calendar.YEAR))) {
                    return days[neededTime.get(Calendar.DAY_OF_WEEK)];
                } else {
                    if (TimeZone.getDefault().getDisplayName().equals(Locale.US.getDisplayName())) {
                        return DateFormat.format("MM/dd/yyyy", neededTime).toString();
                    } else {
                        return DateFormat.format("dd/MM/yyyy", neededTime).toString();
                    }
                }
            } else {
                if (TimeZone.getDefault().getDisplayName().equals(Locale.US.getDisplayName())) {
                    return DateFormat.format("MM/dd/yyyy", neededTime).toString();
                } else {
                    return DateFormat.format("dd/MM/yyyy", neededTime).toString();
                }
            }
        } else {
            if (TimeZone.getDefault().getDisplayName().equals(Locale.US.getDisplayName())) {
                return DateFormat.format("MM/dd/yyyy", neededTime).toString();
            } else {
                return DateFormat.format("dd/MM/yyyy", neededTime).toString();
            }
        }
    }

    public static String getTimeForChatHeader(long neededTimeMilis) {
        Calendar nowTime = Calendar.getInstance();
        Calendar neededTime = Calendar.getInstance();
        neededTime.setTimeInMillis(neededTimeMilis);
        String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        if ((neededTime.get(Calendar.YEAR) == nowTime.get(Calendar.YEAR))) {
            if ((neededTime.get(Calendar.MONTH) == nowTime.get(Calendar.MONTH))) {
                if (nowTime.get(Calendar.DATE) == neededTime.get(Calendar.DATE)) {
                    return "Today, " + DateFormat.format("hh:mm a", neededTime).toString();
                } else if (nowTime.get(Calendar.DATE) - neededTime.get(Calendar.DATE) == 1) {
                    return "Yesterday";
                } else if ((nowTime.get(Calendar.WEEK_OF_YEAR) == neededTime.get(Calendar.WEEK_OF_YEAR) && nowTime.get(Calendar.YEAR) == neededTime.get(Calendar.YEAR))) {
                    return days[neededTime.get(Calendar.DAY_OF_WEEK)];
                } else {
                    if (TimeZone.getDefault().getDisplayName().equals(Locale.US.getDisplayName())) {
                        return DateFormat.format("MM/dd/yyyy HH:mm a", neededTime).toString();
                    } else {
                        return DateFormat.format("dd/MM/yyyy HH:mm a", neededTime).toString();
                    }
                }
            } else {
                if (TimeZone.getDefault().getDisplayName().equals(Locale.US.getDisplayName())) {
                    return DateFormat.format("MM/dd/yyyy HH:mm a", neededTime).toString();
                } else {
                    return DateFormat.format("dd/MM/yyyy HH:mm a", neededTime).toString();
                }
            }
        } else {
            if (TimeZone.getDefault().getDisplayName().equals(Locale.US.getDisplayName())) {
                return DateFormat.format("MM/dd/yyyy HH:mm a", neededTime).toString();
            } else {
                return DateFormat.format("dd/MM/yyyy HH:mm a", neededTime).toString();
            }
        }
    }

    public static String getDate(String ourDate) {
        String newDate = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date value = formatter.parse(ourDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            ourDate = dateFormatter.format(value);

            newDate = getTimeForChatHeader(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").parse(ourDate).getTime());
        } catch (Exception e) {
            newDate = "00-00-0000 00:00";
        }
        return newDate;
    }

    public static String getDateForConversation(String ourDate) {
        String ourNewDateDate = "";
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss a");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
            ourDate = simpleDateFormat.format(formatter.parse(ourDate));
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date value = simpleDateFormat.parse(ourDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            ourNewDateDate = dateFormatter.format(value);

            ourNewDateDate = getTimeForConversation(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").parse(ourNewDateDate).getTime());

            return ourNewDateDate;
        } catch (Exception e) {
            ourNewDateDate = "00-00-0000 00:00";
        }
        return ourNewDateDate;
    }

    public static String getLocalConvertedTime(String time) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date value = formatter.parse(time);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm a"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            time = dateFormatter.format(value);

            if (time.startsWith("0")) {
                time = time.substring(1);
            }

            //Log.d("ourDate", ourDate);
        } catch (Exception e) {
            time = "00-00-0000 00:00";
        }
        return time;
    }

    public static String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(new Date());
    }

    public static String getDateForContactNote() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        return simpleDateFormat.format(new Date());
    }
}
