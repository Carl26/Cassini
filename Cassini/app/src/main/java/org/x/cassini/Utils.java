//package org.x.cassini;
//
///**
// * Created by shuangyang on 12/8/17.
// */
//
//import android.app.Activity;
//import android.content.Intent;
//
//public class Utils {
//    private static int sTheme;
//    public final static int THEME_Mono = 0;
//    public final static int THEME_rThin = 1;
////    public final static int THEME_BLUE = 2;
//    /**
//     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
//     */
//    public static void changeToTheme(Activity activity, int theme)
//    {
//        sTheme = theme;
//        activity.finish();
//        activity.startActivity(new Intent(activity, activity.getClass()));
//    }
//    /** Set the theme of the activity, according to the configuration. */
//
//    public static void onActivityCreateSetTheme(Activity activity)
//    {
//        switch (sTheme)
//        {
//            default:
//            case THEME_Mono:
//                activity.setTheme(R.style.MonoTheme);
//                break;
//            case THEME_rThin:
//                activity.setTheme(R.style.rThinTheme);
//                break;
//        }
//    }
//}
