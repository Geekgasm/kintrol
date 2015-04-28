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
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by d037698 on 4/26/15.
 */
public class KinosKontroller {
    private static final int PORT = 9004;
    private static String deviceIp;
    private static KinosNotificationHandler notificationHandler;
    private static TelnetClient telnetClient;
    private static TextView volumeView;
    private static TextView operationStateView;
    private static TextView sourceView;
    private static TextView traceDataView;

    private static final KinosKontroller theKontroller = new KinosKontroller();

    public static KinosKontroller getKontroller() {
        return theKontroller;
    }

    private KinosKontroller() {
    }

    public void start(String deviceIp, TextView volumeView, TextView operationStateView, TextView sourceView, TextView traceDataView) {
        this.volumeView = volumeView;
        this.operationStateView = operationStateView;
        this.sourceView = sourceView;
        this.traceDataView = traceDataView;
        this.deviceIp = deviceIp;
        disconnectTelnetClient();
        try {
            createTelnetClient();
            establishConnection();
        } catch (Exception e) {
            trace("FATAL", "Error starting KinosKontroller", e);
        }

    }

    private void disconnectTelnetClient() {
        if (telnetClient != null && telnetClient.isConnected()) {
            try {
                telnetClient.disconnect();
            } catch (IOException e) {
                trace("WARNING", "Error disconnecting Telnet client: ", e);
            }
        }
    }

    private void establishConnection() throws IOException {
        if (!telnetClient.isConnected()) {
            new AsyncTask<String, Integer, Boolean>() {
                @Override
                protected Boolean doInBackground(String... params) {
                    try {
                        telnetClient.connect(deviceIp, PORT);
                        Thread notificationHandler = new Thread(new KinosNotificationHandler(telnetClient));
                        notificationHandler.start();
                        checkDeviceStatus();
                    } catch (IOException e) {
                        trace("ERROR", "Unable to open telnet connection to " + deviceIp + ":" + PORT, e);
                    }
                    return null;
                }
            }.execute();
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

    private void queueCommand(String... commands) {
        new SendCommandTask().execute(commands);
    }

    private synchronized void sendCommand(String commandString) {
        try {
            establishConnection();
            OutputStream outputStream = telnetClient.getOutputStream();
            outputStream.write((commandString + "\n").getBytes("UTF-8"));
            outputStream.flush();
        } catch (IOException ex) {
            trace("FATAL", "Error sending command '" + commandString + "'", ex);
            try {
                telnetClient.disconnect();
            } catch (IOException ex2) {
                trace("WARNING", "Error closing connection", ex2);
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


    class SendCommandTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... commands) {
            for (String command : commands) {
                sendCommand(command);
            }
            return true;
        }
    }

    static void updateVolumeView(String volume) {
        setTextViewText(volumeView, volume);
    }

    static void updateOperationStateView(String operationState) {
        setTextViewText(operationStateView, operationState);
    }

    static void updateSourceView(String source) {
        setTextViewText(sourceView, source);
    }

    static void trace(String tag, String message, Throwable throwable) {
        Log.w(tag, message, throwable);
        appendTextViewText(traceDataView, tag + ": " + message + (throwable != null ? ": " + throwable.getMessage() : ""));
    }

    static void setTextViewText(final TextView textView, final String displayText) {
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(displayText);
            }
        });
    }

    static void appendTextViewText(final TextView textView, final String displayText) {
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.append(displayText + "\n");
            }
        });
    }
}
