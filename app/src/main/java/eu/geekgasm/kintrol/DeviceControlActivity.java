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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.geekgasm.kintrol.kinos.SurroundModes;


public class DeviceControlActivity extends ActionBarActivity implements NotificationListener {

    public static final String EXTRA_IP_ADDRESS = "eu.geekgasm.kintrol.IP_ADDRESS";
    public static final String EXTRA_PORT = "eu.geekgasm.kintrol.PORT";
    public static final String EXTRA_DEVICE_NAME = "eu.geekgasm.kintrol.DEVICE_NAME";
    public static final String EXTRA_DEVICE_TYPE = "eu.geekgasm.kintrol.DEVICE_TYPE";
    public static final String EXTRA_DEVICE_VOLUMES = "eu.geekgasm.kintrol.DEVICE_VOLUMES";
    private final DeviceInfoPersistenceHandler deviceListPersistor = new DeviceInfoPersistenceHandler(this);
    private Handler handler;
    private TextView deviceNameView;
    private AutoSizeText volumeView;
    private AutoSizeText operationStateView;
    private AutoSizeText sourceView;
    private KontrollerThread kontrollerThread;
    private DeviceInfo deviceInfo;
    private String powerCounterValue;
    private String deviceId;
    private String softwareVersion;
    private AutoSizeText surroundModeView;
    private int discreteVolumeValue;

    private static void runJustBeforeBeingDrawn(final View view, final Runnable runnable) {
        final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                runnable.run();
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        };
        view.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * catch unexpected error
         */
        Thread.setDefaultUncaughtExceptionHandler(new FatalityHander());

        setContentView(R.layout.activity_device_control);
        Intent intent = getIntent();
        deviceInfo = new DeviceInfo(
                intent.getStringExtra(EXTRA_IP_ADDRESS),
                intent.getStringExtra(EXTRA_PORT),
                intent.getStringExtra(EXTRA_DEVICE_TYPE),
                intent.getStringExtra(EXTRA_DEVICE_NAME),
                intent.getStringArrayExtra(EXTRA_DEVICE_VOLUMES));

        handler = new Handler();

        deviceNameView = (TextView) findViewById(R.id.device_name);
        volumeView = new AutoSizeText(this, R.id.volume);
        operationStateView = new AutoSizeText(this, R.id.operation_state);
        sourceView = new AutoSizeText(this, R.id.current_source);
        surroundModeView = new AutoSizeText(this, R.id.current_surround_mode);
        deviceNameView.setText(deviceInfo.deviceName);
        discreteVolumeValue = deviceInfo.getFirstDiscreteVolumeValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Button discreteVolumeButton = (Button) findViewById(R.id.discrete_volume_button);
        if (discreteVolumeValue >= 0) {
            discreteVolumeButton.setText(String.valueOf(discreteVolumeValue));
            discreteVolumeButton.setVisibility(View.VISIBLE);
        } else {
            discreteVolumeButton.setVisibility(View.INVISIBLE);
        }

        int surroundVisibility = new DeviceDirectory().getDevice(deviceInfo.deviceType).hasSurround()
                ? View.VISIBLE
                : View.INVISIBLE;
        View surroundView = findViewById(R.id.surround_group);
        surroundView.setVisibility(surroundVisibility);

