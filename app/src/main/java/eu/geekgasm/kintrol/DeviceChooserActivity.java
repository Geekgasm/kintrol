/*
 Kintrol: Remote control app for LINN(R) KINOS(TM), KISTO(TM) and
 Klimax Kontrol(TM) system controllers.
 Copyright (C) 2015-2017 Oliver Götz

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

public class DeviceChooserActivity extends AbstractDeviceActivity {

    private static final String TAG = DeviceChooserActivity.class.getSimpleName();

    private final List<DeviceInfo> deviceList = new ArrayList<>();
    private final DeviceInfoPersistenceHandler deviceListPersistor = new DeviceInfoPersistenceHandler(this);
    private ListView deviceListView;
    private ArrayAdapter<DeviceInfo> deviceInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * catch unexpected error
         */
        Thread.setDefaultUncaughtExceptionHandler(new FatalityHander());

        setContentView(R.layout.activity_device_choser);
        deviceListView = (ListView) findViewById(R.id.deviceListView);
        new DeviceInfoPersistenceHandler(this).loadDeviceList(deviceList);
        Log.i(TAG, "Loaded device list: " + deviceList.toString());
        deviceInfoAdapter = new ArrayAdapter<DeviceInfo>(this, R.layout.devicerow, deviceList);
        deviceListView.setAdapter(deviceInfoAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceInfo deviceInfo = (DeviceInfo) parent.getItemAtPosition(position);
                startControlActivity(deviceInfo);
            }
        });
        if (deviceList.isEmpty()) {
            openAddDeviceDialog(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_choser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_device) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startControlActivity(DeviceInfo deviceInfo) {
        Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRA_IP_ADDRESS, deviceInfo.getIpAddress());
        intent.putExtra(DeviceControlActivity.EXTRA_PORT, deviceInfo.getPort());
        intent.putExtra(DeviceControlActivity.EXTRA_DEVICE_TYPE, deviceInfo.getDeviceType());
        intent.putExtra(DeviceControlActivity.EXTRA_DEVICE_NAME, deviceInfo.getDeviceName());
        intent.putExtra(DeviceControlActivity.EXTRA_DEVICE_VOLUMES, deviceInfo.getDiscreteVolumeValues());
        startActivity(intent);
    }

    public void openAddDeviceDialog(MenuItem item) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.fragment_dialog_edit_device, null);
        final RadioGroup deviceTypeGroup = (RadioGroup) promptsView.findViewById(R.id.device_type_group);
        deviceTypeGroup.check(DeviceDirectory.getDevice(null).getDeviceTypeRadioButtonId());
        final EditText deviceNameText = (EditText) promptsView.findViewById(R.id.edit_device_name);
        final EditText ipAddressText = (EditText) promptsView.findViewById(R.id.edit_ip_address);
        final EditText portText = (EditText) promptsView.findViewById(R.id.edit_port);
        final EditText discreteVolumeText = (EditText) promptsView.findViewById(R.id.edit_discrete_volume);
        final ArrayAdapter<DeviceInfo> adapter = deviceInfoAdapter;
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(promptsView)
                .setTitle("New Device")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeviceInfo newDevice = new DeviceInfo(
                                ipAddressText.getText().toString(),
                                portText.getText().toString(),
                                DeviceDirectory.getDeviceById(deviceTypeGroup.getCheckedRadioButtonId()).getDeviceName(),
                                deviceNameText.getText().toString(),
                                getDiscreteVolumes(discreteVolumeText));
                        deviceList.add(newDevice);
                        deviceListPersistor.saveDeviceList(deviceList);
                        adapter.notifyDataSetChanged();
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

}
