/*
 Kintrol: Remote control app for LINN(R) KINOS(TM) and KISTO(TM) system controllers.
 Copyright (C) 2015 Oliver Götz

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License version 3.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.geekgasm.kintrol;

import android.util.Log;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationHandler implements Runnable {

    private static final String TAG = NotificationHandler.class.getSimpleName();

    private TelnetClient telnetClient;
    private NotificationListener notificationListener;
    private StatusChecker statusChecker;
    private Device device;
    private String currentVolume = NotificationListener.NOT_AVAILABLE;
    private boolean isMuted = false;

    public NotificationHandler(TelnetClient telnetClient,
                               NotificationListener notificationListener,
                               StatusChecker statusChecker,
                               Device device) {
        this.telnetClient = telnetClient;
        this.notificationListener = notificationListener;
        this.statusChecker = statusChecker;
        this.device = device;
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

    public boolean isMuted() {
        return isMuted;
    }

    private void updateDeviceState(String deviceData) {
        updateVolumeStatus(deviceData);
        updateInputProfileStatus(deviceData);
        updateInputNameStatus(deviceData);
        updateSurroundModeStatus(deviceData);
        updateStandbyStatus(deviceData);
        updateDeviceId(deviceData);
        updatePowerCounter(deviceData);
        updateSoftwareVersion(deviceData);
        updateHardwareVersion(deviceData);
    }

    private boolean updateVolumeStatus(String deviceData) {
        boolean updateStatus = false;
        Matcher volumeMatcher = getMatcher(ResponseValueKey.VOLUME_STATUS, deviceData);
        if (matches(volumeMatcher)) {
            currentVolume = volumeMatcher.group(2);
            updateStatus = true;
        }
        Matcher muteMatcher = getMatcher(ResponseValueKey.MUTE_STATUS, deviceData);
        if (matches(muteMatcher)) {
            String muted = muteMatcher.group(2);
            isMuted = isOn(muted);
            updateStatus = true;
        }
        if (updateStatus) {
            notificationListener.handleVolumeUpdate(isMuted ? "MUTED" : currentVolume);
            notificationListener.handleOperationStatusUpdate(NotificationListener.OPERATIONAL_STATUS_TEXT);
        }
        return updateStatus;
    }

    private boolean updateInputProfileStatus(String deviceData) {
        Matcher matcher = getMatcher(ResponseValueKey.INPUT_PROFILE_STATUS, deviceData);
        if (matches(matcher)) {
            String currentInputProfileId = matcher.group(2);
            if (matcher.groupCount() >= 3) {
                String currentInputProfileName = matcher.group(3);
                notificationListener.handleSourceUpdate(currentInputProfileName);
                notificationListener.handleOperationStatusUpdate(NotificationListener.OPERATIONAL_STATUS_TEXT);
            } else {
                statusChecker.checkInputName(currentInputProfileId);
            }
            return true;
        }
        return false;
    }

    private boolean updateInputNameStatus(String deviceData) {
        Matcher matcher = getMatcher(ResponseValueKey.INPUT_NAME, deviceData);
        if (matches(matcher)) {
            String currentInputProfileName = matcher.group(2).trim();
            notificationListener.handleSourceUpdate(currentInputProfileName);
            notificationListener.handleOperationStatusUpdate(NotificationListener.OPERATIONAL_STATUS_TEXT);
            return true;
        }
        return false;
    }

    private boolean updateSurroundModeStatus(String deviceData) {
        Matcher matcher = getMatcher(ResponseValueKey.SURROUND_MODE, deviceData);
        if (matches(matcher)) {
            String currentSurroundMode = matcher.group(2);
            notificationListener.handleSurroundModeUpdate(currentSurroundMode);
            notificationListener.handleOperationStatusUpdate(NotificationListener.OPERATIONAL_STATUS_TEXT);
            return true;
        }
        return false;
    }

    private boolean updateStandbyStatus(String deviceData) {
        Matcher matcher = getMatcher(ResponseValueKey.STANDBY_STATUS, deviceData);
        if (matches(matcher)) {
            String standbyStatus = matcher.group(2);
            String operationStatus = "<unknown>";
            if (isOff(standbyStatus)) {
                operationStatus = NotificationListener.OPERATIONAL_STATUS_TEXT;
                statusChecker.checkVolume();
                statusChecker.checkInputProfile();
                statusChecker.checkSurroundMode();
            } else if (isOn(standbyStatus)) {
                operationStatus = NotificationListener.STANDBY_STATUS_TEXT;
                notificationListener.handleVolumeUpdate(NotificationListener.NOT_AVAILABLE);
                notificationListener.handleSourceUpdate(NotificationListener.NOT_AVAILABLE);
                notificationListener.handleSurroundModeUpdate(NotificationListener.NOT_AVAILABLE);
            } else {
                operationStatus = standbyStatus;
            }
            notificationListener.handleOperationStatusUpdate(operationStatus);
            return true;
        }
        return false;
    }

    private boolean updatePowerCounter(String deviceData) {
        Matcher matcher = getMatcher(ResponseValueKey.POWER_COUNTER, deviceData);
        if (matches(matcher)) {
            String powerCounterValue = matcher.group(2);
            notificationListener.handlePowerCounterUpdate(powerCounterValue);
            return true;
        }
        return false;

    }

    private boolean updateDeviceId(String deviceData) {
        Matcher matcher = getMatcher(ResponseValueKey.DEVICE_ID, deviceData);
        if (matches(matcher)) {
            String deviceId = matcher.group(2);
            notificationListener.handleDeviceIdUpdate(deviceId);
            return true;
        }
        return false;
    }

    private boolean updateSoftwareVersion(String deviceData) {
        Matcher matcher = getMatcher(ResponseValueKey.SOFTWARE_VERSION, deviceData);
        if (matches(matcher)) {
            String softwareVersion = matcher.group(2);
            notificationListener.handleSoftwareVersionUpdate(softwareVersion);
            return true;
        }
        return false;

    }

    private boolean updateHardwareVersion(String deviceData) {
        Matcher matcher = getMatcher(ResponseValueKey.HARDWARE_VERSION, deviceData);
        if (matches(matcher)) {
            String softwareVersion = matcher.group(2);
            notificationListener.handleSoftwareVersionUpdate(softwareVersion);
            return true;
        }
        return false;
    }

    private Matcher getMatcher(ResponseValueKey key, String deviceData) {
        Pattern pattern = device.getResponsePatterns().get(key);
        if (pattern == null) {
            return null;
        }
        return pattern.matcher(deviceData);
    }

    private boolean matches(Matcher matcher) {
        return matcher != null && matcher.matches();
    }

    public void shutdown() {
        telnetClient = null;
        notificationListener = null;
    }

    private boolean isOn(String status) {
        return "ON".equals(status) || "Y".equals(status);
    }

    private boolean isOff(String status) {
        return "OFF".equals(status) || "N".equals(status);
    }
}
