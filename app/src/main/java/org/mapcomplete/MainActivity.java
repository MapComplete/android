package org.mapcomplete;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.BridgeActivity;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;

import java.util.Objects;

public class MainActivity extends BridgeActivity {

    private PluginCall locationRequest = null;
    private PluginCall authRequest = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(Databridge.class);
        new GeolocationBridge(getApplicationContext(), this);
        Databridge.addResponder("location:request-permission", pluginCall -> {
            this.locationRequest = pluginCall;
            this.requestPermission();
        });
        Databridge.addResponder("request:login", pluginCall -> {
            this.authRequest = pluginCall;
        });
        super.onCreate(savedInstanceState);
    }

    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    GeolocationBridge.requestCode
            );

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!Intent.ACTION_VIEW.equals(intent.getAction())) {
            return;
        }
        Uri url = intent.getData();
        if (url == null) {
            return;
        }

        if (Objects.equals(url.getPath(), "/land.html")) {
            var code = url.getQueryParameter("code");
            var state = url.getQueryParameter("state");
            JSObject obj = new JSObject();
            obj.put("code", code);
            obj.put("state", state);
            JSObject res = new JSObject();
            res.put("value", obj);
            Log.i("main", "Resolving auth call");
            this.authRequest.resolve(res);
            return;
        }

        System.out.println("Intercepted URL: " + url);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GeolocationBridge.requestCode) {
            if (this.locationRequest != null) {
                // We've only requested "FINE_LOCATION"
                var granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.i("Geolocation", "Got fine location request: " + granted);
                Databridge.sendAnswerTo(this.locationRequest, granted ? "granted" : "denied");
            }
        }
    }

}
