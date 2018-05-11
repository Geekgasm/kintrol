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

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

import java.io.IOException;
import java.io.OutputStream;

public class Kontroller {
    private static final String TAG = Kontroller.class.getSimpleName();
    private static final int DEFAULT_PORT = 9004;

    private String deviceIp;
    private int devicePort;
    private NotificationListener notificationListener;
    private StatusChecker statusChecker;
    private Device device;
    private TelnetClient telnetClient;
    private NotificationHandler notificationHandler;

    public Kontroller(String deviceIp,
                      String devicePortString, NotificationListener notificationListener,
                      StatusChecker statusChecker,
                      Device device) {
        this.deviceIp = deviceIp;
        this.devicePort = decodeDevicePort(devicePortString);
        this.notificationListener = notificationListener;
        this.statusChecker = statusChecker;
        this.device = device;
    }

    private int decodeDevicePort(String devicePortString) {
        if (devicePortString != null) {
            try {
                return Integer.valueOf(devicePortString);
            } catch (NumberFormatException ex) {
                // Fall back to default
            }
        }
        return DEFAULT_PORT;
    }

    public void start() {
        disconnectTelnetClient();
        try {
            establishConnection();
        } catch (Exception e) {
            Log.e(TAG, "Error starting Kontroller", e);
        }
    }

    private void disconnectTelnetClient() {
        if (telnetClient != null && telnetClient.isConnected()) {
            try {
                telnetClient.disconnect();
            } catch (IOException e) {
                Log.w(TAG, "Error disconnecting Telnet client: ", e);
            }
        }
        notificationListener.handleNoConnectionStatusUpdate();
    }

    private void establishConnection() throws IOException, InvalidTelnetOptionException {
        if (telnetClient == null) {
            createTelnetClient();
        }
        if (!telnetClient.isConnected()) {
            try {
                Log.d(TAG, "Opening telnet connection to " + deviceIp + ":" + devicePort);
                telnetClient.connect(deviceIp, devicePort);
                notificationHandler = new NotificationHandler(telnetClient, notificationListener, statusChecker, device);
                Thread notificationHandlerThread = new Thread(notificationHandler, "Device response handler thread");
                notificationHandlerThread.start();
            } catch (IOException e) {
                Log.e(TAG, "Unable to open telnet connection to " + deviceIp + ":" + devicePort, e);
            }
        }
    }

    private void createTelnetClient() throws IOException, InvalidTelnetOptionException {
        telnetClient = new TelnetClient();

        TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
        EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true, false);
        SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);

        telnetClient.addOptionHandler(ttopt);
        telnetClient.addOptionHandler(echoopt);
        telnetClient.addOptionHandler(gaopt);
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
            return;
        }
        try {
            commandString = String.format(commandString, arguments);
            Log.d(TAG, "Sending command: " + commandString);
            establishConnection();
            OutputStream outputStream = telnetClient.getOutputStream();
            if (outputStream == null)
                throw new IOException("Could not get output stream from telnet client");
            outputStream.write(("$" + commandString + "$\n").getBytes("UTF-8"));
            outputStream.flush();
        } catch (IOException | InvalidTelnetOptionException ex) {
            Log.e(TAG, "Error sending command '" + commandString + "'", ex);
            try {
                telnetClient.disconnect();
            } catch (IOException ioex) {
                Log.w(TAG, "Error closing connection", ioex);
            }
        }
    }

    public void stop() {
        if (notificationHandler != null)
            notificationHandler.shutdown();
        disconnectTelnetClient();
        telnetClient = null;
    }

}
