package de.thegrate.kintrol;

import android.util.Log;
import android.widget.TextView;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by d037698 on 4/26/15.
 */
public class KinosNotificationHandler implements Runnable {
    private final TelnetClient telnetClient;
    private TextView volumeView;
    private TextView operationStateView;
    private TextView sourceView;
    private TextView traceDataView;

    public KinosNotificationHandler(TelnetClient telnetClient, TextView volumeView, TextView operationStateView, TextView sourceView, TextView traceDataView) {
        this.telnetClient = telnetClient;
        this.volumeView = volumeView;
        this.operationStateView = operationStateView;
        this.sourceView = sourceView;
        this.traceDataView = traceDataView;
    }

    @Override
    public void run() {
        InputStream instr = telnetClient.getInputStream();

        try {
            byte[] buff = new byte[1024];
            int ret_read = 0;

            do {
                ret_read = instr.read(buff);
                if (ret_read > 0) {
                    String deviceData = new String(buff, 0, ret_read);
                    updateDeviceState(deviceData);
                }
            }
            while (ret_read >= 0);
        } catch (IOException e) {
            String msg = "Error in KinosNotificationHandler Thread, Exception while reading socket:";
            Log.e("FATAL", msg, e);
            appendTextViewText(traceDataView, msg + ": " + e.getMessage());
        }

        try {
            telnetClient.disconnect();
        } catch (IOException e) {
            String msg = "Error in KinosNotificationHandler Thread, Exception while closing telnet:";
            Log.e("FATAL", msg, e);
            appendTextViewText(traceDataView, msg + ": " + e.getMessage());
        }
    }


    private void updateDeviceState(String deviceData) {
        updateVolumeStatus(deviceData);
        updateInputProfileStatus(deviceData);
        updateStandbyStatus(deviceData);
    }

    static final Pattern VOLUME_STATUS_PATTERN = Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$VOLUME ([^\\$]+)\\$.*", Pattern.DOTALL);

    private boolean updateVolumeStatus(String deviceData) {
        Matcher matcher = VOLUME_STATUS_PATTERN.matcher(deviceData);
        if (matcher.matches()) {
            String volume = matcher.group(2);
            setTextViewText(volumeView, volume);
            return true;
        }
        return false;
    }

    static final Pattern INPUT_PROFILE_STATUS_PATTERN = Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$INPUT PROFILE (\\d+) \\(([^\\$]+)\\)\\$.*", Pattern.DOTALL);

    private boolean updateInputProfileStatus(String deviceData) {
        Matcher matcher = INPUT_PROFILE_STATUS_PATTERN.matcher(deviceData);
        if (matcher.matches()) {
            String inputProfileName = matcher.group(3);
            setTextViewText(sourceView, inputProfileName);
            return true;
        }
        return false;
    }

    private static final Pattern STANDBY_STATUS_PATTERN = Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$STANDBY ([^\\$]+)\\$.*", Pattern.DOTALL);
    private static final Map<String, String> STANDBY_TO_OPERATION_STATUS = new HashMap<String, String>() {{
        put("OFF", "Operational");
        put("ON", "Standby");
    }};

    private boolean updateStandbyStatus(String deviceData) {
        Matcher matcher = STANDBY_STATUS_PATTERN.matcher(deviceData);
        if (matcher.matches()) {
            String standbyStatus = matcher.group(2);
            String operationStatus = STANDBY_TO_OPERATION_STATUS.get(standbyStatus);
            setTextViewText(operationStateView, operationStatus != null ? operationStatus : "Unknown");
            return true;
        }
        return false;
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
