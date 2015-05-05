package eu.geekgasm.kintrol;

import android.util.Log;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by d037698 on 4/26/15.
 */
public class KinosKontroller {
    private static final String TAG = KinosKontroller.class.getSimpleName();
    private static final int PORT = 9004;

    private static String deviceIp;
    private KinosNotificationListener notificationListener;
    private KinosStatusChecker statusChecker;
    private TelnetClient telnetClient;
    private KinosNotificationHandler notificationHandler;

    public KinosKontroller(String deviceIp, KinosNotificationListener notificationListener, KinosStatusChecker statusChecker) {
        this.deviceIp = deviceIp;
        this.notificationListener = notificationListener;
        this.statusChecker = statusChecker;
    }

    public void start() {
        disconnectTelnetClient();
        try {
            establishConnection();
        } catch (Exception e) {
            Log.e(TAG, "Error starting KinosKontroller", e);
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
    }

    private void establishConnection() throws IOException, InvalidTelnetOptionException {
        if (telnetClient == null) {
            createTelnetClient();
        }
        if (!telnetClient.isConnected()) {
            try {
                Log.d(TAG, "Opening telnet connection to " + deviceIp + ":" + PORT);
                telnetClient.connect(deviceIp, PORT);
                notificationHandler = new KinosNotificationHandler(telnetClient, notificationListener, statusChecker);
                Thread notificationHandlerThread = new Thread(notificationHandler, "Kinos response handler thread");
                notificationHandlerThread.start();
            } catch (IOException e) {
                Log.e(TAG, "Unable to open telnet connection to " + deviceIp + ":" + PORT, e);
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
        checkForOperationStatus();
        checkVolume();
        checkMuteStatus();
        checkInputProfile();
        checkSurroundMode();
        checkDeviceId();
        checkPowerCounter();
        checkSoftwareVersion();
    }

    public void checkForOperationStatus() {
        sendCommand("$STANDBY ?$");
    }

    public void checkVolume() {
        sendCommand("$VOLUME ?$");
    }

    private void checkMuteStatus() {
        sendCommand("$MUTE ?$");
    }

    public void checkInputProfile() {
        sendCommand("$INPUT PROFILE ?$");
    }

    public void checkSurroundMode() {
        sendCommand("$SURROUND ?$");
    }

    public void checkDeviceId() {
        sendCommand("$ID ?$");
    }

    public void checkPowerCounter() {
        sendCommand("$COUNTER POWER ?$");
    }

    public void checkSoftwareVersion() {
        sendCommand("$VERSION SOFTWARE ?$");
    }

    public void switchOn() {
        sendCommand("$STANDBY OFF$");
    }

    public void switchOff() {
        sendCommand("$STANDBY ON$");
    }

    public void decreaseVolume() {
        sendCommand("$VOLUME -$");
    }

    public void increaseVolume() {
        sendCommand("$VOLUME +$");
    }

    public void previousInputProfile() {
        sendCommand("$INPUT PROFILE -$");
    }

    public void nextInputProfile() {
        sendCommand("$INPUT PROFILE +$");
    }

    public void toggleMute() {
        sendCommand("$MUTE TOGGLE$");
    }

    public void previousSurroundMode() {
        sendCommand("$SURROUND -$");
    }

    public void nextSurroundMode() {
        sendCommand("$SURROUND +$");
    }

    private void sendCommand(String commandString) {
        try {
            establishConnection();
            OutputStream outputStream = telnetClient.getOutputStream();
            if (outputStream == null)
                throw new IOException("Could not get output stream from telnet client");
            outputStream.write((commandString + "\n").getBytes("UTF-8"));
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
