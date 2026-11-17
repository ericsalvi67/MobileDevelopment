package com.salvi.TaskManagER.Support;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import java.util.Locale;

public final class Location {

    public interface Callback {
        void onLocationResult(String latitude, String longitude);
    }

    private Location() {
    }

    public static boolean hasPermission(Context context) {
        return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            || context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void getCurrentLocation(Context context, Callback callback) {
        LocationManager manager =
            (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean finePermission =
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        String provider = finePermission
            ? LocationManager.GPS_PROVIDER
            : LocationManager.NETWORK_PROVIDER;

        try {
            if (!manager.isProviderEnabled(provider)) {
                provider = finePermission
                    ? LocationManager.NETWORK_PROVIDER
                    : LocationManager.GPS_PROVIDER;
            }
            if (!manager.isProviderEnabled(provider)) {
                callback.onLocationResult(null, null);
                return;
            }

            manager.getCurrentLocation(
                provider,
                null,
                context.getMainExecutor(),
                location -> {
                    if (location == null) {
                        callback.onLocationResult(null, null);
                        return;
                    }

                    callback.onLocationResult(
                        String.format(Locale.US, "%.4f", location.getLatitude()),
                        String.format(Locale.US, "%.4f", location.getLongitude())
                    );
                }
            );
        } catch (SecurityException | IllegalArgumentException exception) {
            callback.onLocationResult(null, null);
        }
    }
}
