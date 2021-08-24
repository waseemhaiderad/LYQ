package lnq.com.lnq.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lnq.com.lnq.common.StringMethods;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsData;
import lnq.com.lnq.model.gson_converter_models.chat.GetChatData;
import lnq.com.lnq.model.gson_converter_models.conversation.GetChatThread;

public class SortingUtils {

    public static void sortTagsList(List<String> tagsList) {
        Collections.sort(tagsList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int compare = o1.replace("#", "").trim().compareTo(o2.replace("#", "").trim());
                return compare;
            }
        });
    }

    public static void sortContactsByString(List<UserConnectionsData> userConnectionsDataList, String type) {
        if (type.equals("alphabet")) {
            try {
                Collections.sort(userConnectionsDataList, new Comparator<UserConnectionsData>() {
                    @Override
                    public int compare(UserConnectionsData o1, UserConnectionsData o2) {
                        try {
                            if (!StringMethods.getAlphabetsArray().contains(o1.getUser_fname().substring(0, 1)))
                                return Integer.MAX_VALUE;
                            else if (!StringMethods.getAlphabetsArray().contains(o2.getUser_fname().substring(0, 1)))
                                return Integer.MIN_VALUE;
                            else
                                return o1.getUser_fname().compareTo(o2.getUser_fname());
                        } catch (Exception e) {
                            return 0;
                        }
                    }
                });
            } catch (Exception e) {

            }
        } else if (type.equals("recentViewed")) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss a");
            try {
                Collections.sort(userConnectionsDataList, new Comparator<UserConnectionsData>() {
                    @Override
                    public int compare(UserConnectionsData o1, UserConnectionsData o2) {
                        try {
                            if (o1.getRecent_viewed().isEmpty()) return Integer.MAX_VALUE;
                            else if (o2.getRecent_viewed().isEmpty()) return Integer.MIN_VALUE;
                            else
                                return simpleDateFormat.parse(o2.getRecent_viewed()).compareTo(simpleDateFormat.parse(o1.getRecent_viewed()));
                        } catch (Exception e) {
                            return 0;
                        }
                    }
                });
            } catch (Exception e) {

            }
        } else if (type.equals("recentLNQ")) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss a");
            try {
                Collections.sort(userConnectionsDataList, new Comparator<UserConnectionsData>() {
                    @Override
                    public int compare(UserConnectionsData o1, UserConnectionsData o2) {
                        try {
                            if (o1.getConnection_date().isEmpty()) return Integer.MAX_VALUE;
                            else if (o2.getConnection_date().isEmpty()) return Integer.MIN_VALUE;
                            else
                                return simpleDateFormat.parse(o2.getConnection_date()).compareTo(simpleDateFormat.parse(o1.getConnection_date()));
                        } catch (Exception e) {
                            return 0;
                        }
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    public static void sortChatByDate(List<GetChatThread> chatThreadList) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss a");
        try {
            Collections.sort(chatThreadList, new Comparator<GetChatThread>() {
                @Override
                public int compare(GetChatThread o1, GetChatThread o2) {
                    try {
                        return simpleDateFormat.parse(o2.getLastMessageTime()).compareTo(simpleDateFormat.parse(o1.getLastMessageTime()));
                    } catch (Exception e) {
                        return 0;
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    public static void sortChatDataByDate(List<GetChatData> chatThreadList) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss a");
        try {
            Collections.sort(chatThreadList, new Comparator<GetChatData>() {
                @Override
                public int compare(GetChatData o1, GetChatData o2) {
                    try {
                        return simpleDateFormat.parse(o1.getmessage_time()).compareTo(simpleDateFormat.parse(o2.getmessage_time()));
                    } catch (Exception e) {
                        Log.e("SortingError", "compare: Error sort" + e.getMessage());
                        return 1;
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    public static void sortContactsByDouble(List<UserConnectionsData> userConnectionsDataList) {
        try {
            Collections.sort(userConnectionsDataList, new Comparator<UserConnectionsData>() {
                @Override
                public int compare(UserConnectionsData o1, UserConnectionsData o2) {
                    if (o1.getUser_distance().isEmpty()) return Integer.MAX_VALUE;
                    else if (o2.getUser_distance().isEmpty()) return Integer.MIN_VALUE;
                    else
                        return Double.compare(Double.parseDouble(o1.getUser_distance()), Double.parseDouble(o2.getUser_distance()));
                }
            });
        } catch (Exception e) {

        }
    }

    public static String formateDate(String date){
        if(date.contains("AM")){
            date =date.replace("AM","am");
        }
        if(date.contains("Am")){
            date =date.replace("Am","am");
        }
        if(date.contains("PM")){
            date =date.replace("PM","pm");
        }
        if(date.contains("Pm")){
            date =date.replace("Pm","pm");
        }
        return date;
    }
}