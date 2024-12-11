package org.mapcomplete;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.location.LocationManagerCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

public class GeolocationBridge {

    public static final int requestCode = 684198;

    private final Context context;
    private final MainActivity mainActivity;

    GeolocationBridge(Context context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;

        Databridge.addResponder("location:watch", pluginCall -> {
            pluginCall.setKeepAlive(true);
            new LocationUpdateListener(pluginCall, context).requestLocationUpdates(true);
        });
    }


}

class LocationUpdateListener implements LocationListener {

    private final PluginCall callback;
    private final Context context;

    public LocationUpdateListener(PluginCall callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    private void answer(String answer) {
        Databridge.sendAnswerTo(this.callback, answer);
    }

    private void error(String msg) {
        this.callback.reject(msg);
    }

    /**
     * Query the location manager to indicate what providers are available.
     * Typical providers are `passive`, `network`, `fused` and `gps`
     * <p>
     * This method selects the best appropriate
     */
    private String getPreferredProvider(boolean enableHighAccuracy) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        var providers = lm.getProviders(true);
        if (enableHighAccuracy) {
            if (providers.contains("gps")) {
                return "gps";
            }
            if (providers.contains("fused")) {
                return "fused";
            }
            if (providers.contains("network")) {
                return "network";
            }
        } else {
            if (providers.contains("network")) {
                return "network";
            }
            if (providers.contains("fused")) {
                return "fused";
            }
            if (providers.contains("gps")) {
                return "gps";
            }
        }
        if (providers.contains("passive")) {
            return "passive";
        }

        return null;
    }


    private Boolean isLocationServicesEnabled() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(lm);
    }


    @SuppressWarnings("MissingPermission")
    public void requestLocationUpdates(boolean enableHighAccuracy) {

        if (!this.isLocationServicesEnabled()) {
            this.error("location disabled");
            return;
        }

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        var provider = getPreferredProvider(enableHighAccuracy);
        if (provider == null) {
            this.error("Location unavailable: no providers defined. Note: this is a Google Play Services free implementation");
            return;
        }
        lm.requestLocationUpdates(provider, 1000, 10, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        JSObject loc = new JSObject();
        loc.put("latitude", location.getLatitude());
        loc.put("longitude", location.getLongitude());
        if (location.hasAccuracy()) {
            loc.put("accuracy", location.getAccuracy());
        }
        if (location.hasAltitude()) {
            loc.put("altitude", location.getAltitude());
        }
        if (location.hasBearing()) {
            // Expected for heading: 0 is north, 90 is east, up till 359° ; see https://developer.mozilla.org/en-US/docs/Web/API/GeolocationCoordinates
            // getBearing returns essentially the same
            loc.put("heading", location.getBearing());
        }

        JSObject ret = new JSObject();
        ret.put("value", loc);
        Log.i("databridge", "Resolving call");
        this.callback.resolve(ret);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }




}

