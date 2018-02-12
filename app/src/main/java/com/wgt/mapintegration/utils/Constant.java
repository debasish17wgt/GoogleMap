package com.wgt.mapintegration.utils;

/**
 * Created by debasish on 09-02-2018.
 */

public class Constant {
    public interface ACTION {
        String ACTION_START_LOCATION_SERVICE = "com.wgt.mapintegration.action.START_LOCATION_SERVICE";
        String ACTION_STOP_LOCATION_SERVICE = "com.wgt.mapintegration.action.STOP_LOCATION_SERVICE";
    }

    public interface NOTIFICATION_ID {
        int LOCATION_SERVICE_NOTIFICATION_ID = 100;
    }

    public interface PREFERENCE {
        String PREF_USER_FILE = "user_profile";
        String PREF_USER_NAME = "user_name";
        String PREF_USER_EMAIL = "user_email";
        String PREF_USER_PIC_LOC = "user_pic_loc";
        String PREF_USER_LOGGED_STATUS = "user_logged_status";
    }

    public interface INTENT {
        String INTENT_LOCATION_BROADCAST = "com.wgt.mapintegration.intent.location_broadcast";
        String INTENT_LOCATION_LAT = "com.wgt.mapintegration.intent.location_lat";
        String INTENT_LOCATION_LONG = "com.wgt.mapintegration.intent.location_long";

        String INTENT_LOCATION_SERVICE_STOPPED = "com.wgt.mapintegration.intent.location.stopped";
    }

    public interface SAMPLE {
        String email = "debasish17wgt@gmail.com";
        String name = "Debasish Nandi";
    }
}
