package de.thegrate.kintrol;

import android.util.Log;
import android.widget.TextView;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by d037698 on 4/26/15.
 */
public class KinosNotificationHandler implements Runnable {

    private static final String TAG = KinosNotificationHandler.class.getSimpleName();

    private static final String NOT_AVAILABLE = "---";
    public static final String OPERATIONAL_STATUS_TEXT = "Operational";
    public static final String STANDBY_STATUS_TEXT = "Standby";
    private TelnetClient telnetClient;
    private KinosNotificationListener notificationListener;
    private KinosStatusChecker statusChecker;
    private String currentVolume = NOT_AVAILABLE;
    private boolean isMuted = false;

    public KinosNotificationHandler(TelnetClient telnetClient, KinosNotificationListener notificationListener, KinosStatusChecker statusChecker) {
        this.telnetClient = telnetClient;
        this.notificationListener = notificationListener;
        this.statusChecker = statusChecker;
    }

    @Override
    public void run() {
        InputStream instr = telnetClient.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(instr));
        String deviceData = "";
        statusChecker.checkDeviceStatus(200);
        try {
            while ((deviceData = reader.readLine()) != null) {
                updateDeviceState(deviceData);
            }
/*
            byte[] buff = new byte[1024];
            int ret_read = 0;

            do {
                ret_read = instr.read(buff);
                if (ret_read > 0) {
                    String deviceData = new String(buff, 0, ret_read);
                    updateDeviceState(deviceData);
                }
            }
            while (ret_read >= 0);
            */
        } catch (IOException e) {
            Log.e(TAG, "Error in KinosNotificationHandler Thread, Exception while reading socket:", e);
        }

        try {
            telnetClient.disconnect();
        } catch (IOException e) {
            Log.e(TAG, "Error in KinosNotificationHandler Thread, Exception while closing telnet:", e);
        }
    }


    private void updateDeviceState(String deviceData) {
        updateVolumeStatus(deviceData);
        updateInputProfileStatus(deviceData);
        updateStandbyStatus(deviceData);
    }

    static final Pattern VOLUME_STATUS_PATTERN = Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$VOLUME ([^\\$]+)\\$.*", Pattern.DOTALL);
    static final Pattern MUTE_STATUS_PATTERN = Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$MUTE ([^\\$]+)\\$.*", Pattern.DOTALL);

    private boolean updateVolumeStatus(String deviceData) {
        boolean updateStatus = false;
        String volume = currentVolume;

        Matcher volumeMatcher = VOLUME_STATUS_PATTERN.matcher(deviceData);
        if (volumeMatcher.matches()) {
            currentVolume = volumeMatcher.group(2);
            updateStatus = true;
        }
        Matcher muteMatcher = MUTE_STATUS_PATTERN.matcher(deviceData);
        if (muteMatcher.matches()) {
            String muted = muteMatcher.group(2);
            if ("ON".equals(muted)) {
                volume = "MUTED";
            } else {
                volume = currentVolume;
            }
            updateStatus = true;
        }
        if (updateStatus) {
            notificationListener.handleVolumeUpdate(volume);
            notificationListener.handleOperationStatusUpdate(OPERATIONAL_STATUS_TEXT);
        }
        return updateStatus;
    }

    static final Pattern INPUT_PROFILE_STATUS_PATTERN = Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$INPUT PROFILE (\\d+) \\(([^\\$]+)\\)\\$.*", Pattern.DOTALL);

    private boolean updateInputProfileStatus(String deviceData) {
        Matcher matcher = INPUT_PROFILE_STATUS_PATTERN.matcher(deviceData);
        if (matcher.matches()) {
            String currentInputProfile = matcher.group(3);
            notificationListener.handleSourceUpdate(currentInputProfile);
            notificationListener.handleOperationStatusUpdate(OPERATIONAL_STATUS_TEXT);
            return true;
        }
        return false;
    }

    private static final Pattern STANDBY_STATUS_PATTERN = Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$STANDBY ([^\\$]+)\\$.*", Pattern.DOTALL);

    private boolean updateStandbyStatus(String deviceData) {
        Matcher matcher = STANDBY_STATUS_PATTERN.matcher(deviceData);
        if (matcher.matches()) {
            String standbyStatus = matcher.group(2);
            String operationStatus = "<unknown>";
            if ("OFF".equals(standbyStatus)) {
                operationStatus = OPERATIONAL_STATUS_TEXT;
                statusChecker.checkVolume();
                statusChecker.checkInputProfile();
            } else if ("ON".equals(standbyStatus)) {
                operationStatus = STANDBY_STATUS_TEXT;
                notificationListener.handleVolumeUpdate(NOT_AVAILABLE);
                notificationListener.handleSourceUpdate(NOT_AVAILABLE);
            } else {
                operationStatus = standbyStatus;
            }
            notificationListener.handleOperationStatusUpdate(operationStatus);
            return true;
        }
        return false;
    }


    public void shutdown() {
        telnetClient = null;
        notificationListener = null;
    }
}
