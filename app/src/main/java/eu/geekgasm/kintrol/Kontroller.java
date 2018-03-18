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

import java.io.IOException;

public class Kontroller {
    private static final String TAG = Kontroller.class.getSimpleName();

    private NotificationListener notificationListener;
    private StatusChecker statusChecker;
    private Device device;
    private TelnetCommunicator telnetCommunicator;
    private NotificationHandler notificationHandler;

    public Kontroller(DeviceInfo deviceInfo,
                      NotificationListener notificationListener,
                      StatusChecker statusChecker) {
        this.notificationListener = notificationListener;
        this.statusChecker = statusChecker;
        this.device = DeviceDirectory.getDevice(deviceInfo.getDeviceType());
        this.telnetCommunicator = new TelnetCommunicator(deviceInfo);
    }

    public void start() {
        notificationListener.handleNoConnectionStatusUpdate();
        try {
            connectToDevice();
        } catch (Exception e) {
            Log.e(TAG, "Error starting Kontroller", e);
        }
    }

    private void connectToDevice() throws IOException {
        if (telnetCommunicator.connect()) {
            notificationHandler = new NotificationHandler(telnetCommunicator, notificationListener, statusChecker, device);
            Thread notificationHandlerThread = new Thread(notificationHandler, "Device response handler thread");
            notificationHandlerThread.start();
        }
    }

    public void checkDeviceStatus() {
        checkOperationStatus();
        checkVolume();
        checkMuteStatus();
        checkInputProfile();
        checkSurroundMode();
        checkDeviceId();
        checkPowerCounter();
        checkSoftwareVersion();
        checkHardwareVersion();
    }

    public void checkOperationStatus() {
        sendCommand(KommandKey.checkOperationStatus);
    }

    public void checkVolume() {
        sendCommand(KommandKey.checkVolume);
    }

    private void checkMuteStatus() {
        sendCommand(KommandKey.checkMuteStatus);
    }

    public void checkInputProfile() {
        sendCommand(KommandKey.checkInputProfile);
    }

    public void checkUnityGain(String currentInputProfileId) {
        sendCommand(KommandKey.checkUnityGain, currentInputProfileId);
    }

    public void checkInputName(String currentInputProfileId) {
        sendCommand(KommandKey.checkInputName, currentInputProfileId);
    }

    public void checkSurroundMode() {
        sendCommand(KommandKey.checkSurroundMode);
    }

    public void checkDeviceId() {
        sendCommand(KommandKey.checkDeviceId);
    }

    public void checkPowerCounter() {
        sendCommand(KommandKey.checkPowerCounter);
    }

    public void checkSoftwareVersion() {
        sendCommand(KommandKey.checkSoftwareVersion);
    }

    public void checkHardwareVersion() {
        sendCommand(KommandKey.checkHardwareVersion);
    }

    public void switchOn() {
        sendCommand(KommandKey.switchOn);
    }

    public void switchOff() {
        sendCommand(KommandKey.switchOff);
    }

    public void decreaseVolume() {
        sendCommand(KommandKey.decreaseVolume);
    }

    public void increaseVolume() {
        sendCommand(KommandKey.increaseVolume);
    }

    public void previousInputProfile() {
        sendCommand(KommandKey.previousInputProfile);
    }

    public void nextInputProfile() {
        sendCommand(KommandKey.nextInputProfile);
    }

    public void toggleMute() {
        KommandKey toggleMuteKommand = KommandKey.toggleMute;
        if (!device.getCommands().containsKey(KommandKey.toggleMute)) {
            toggleMuteKommand = notificationHandler.isMuted() ? KommandKey.muteOff : KommandKey.muteOn;
        }
        sendCommand(toggleMuteKommand);
    }

    public void setVolume(int volume) {
        sendCommand(KommandKey.setVolume, volume);
    }

    public void previousSurroundMode() {
        sendCommand(KommandKey.previousSurroundMode);
    }

    public void nextSurroundMode() {
        sendCommand(KommandKey.nextSurroundMode);
    }

    private void sendCommand(KommandKey kommandKey, Object... arguments) {
        String commandString = device.getCommands().get(kommandKey);
        if (commandString == null || commandString.trim().equals("")) {
            Log.d(TAG, "Command " + kommandKey + " not supported for this device, ignoring.");
            return;
        }
        try {
            commandString = String.format(commandString, arguments);
            Log.d(TAG, "Sending command: " + commandString);
            telnetCommunicator.sendLine("$" + commandString + "$");
        } catch (IOException ex) {
            Log.e(TAG, "Error sending command '" + commandString + "'", ex);
            telnetCommunicator.disconnect();
        }
    }

    public void stop() {
        if (notificationHandler != null) {
            notificationHandler.requestStop();
            notificationHandler = null;
        }
        telnetCommunicator.shutdown();
    }

}
