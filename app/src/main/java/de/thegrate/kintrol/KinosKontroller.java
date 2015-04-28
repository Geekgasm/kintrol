package de.thegrate.kintrol;

import android.util.Log;
import android.widget.TextView;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by d037698 on 4/26/15.
 */
public class KinosKontroller implements Runnable {
    private static final int PORT = 9004;
    private String deviceIp;
    private KinosNotificationHandler notificationHandler;
    private TelnetClient telnetClient;
    private TextView volumeView;
    private TextView operationStateView;
    private TextView sourceView;
    private TextView traceDataView;
    private Queue<String> commandQueue = new LinkedBlockingQueue<>();

    public KinosKontroller() {
    }

    @Override
    public void run() {
        try {
            createTelnetClient();
            establishConnection();
            do {
                executeCommands();
            } while (true);
        } catch (InterruptedException ie) {
            KinosNotificationHandler.appendTextViewText(traceDataView, "Kontroller Thread interrupted");
        } catch (Exception e) {
            String msg = "Error in KinosKontroller Thread";
            Log.e("FATAL", msg, e);
            KinosNotificationHandler.appendTextViewText(traceDataView, msg + ": " + e.getMessage());
        }
    }

    private synchronized void executeCommands() throws InterruptedException {
        while (commandQueue.isEmpty()) {
            wait();
        }
        String command = commandQueue.poll();
        if (command != null) {
            sendCommand(command);
        }
    }

    private void establishConnection() throws IOException {
        if (!telnetClient.isConnected()) {
            telnetClient.connect(deviceIp, PORT);
            Thread notificationHandler = new Thread(new KinosNotificationHandler(telnetClient, volumeView, operationStateView, sourceView, traceDataView));
            notificationHandler.start();
            checkDeviceStatus();
        }
    }

    public void checkDeviceStatus() {
        checkForOperationStatus();
        checkVolume();
        checkInputProfile();
    }

    public void checkForOperationStatus() {
        queueCommand("$STANDBY ?$");
    }

    public void checkVolume() {
        queueCommand("$VOLUME ?$");
    }

    public void checkInputProfile() {
        queueCommand("$INPUT PROFILE ?$");
    }

    public void switchOn() {
        queueCommand("STANDBY OFF$");
    }

    public void switchOff() {
        queueCommand("STANDBY ON$");
    }

    public void decreaseVolume() {
        queueCommand("$VOLUME -$");
    }

    public void increaseVolume() {
        queueCommand("$VOLUME +$");
    }

    public void previousInputProfile() {
        queueCommand("$INPUT PROFILE -$");
    }

    public void nextInputProfile() {
        queueCommand("$INPUT PROFILE +$");
    }

    private void queueCommand(String commmand) {
        commandQueue.add(commmand);
    }

    private void sendCommand(String commandString) {
        try {
            establishConnection();
            OutputStream outputStream = telnetClient.getOutputStream();
            outputStream.write((commandString + "\n").getBytes("UTF-8"));
            outputStream.flush();
        } catch (IOException ex) {
            String msg = "Error sending command '" + commandString + "'";
            Log.e("FATAL", msg, ex);
            KinosNotificationHandler.appendTextViewText(traceDataView, msg + ": " + ex.getMessage());
            try {
                telnetClient.disconnect();
            } catch (IOException ex2) {
                String msg2 = "Error closing connection";
                Log.w("WARN", msg2, ex2);
                KinosNotificationHandler.appendTextViewText(traceDataView, msg2 + ": " + ex2.getMessage());
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

    public String getIpAddress() {
        return deviceIp;
    }

    public void setContext(String deviceIp, TextView volumeView, TextView operationStateView, TextView sourceView, TextView traceDataView) {
        this.deviceIp = deviceIp;
        this.volumeView = volumeView;
        this.operationStateView = operationStateView;
        this.sourceView = sourceView;
        this.traceDataView = traceDataView;
    }
}
