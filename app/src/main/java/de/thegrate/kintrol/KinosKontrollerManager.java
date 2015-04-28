package de.thegrate.kintrol;

import android.widget.TextView;

/**
 * Created by d037698 on 4/27/15.
 */
public class KinosKontrollerManager {
    private static KinosKontroller theKontroller = new KinosKontroller();
    private static Thread kontrollerThread = null;

    public static KinosKontroller getKontroller() {
        return theKontroller;
    }

    public static void startKontroller(String deviceIp, TextView volumeView, TextView operationStateView, TextView sourceView, TextView traceDataView) {
        if (kontrollerThread != null || theKontroller.getIpAddress() != deviceIp) {
            kontrollerThread.interrupt();
            kontrollerThread = null;
        }
        if (kontrollerThread == null) {
            theKontroller.setContext(deviceIp, volumeView, operationStateView, sourceView, traceDataView);
            kontrollerThread = new Thread(theKontroller);
            kontrollerThread.start();
        }
    }
}
