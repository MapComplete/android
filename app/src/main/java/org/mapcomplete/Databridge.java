package org.mapcomplete;


import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@CapacitorPlugin(name = "Echo")
public class Databridge extends Plugin {

    private final Map<String, Consumer<PluginCall>> responders;

    private static Consumer<PluginCall> answer(String answer) {
        JSObject ret = new JSObject();
        ret.put("value", answer);
        Log.i("databridge","Resolving call");
        return (PluginCall call) -> call.resolve(ret);
    }

    /**
     * A responder will be activated if the native code asks for it.
     * Use call.setKeepAlive(true) for multiple responses
     * @param responders
     */
    public Databridge(Map<String, Consumer<PluginCall>> responders) {
        this.responders = responders;
        responders.put("meta", Databridge.answer("capacitator-shell 0.0.1;"));
    }

    @PluginMethod()
    public void request(PluginCall call) {
        String key = call.getString("key");
        Log.i("databridge","Got a call: "+key);
        var c=  this.responders.get(key);
        if(c != null){
            c.accept(call);
        }else{
            call.reject("ERROR: no responder installed for "+key);
        }
    }
}
