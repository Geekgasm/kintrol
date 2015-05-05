package de.thegrate.kintrol;

import android.util.Log;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by d037698 on 4/26/15.
 */
public class KinosNotificationHandler implements Runnable {

    private static final String TAG = KinosNotificationHandler.class.getSimpleName();

    static final Pattern VOLUME_STATUS_PATTERN = Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$VOLUME ([^\\$]+)\\$.*", Pattern.DOTALL);
    static final Pattern MUTE_STATUS_PATTERN = Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$MUTE ([^\\$]+)\\$.*", Pattern.DOTALL);
    static final Pattern INPUT_PROFILE_STATUS_PATTERN = Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$INPUT PROFILE (\\d+) \\(([^\\$]+)\\)\\$.*", Pattern.DOTALL);
    static final Pattern DEVICE_ID_PATTERN = Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$ID ([^\\$]+)\\$.*", Pattern.DOTALL);
    static final Pattern POWER_COUNTER_PATTERN = Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$COUNTER POWER ([^\\$]+)\\$.*", Pattern.DOTALL);
    private static final Pattern STANDBY_STATUS_PATTERN = Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$STANDBY ([^\\$]+)\\$.*", Pattern.DOTALL);
    private TelnetClient telnetClient;
    private KinosNotificationListener notificationListener;
    private KinosStatusChecker statusChecker;
    private String currentVolume = KinosNotificationListener.NOT_AVAILABLE;
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
        statusChecker.checkDeviceStatus(600);
        try {
            while ((deviceData = reader.readLine()) != null) {
                updateDeviceState(deviceData);
            }
        } catch (IOException e) {
            Log.w(TAG, "Error in KinosNotificationHandler Thread, Exception while reading socket:", e);
        }
    }

    private void updateDeviceState(String deviceData) {
        updateVolumeStatus(deviceData);
        updateInputProfileStatus(deviceData);
        updateStandbyStatus(deviceData);
        updateDeviceId(deviceData);
        updatePowerCounter(deviceData);
    }

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
            notificationListener.handleOperationStatusUpdate(KinosNotificationListener.OPERATIONAL_STATUS_TEXT);
        }
        return updateStatus;
    }

    private boolean updateInputProfileStatus(String deviceData) {
        Matcher matcher = INPUT_PROFILE_STATUS_PATTERN.matcher(deviceData);
        if (matcher.matches()) {
            String currentInputProfile = matcher.group(3);
            notificationListener.handleSourceUpdate(currentInputProfile);
            notificationListener.handleOperationStatusUpdate(KinosNotificationListener.OPERATIONAL_STATUS_TEXT);
            return true;
        }
        return false;
    }

    private boolean updateStandbyStatus(String deviceData) {
        Matcher matcher = STANDBY_STATUS_PATTERN.matcher(deviceData);
        if (matcher.matches()) {
            String standbyStatus = matcher.group(2);
            String operationStatus = "<unknown>";
            if ("OFF".equals(standbyStatus)) {
                operationStatus = KinosNotificationListener.OPERATIONAL_STATUS_TEXT;
                statusChecker.checkVolume();
                statusChecker.checkInputProfile();
            } else if ("ON".equals(standbyStatus)) {
                operationStatus = KinosNotificationListener.STANDBY_STATUS_TEXT;
                notificationListener.handleVolumeUpdate(KinosNotificationListener.NOT_AVAILABLE);
                notificationListener.handleSourceUpdate(KinosNotificationListener.NOT_AVAILABLE);
            } else {
                operationStatus = standbyStatus;
            }
            notificationListener.handleOperationStatusUpdate(operationStatus);
            return true;
        }
        return false;
    }

    private boolean updatePowerCounter(String deviceData) {
        Matcher matcher = POWER_COUNTER_PATTERN.matcher(deviceData);
        if (matcher.matches()) {
            String powerCounterValue = matcher.group(2);
            notificationListener.handlePowerCounterUpdate(powerCounterValue);
            return true;
        }
        return false;

    }

    private boolean updateDeviceId(String deviceData) {
        Matcher matcher = DEVICE_ID_PATTERN.matcher(deviceData);
        if (matcher.matches()) {
            String deviceId = matcher.group(2);
            notificationListener.handleDeviceIdUpdate(deviceId);
            return true;
        }
        return false;

    }


    public void shutdown() {
        telnetClient = null;
        notificationListener = null;
    }
}
