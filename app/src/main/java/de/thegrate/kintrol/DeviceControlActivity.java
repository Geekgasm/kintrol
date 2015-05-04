package de.thegrate.kintrol;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;


public class DeviceControlActivity extends ActionBarActivity implements KinosNotificationListener {

    private static final String KONTROLLER = "KINOS_KONTROLLER";

    private Handler handler;
    private TextView volumeView;
    private TextView operationStateView;
    private TextView sourceView;
    private KinosKontrollerThread kontrollerThread;
    private final DeviceInfoPersistenceHandler deviceListPersistor = new DeviceInfoPersistenceHandler(this);
    private DeviceInfo deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        Intent intent = getIntent();
        deviceInfo = new DeviceInfo(intent.getStringExtra(DeviceChooserActivity.EXTRA_IP_ADDRESS), intent.getStringExtra(DeviceChooserActivity.EXTRA_DEVICE_NAME));

        startKontrollerThread(deviceInfo);

        handler = new Handler();

        TextView deviceNameView = (TextView) findViewById(R.id.device_name);
        deviceNameView.setText(deviceInfo.deviceName);

        volumeView = (TextView) findViewById(R.id.volume);
        operationStateView = (TextView) findViewById(R.id.operation_state);
        sourceView = (TextView) findViewById(R.id.current_source);
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
                        DeviceInfo newDevice = new DeviceInfo(ipAddressText.getText().toString(), deviceNameText.getText().toString());
                        deviceListPersistor.updateDevice(deviceInfo, newDevice);
                        deviceInfo = newDevice;
                        startKontrollerThread(deviceInfo);
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

    public void openDeleteDeviceDialog(MenuItem item) {
    }
}
