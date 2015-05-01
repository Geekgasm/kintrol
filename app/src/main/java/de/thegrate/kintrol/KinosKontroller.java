package de.thegrate.kintrol;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

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
                telnetClient.connect(deviceIp, PORT);
                notificationHandler = new KinosNotificationHandler(telnetClient, notificationListener, statusChecker);
                Thread notificationHandlerThread = new Thread(notificationHandler);
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
        checkInputProfile();
    }

    public void checkForOperationStatus() {
        sendCommand("$STANDBY ?$");
    }

    public void checkVolume() {
        sendCommand("$VOLUME ?$");
    }

    public void checkInputProfile() {
        sendCommand("$INPUT PROFILE ?$");
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

    private void sendCommand(String commandString) {
        try {
            establishConnection();
            OutputStream outputStream = telnetClient.getOutputStream();
            outputStream.write((commandString + "\n").getBytes("UTF-8"));
            outputStream.flush();
        } catch (IOException | InvalidTelnetOptionException ex) {
            Log.e(TAG, "Error sending command '" + commandString + "'", ex);
            try {
                telnetClient.disconnect();
            } catch (IOException ex2) {
                Log.w(TAG, "Error closing connection", ex2);
            }
        }
    }

    public void stop() {
        notificationHandler.shutdown();
        disconnectTelnetClient();
        telnetClient = null;
    }
}
