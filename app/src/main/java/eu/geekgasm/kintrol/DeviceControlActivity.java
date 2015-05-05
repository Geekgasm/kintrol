package eu.geekgasm.kintrol;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DeviceControlActivity extends ActionBarActivity implements KinosNotificationListener {

    public static final String EXTRA_IP_ADDRESS = "eu.geekgasm.kintrol.IP_ADDRESS";
    public static final String EXTRA_DEVICE_NAME = "eu.geekgasm.kintrol.DEVICE_NAME";
    private final DeviceInfoPersistenceHandler deviceListPersistor = new DeviceInfoPersistenceHandler(this);
    private Handler handler;
    private TextView deviceNameView;
    private TextView volumeView;
    private TextView operationStateView;
    private TextView sourceView;
    private KinosKontrollerThread kontrollerThread;
    private DeviceInfo deviceInfo;
    private String powerCounterValue;
    private String deviceId;
    private String softwareVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        Intent intent = getIntent();
        deviceInfo = new DeviceInfo(intent.getStringExtra(EXTRA_IP_ADDRESS), intent.getStringExtra(EXTRA_DEVICE_NAME));

        startKontrollerThread(deviceInfo);

        handler = new Handler();

        deviceNameView = (TextView) findViewById(R.id.device_name);
        volumeView = (TextView) findViewById(R.id.volume);
        operationStateView = (TextView) findViewById(R.id.operation_state);
        sourceView = (TextView) findViewById(R.id.current_source);

        deviceNameView.setText(deviceInfo.deviceName);
    }

    private void startKontrollerThread(DeviceInfo deviceInfo) {
        if (kontrollerThread != null)
            kontrollerThread.requestStop();
        kontrollerThread = new KinosKontrollerThread(deviceInfo.ipAddress, this);
        kontrollerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
                volumeView.setText(volumeValue);
            }
        });
    }

    @Override
    public void handleSourceUpdate(final String sourceName) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                sourceView.setText(sourceName);
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
        final EditText deviceNameText = (EditText) promptsView.findViewById(R.id.edit_device_name);
        deviceNameText.setText(deviceInfo.deviceName);
        final EditText ipAddressText = (EditText) promptsView.findViewById(R.id.edit_ip_address);
        ipAddressText.setText(deviceInfo.ipAddress);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(promptsView)
                .setTitle("Edit Device")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DeviceInfo newDevice = new DeviceInfo(ipAddressText.getText().toString(), deviceNameText.getText().toString());
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
        intent.putExtra(EXTRA_DEVICE_NAME, deviceInfo.getDeviceName());
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

}