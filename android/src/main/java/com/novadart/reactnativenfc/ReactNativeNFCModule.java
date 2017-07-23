package com.novadart.reactnativenfc;


import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class ReactNativeNFCModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener {

    private static final String EVENT_NFC_DISCOVERED = "DISCOVERED";
    private static final String EVENT_NFC_ON = "ON";
    private static final String EVENT_NFC_OFF = "OFF";

    private NfcAdapter nfcAdapter;

    public ReactNativeNFCModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "ReactNativeNFC";
    }

    @Override
    public void onHostResume() {
        setupForegroundDispatch();
    }

    @Override
    public void onHostPause() {
        if (nfcAdapter != null)
            stopForegroundDispatch();
    }

    @Override
    public void onHostDestroy() {
    }


    public void setupForegroundDispatch() {
        if(nfcAdapter == null){
            nfcAdapter = NfcAdapter.getDefaultAdapter(getReactApplicationContext());
        }

        nfcAdapter.enableForegroundDispatch(getCurrentActivity(), null, null, null);
    }

    public void stopForegroundDispatch() {
        nfcAdapter.disableForegroundDispatch(getCurrentActivity());
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {}

    @Override
    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {

            switch (intent.getAction()){
                case NfcAdapter.ACTION_ADAPTER_STATE_CHANGED:
                    if(nfcAdapter.isEnabled()) {
                        sendEvent(EVENT_NFC_ON, null);
                    }else{
                        sendEvent(EVENT_NFC_OFF, null);
                    }
                    break;

                case NfcAdapter.ACTION_TECH_DISCOVERED:
                    byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                    String hexId = toHex(id);
                    sendEvent(EVENT_NFC_DISCOVERED, hexId);
                    break;

            }
        }
    }

    /**
     * This method is used to check the status of the NFC adapter.
     * @param promise for javascript that resolves to a status
     */
    @ReactMethod
    public void getStatus(Promise promise){
        try {
            NfcStatusType status = NfcStatusType.NA;
            if(nfcAdapter != null){
                status = NfcStatusType.ON;
            }
            if(!nfcAdapter.isEnabled()){
                status = NfcStatusType.OFF;
            }
            promise.resolve(status);
        }catch (Exception e) {
            promise.reject("Failed to get NFC status", e);
        }
    }


    private void sendEvent(String event, @Nullable Object payload) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(getName() + "/" + event, payload);
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

}