        startKontrollerThread(deviceInfo);
    }

    private void setNoConnectionInfo() {
        operationStateView.setText(NotificationListener.NOT_CONNECTED_STATUS_TEXT);
        volumeView.setText(NotificationListener.NOT_AVAILABLE);
        sourceView.setText(NotificationListener.NOT_AVAILABLE);
        surroundModeView.setText(NotificationListener.NOT_AVAILABLE);
    }

    private void startKontrollerThread(DeviceInfo deviceInfo) {
        if (kontrollerThread != null)
            kontrollerThread.requestStop();
        kontrollerThread = new KontrollerThread(deviceInfo, this);
        kontrollerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // request the thread to stop
        kontrollerThread.requestStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_device) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    kontrollerThread.increaseVolume();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    kontrollerThread.decreaseVolume();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    public void decreaseVolume(View view) {
        kontrollerThread.decreaseVolume();
    }

    public void increaseVolume(View view) {
        kontrollerThread.increaseVolume();
    }

    public void switchOn(View view) {
        kontrollerThread.switchOn();
    }

    public void switchOff(View view) {
        kontrollerThread.switchOff();
    }

    public void previousSource(View view) {
        kontrollerThread.previousInputProfile();
    }

    public void nextSource(View view) {
        kontrollerThread.nextInputProfile();
    }

    public void muteToggle(View view) {
        kontrollerThread.toggleMute();
    }

    public void setDiscreteVolume(View view) {
        kontrollerThread.setVolume(discreteVolumeValue);
    }

    public void previousSurroundMode(View view) {
        kontrollerThread.previousSurroundMode();
    }

    public void nextSurroundMode(View view) {
        kontrollerThread.nextSurroundMode();
    }

    @Override
    public void handleOperationStatusUpdate(final String operationState) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                operationStateView.setText(operationState);
            }
        });
    }

    @Override
    public void handleVolumeUpdate(final String volumeValue) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                volumeView.setText(unescapeHexCharacters(volumeValue));
            }
        });
    }

    @Override
    public void handleSourceUpdate(final String sourceName) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                sourceView.setText(unescapeHexCharacters(sourceName));
            }
        });
    }

    private String unescapeHexCharacters(String escapedString) {
        try {
            return URLDecoder.decode(escapedString.replaceAll("\\\\x", "%"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 encoding must always exist
            return escapedString;
        }
    }

    @Override
    public void handleSurroundModeUpdate(final String currentSurroundMode) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                surroundModeView.setText(SurroundModes.renderSurroundModeString(currentSurroundMode));
            }
        });
    }

    @Override
    public void handleNoConnectionStatusUpdate() {
        runJustBeforeBeingDrawn(surroundModeView.getViewGroup(), new Runnable() {
            @Override
            public void run() {
                setNoConnectionInfo();
            }
        });
    }

    @Override
    public void handlePowerCounterUpdate(String powerCounterValue) {
        this.powerCounterValue = powerCounterValue;
    }

    @Override
    public void handleDeviceIdUpdate(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public void handleSoftwareVersionUpdate(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public void openEditDeviceDialog(MenuItem item) {

        final DeviceInfo newDevice = new DeviceInfo();
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.fragment_dialog_edit_device, null);
        final RadioGroup deviceTypeGroup = (RadioGroup) promptsView.findViewById(R.id.device_type_group);
        deviceTypeGroup.check(deviceInfo.getDeviceTypeId());
        final EditText deviceNameText = (EditText) promptsView.findViewById(R.id.edit_device_name);
        deviceNameText.setText(deviceInfo.deviceName);
        final EditText ipAddressText = (EditText) promptsView.findViewById(R.id.edit_ip_address);
        ipAddressText.setText(deviceInfo.ipAddress);
        final EditText portText = (EditText) promptsView.findViewById(R.id.edit_port);
        portText.setText(deviceInfo.port);
        final EditText discreteVolumeText = (EditText) promptsView.findViewById(R.id.edit_discrete_volume);
        if (deviceInfo.discreteVolumeValues != null && deviceInfo.discreteVolumeValues.length > 0)
            discreteVolumeText.setText(deviceInfo.discreteVolumeValues[0]);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(promptsView)
                .setTitle("Edit Device")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DeviceInfo newDevice = new DeviceInfo(
                                ipAddressText.getText().toString(),
                                portText.getText().toString(),
                                DeviceInfo.getDeviceTypeById(DeviceChooserActivity.getDeviceTypeId(deviceTypeGroup)),
                                deviceNameText.getText().toString(),
                                DeviceChooserActivity.getDiscreteVolumes(discreteVolumeText));
                        deviceListPersistor.updateDevice(deviceInfo, newDevice);
                        startControlActivity(newDevice);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        alertDialog.show();
    }

    private void startControlActivity(DeviceInfo deviceInfo) {
        Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(EXTRA_IP_ADDRESS, deviceInfo.getIpAddress());
        intent.putExtra(EXTRA_PORT, deviceInfo.getPort());
        intent.putExtra(EXTRA_DEVICE_TYPE, deviceInfo.getDeviceType());
        intent.putExtra(EXTRA_DEVICE_NAME, deviceInfo.getDeviceName());
        intent.putExtra(EXTRA_DEVICE_VOLUMES, deviceInfo.getDiscreteVolumeValues());
        startActivity(intent);
    }

    public void openDeleteDeviceDialog(MenuItem item) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Device " + deviceInfo.getDeviceName() + "?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deviceListPersistor.deleteDevice(deviceInfo);
                        Intent upIntent = new Intent(DeviceControlActivity.this, DeviceChooserActivity.class);
                        NavUtils.navigateUpTo(DeviceControlActivity.this, upIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        alertDialog.show();
    }

    public void openShowDeviceInfoDialog(MenuItem item) {
        ListView deviceInfoListView = new ListView(this);
        deviceInfoListView.setAdapter(createDeviceInfoListAdapter());
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Device Info")
                .setCancelable(false)
                .setView(deviceInfoListView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        alertDialog.show();
    }

    ListAdapter createDeviceInfoListAdapter() {
        final String[] fromMapKey = new String[]{"key", "value"};
        final int[] toLayoutId = new int[]{android.R.id.text1, android.R.id.text2};
        final List<Map<String, String>> list = new ArrayList<>();
        list.add(createListEntry("Device Name", deviceInfo.deviceName));
        list.add(createListEntry("IP Address", deviceInfo.ipAddress));
        list.add(createListEntry("Device ID", deviceId != null ? deviceId : "unknown"));
        list.add(createListEntry("Total Operation Time", renderOperationTime(powerCounterValue)));
        addSoftwareVersions(list);

        return new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, fromMapKey, toLayoutId);
    }

    private String renderOperationTime(String powerCounterValue) {
        if (powerCounterValue == null)
            return "unknown";
        String[] segments = powerCounterValue.split(":");
        if (segments.length == 4) {
            return String.format("%s days %s hours %s minutes %s seconds", (Object[]) segments);
        }
        return powerCounterValue;
    }

    private void addSoftwareVersions(List<Map<String, String>> list) {
        if (softwareVersion == null) {
            list.add(createListEntry("Software Version", "unknown"));
        } else {
            String[] versions = softwareVersion.split("\\s");
            for (int i = 0; i < versions.length - 1; i += 2) {
                list.add(createListEntry(versions[i] + " version", versions[i + 1]));
            }
        }
    }

    private HashMap<String, String> createListEntry(String key, String value) {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", key);
        map.put("value", value);
        return map;
    }

    public void showAbout(MenuItem item) {
        AboutDialog.showAbout(this);
    }

}
