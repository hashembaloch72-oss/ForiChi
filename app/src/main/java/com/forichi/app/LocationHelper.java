package com.forichi.app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationHelper {

    public interface Callback {
        void onLocation(Location location);
        void onError(String message);
    }

    public static void requestLocation(
            Activity activity,
            Callback callback) {

        if (android.os.Build.VERSION.SDK_INT >= 23) {

            if (activity.checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    &&
                    activity.checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                activity.requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        1001
                );

                callback.onError(
                        "برای پیدا کردن موقعیت، اجازه دسترسی لازم است."
                );

                return;
            }
        }

        LocationManager manager =
                (LocationManager) activity.getSystemService(
                        Activity.LOCATION_SERVICE
                );

        if (manager == null) {
            callback.onError("سرویس موقعیت‌یابی در دسترس نیست.");
            return;
        }

        Location last = null;

        try {
            last = manager.getLastKnownLocation(
                    LocationManager.GPS_PROVIDER
            );

            if (last == null) {
                last = manager.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER
                );
            }
        } catch (SecurityException e) {
            callback.onError("دسترسی به موقعیت مجاز نیست.");
            return;
        }

        if (last != null) {
            callback.onLocation(last);
            return;
        }

        LocationListener listener =
                new LocationListener() {

                    @Override
                    public void onLocationChanged(
                            Location location) {

                        callback.onLocation(location);

                        try {
                            manager.removeUpdates(this);
                        } catch (SecurityException ignored) {
                        }
                    }

                    @Override
                    public void onProviderEnabled(
                            String provider) {
                    }

                    @Override
                    public void onProviderDisabled(
                            String provider) {
                    }

                    @Override
                    public void onStatusChanged(
                            String provider,
                            int status,
                            Bundle extras) {
                    }
                };

        try {
            if (manager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER)) {

                manager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        1,
                        listener
                );

            } else if (manager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER)) {

                manager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1000,
                        1,
                        listener
                );

            } else {
                callback.onError(
                        "لطفاً GPS گوشی را روشن کنید."
                );
            }

        } catch (SecurityException e) {
            callback.onError(
                    "دسترسی به موقعیت مجاز نیست."
            );
        }
    }
}
