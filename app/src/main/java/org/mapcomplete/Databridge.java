package org.mapcomplete;


import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@CapacitorPlugin(name = "Databridge")
public class Databridge extends Plugin {

    private static final Map<String, Consumer<PluginCall>> responders = new HashMap<>();
    static {
        responders.put("meta", Databridge.answer("capacitator-shell 0.0.1;"));
    }

    private static Consumer<PluginCall> answer(String answer) {
        return (PluginCall call) -> Databridge.sendAnswerTo(call, answer);
    }

    public static void sendAnswerTo(PluginCall call, String answer) {
        JSObject ret = new JSObject();
        ret.put("value", answer);
        Log.i("databridge", "Resolving call");
        call.resolve(ret);
    }

    /**
     * A responder will be activated if the native code asks for it.
     * Use call.setKeepAlive(true) if the responder will send multiple responses
     */
    public static void addResponder(String key, Consumer<PluginCall> responder) {
        responders.put(key, responder);
    }

    @PluginMethod()
    public void request(PluginCall call) {
        String key = call.getString("key");
        Log.i("databridge", "Got a call: " + key);
        var c = this.responders.get(key);
        if (c != null) {
            c.accept(call);
        } else {
            call.reject("ERROR: no responder installed for " + key);
        }
    }
}
