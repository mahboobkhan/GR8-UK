package com.Gr8niteout.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.util.Locale;
import java.util.TimeZone;

public class DeviceUtils {
    
    /**
     * Get device UDID (Android ID)
     */
    public static String getDeviceUDID(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            Log.e("DeviceUtils", "Error getting device UDID: " + e.getMessage());
            return "unknown_device";
        }
    }
    
    /**
     * Get app version
     */
    public static String getAppVersion(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
            Log.e("DeviceUtils", "Error getting app version: " + e.getMessage());
            return "1.0.0";
        }
    }
    
    /**
     * Get device type (1 for Android)
     */
    public static String getDeviceType() {
        return "1"; // Android
    }
    
    /**
     * Get device timezone
     */
    public static String getDeviceTimezone() {
        try {
            return TimeZone.getDefault().getID();
        } catch (Exception e) {
            Log.e("DeviceUtils", "Error getting timezone: " + e.getMessage());
            return "UTC";
        }
    }
    
    /**
     * Get country name from locale
     */
    public static String getCountryName() {
        try {
            return Locale.getDefault().getCountry();
        } catch (Exception e) {
            Log.e("DeviceUtils", "Error getting country: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Get device model and brand info
     */
    public static String getDeviceInfo() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }
}
