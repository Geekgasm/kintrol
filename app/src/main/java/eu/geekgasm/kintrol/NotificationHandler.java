/*
 Kintrol: Remote control app for LINN(R) KINOS(TM), KISTO(TM) and
 Klimax Kontrol(TM) system controllers.
 Copyright (C) 2015-2018 Oliver Götz

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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationHandler implements Runnable {

    private static final String TAG = NotificationHandler.class.getSimpleName();

    private TelnetCommunicator telnetCommunicator;
    private NotificationListener notificationListener;
    private StatusChecker statusChecker;
    private Device device;
    private final int reconnectDelayMillis;
    private String currentVolume = NotificationListener.NOT_AVAILABLE;
    private boolean isMuted = false;
    private boolean isOperational = false;
    private boolean isUnityGainOn = false;
    private boolean stopRequested = false;

    public NotificationHandler(TelnetCommunicator telnetClient,
                               NotificationListener notificationListener,
                               StatusChecker statusChecker,
                               Device device,
                               int reconnectDelayMillis) {
        this.telnetCommunicator = telnetClient;
        this.notificationListener = notificationListener;
        this.statusChecker = statusChecker;
        this.device = device;
        this.reconnectDelayMillis = reconnectDelayMillis;
    }

    @Override
    public void run() {
        Log.i(TAG, "Starting NotificationHandler thread");
        do {
            statusChecker.checkDeviceStatus(200);
            statusChecker.checkDeviceStatus(600);
            try {
                BufferedReader reader = telnetCommunicator.getInputReader();
                String deviceData = "";
                while (!stopRequested && (deviceData = reader.readLine()) != null) {
                    updateDeviceState(deviceData);
                }
            } catch (IOException e) {
                Log.i(TAG, "Exception while reading socket, trying to recover:", e);
                try {
                    Thread.sleep(reconnectDelayMillis);
                } catch (InterruptedException e1) {
                    // continue on thread interrupt
                }
            }
        } while (!stopRequested && reconnectDelayMillis > 0);
        Log.i(TAG, "Stopping NotificationHandler thread");
        telnetCommunicator = null;
        notificationListener = null;
    }

    public synchronized void requestStop() {
        Log.d(TAG, "Stop requested for NotificationHandler thread");
        stopRequested = true;
    }

    public boolean isMuted() {
        return isMuted;
    }

    private void updateDeviceState(String deviceData) {
        Log.d(TAG, "Received device data: " + deviceData);
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
        Matcher unityGainMatcher = getMatcher(ResponseValueKey.INPUT_UNITY_GAIN, deviceData);
        if (matches(unityGainMatcher)) {
            isUnityGainOn = isOn(unityGainMatcher.group(2));
            updateStatus = true;
        }
        Matcher muteMatcher = getMatcher(ResponseValueKey.MUTE_STATUS, deviceData);
        if (matches(muteMatcher)) {
            String muted = muteMatcher.group(2);
            isMuted = isOn(muted);
            updateStatus = true;
        }
        if (isOperational && updateStatus) {
            String volumeText;
            if (isUnityGainOn) {
                volumeText = NotificationListener.UNITY_GAIN_TEXT;
            } else if (isMuted) {
                volumeText = NotificationListener.MUTED_TEXT;
            } else {
                volumeText = currentVolume;
            }
            boolean buttonsEnabled = !isUnityGainOn;
            notificationListener.handleVolumeUpdate(volumeText, buttonsEnabled);
            notificationListener.handleOperationStatusUpdate(NotificationListener.OPERATIONAL_STATUS_TEXT);
        }
        return updateStatus;
    }

    private boolean updateInputProfileStatus(String deviceData) {
        Matcher matcher = getMatcher(ResponseValueKey.INPUT_PROFILE_STATUS, deviceData);
        if (isOperational && matches(matcher)) {
            String currentInputProfileId = matcher.group(2);
            if (matcher.groupCount() >= 3) {
                String currentInputProfileName = matcher.group(3);
                notificationListener.handleSourceUpdate(currentInputProfileName);
                notificationListener.handleOperationStatusUpdate(NotificationListener.OPERATIONAL_STATUS_TEXT);
            } else {
                statusChecker.checkInputName(currentInputProfileId);
            }
            statusChecker.checkUnityGain(currentInputProfileId);
            return true;
        }
        return false;
    }

    private boolean updateInputNameStatus(String deviceData) {
        Matcher matcher = getMatcher(ResponseValueKey.INPUT_NAME, deviceData);
        if (isOperational && matches(matcher)) {
            String currentInputProfileName = matcher.group(2).trim();
            notificationListener.handleSourceUpdate(currentInputProfileName);
            notificationListener.handleOperationStatusUpdate(NotificationListener.OPERATIONAL_STATUS_TEXT);
            return true;
        }
        return false;
    }

    private boolean updateSurroundModeStatus(String deviceData) {
        Matcher matcher = getMatcher(ResponseValueKey.SURROUND_MODE, deviceData);
        if (isOperational && matches(matcher)) {
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
            String operationStatus;
            if (isOff(standbyStatus)) {
                operationStatus = NotificationListener.OPERATIONAL_STATUS_TEXT;
                statusChecker.checkVolume();
                statusChecker.checkInputProfile();
                statusChecker.checkSurroundMode();
                isOperational = true;
            } else if (isOn(standbyStatus)) {
                operationStatus = NotificationListener.STANDBY_STATUS_TEXT;
                notificationListener.handleVolumeUpdate(NotificationListener.NOT_AVAILABLE, true);
                notificationListener.handleSourceUpdate(NotificationListener.NOT_AVAILABLE);
                notificationListener.handleSurroundModeUpdate(NotificationListener.NOT_AVAILABLE);
                isOperational = false;
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
            String hardwareVersion = matcher.group(2);
            notificationListener.handleHardwareVersionUpdate(hardwareVersion);
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

    private boolean isOn(String status) {
        return "ON".equals(status) || "Y".equals(status);
    }

    private boolean isOff(String status) {
        return "OFF".equals(status) || "N".equals(status);
    }
}
