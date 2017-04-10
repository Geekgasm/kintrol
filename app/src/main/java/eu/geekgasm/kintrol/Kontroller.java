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
        sendCommand(device.getCommands().get(KommandKey.checkOperationStatus));
    }

    public void checkVolume() {
        sendCommand(device.getCommands().get(KommandKey.checkVolume));
    }

    private void checkMuteStatus() {
        sendCommand(device.getCommands().get(KommandKey.checkMuteStatus));
    }

    public void checkInputProfile() {
        sendCommand(device.getCommands().get(KommandKey.checkInputProfile));
    }

    public void checkInputName(String currentInputProfileId) {
        String command = device.getCommands().get(KommandKey.checkInputName);
        if (command != null) {
            sendCommand(String.format(command, currentInputProfileId));
        }
    }

    public void checkSurroundMode() {
        sendCommand(device.getCommands().get(KommandKey.checkSurroundMode));
    }

    public void checkDeviceId() {
        sendCommand(device.getCommands().get(KommandKey.checkDeviceId));
    }

    public void checkPowerCounter() {
        sendCommand(device.getCommands().get(KommandKey.checkPowerCounter));
    }

    public void checkSoftwareVersion() {
        sendCommand(device.getCommands().get(KommandKey.checkSoftwareVersion));
    }

    public void checkHardwareVersion() {
        sendCommand(device.getCommands().get(KommandKey.checkHardwareVersion));
    }

    public void switchOn() {
        sendCommand(device.getCommands().get(KommandKey.switchOn));
    }

    public void switchOff() {
        sendCommand(device.getCommands().get(KommandKey.switchOff));
    }

    public void decreaseVolume() {
        sendCommand(device.getCommands().get(KommandKey.decreaseVolume));
    }

    public void increaseVolume() {
        sendCommand(device.getCommands().get(KommandKey.increaseVolume));
    }

    public void previousInputProfile() {
        sendCommand(device.getCommands().get(KommandKey.previousInputProfile));
    }

    public void nextInputProfile() {
        sendCommand(device.getCommands().get(KommandKey.nextInputProfile));
    }

    public void toggleMute() {
        String toggleMute = device.getCommands().get(KommandKey.toggleMute);
        if (toggleMute == null) {
            toggleMute = device.getCommands().get(notificationHandler.isMuted() ? KommandKey.muteOff : KommandKey.muteOn);
        }
        sendCommand(toggleMute);
    }

    public void setVolume(int volume) {
        sendCommand(device.getCommands().get(KommandKey.setVolume) + volume);
    }

    public void previousSurroundMode() {
        sendCommand(device.getCommands().get(KommandKey.previousSurroundMode));
    }

    public void nextSurroundMode() {
        sendCommand(device.getCommands().get(KommandKey.nextSurroundMode));
    }

    private void sendCommand(String commandString) {
        if (commandString == null || commandString.trim().equals("")) {
            return;
        }
        try {
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
