package com.forichi.app;

import android.content.Context;
import android.content.SharedPreferences;

public class OrderStore {

    private static final String PREF = "forichi_orders";

    public static void saveOrder(Context context,
                                 String service,
                                 String specialist,
                                 String price) {

        SharedPreferences sp =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        int count = sp.getInt("count", 0);

        sp.edit()
                .putInt("count", count + 1)
                .putString("service_" + count, service)
                .putString("specialist_" + count, specialist)
                .putString("price_" + count, price)
                .putString("status_" + count, "در انتظار بررسی")
                .apply();
    }

    public static int getCount(Context context) {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getInt("count", 0);
    }

    public static String getService(Context context, int index) {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getString("service_" + index, "");
    }

    public static String getSpecialist(Context context, int index) {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getString("specialist_" + index, "");
    }

    public static String getPrice(Context context, int index) {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getString("price_" + index, "");
    }

    public static String getStatus(Context context, int index) {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getString("status_" + index, "");
    }
}
