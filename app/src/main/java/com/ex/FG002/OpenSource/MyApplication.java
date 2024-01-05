package com.ex.FG002.OpenSource;

import android.app.Application;

public class MyApplication extends Application {
    public static String OwnerId;
    public static String OwnerPassword;

    public String getOwnerId() {
        return OwnerId;
    }
    public String getOwnerPaswword() {return OwnerPassword;}
}
