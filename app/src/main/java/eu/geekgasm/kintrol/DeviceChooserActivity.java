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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DeviceChooserActivity extends ActionBarActivity {

    private static final String TAG = DeviceChooserActivity.class.getSimpleName();

    private final List<DeviceInfo> deviceList = new ArrayList<>();
    private final DeviceInfoPersistenceHandler deviceListPersistor = new DeviceInfoPersistenceHandler(this);
    private ListView deviceListView;
    private ArrayAdapter<DeviceInfo> deviceInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_choser);
        deviceListView = (ListView) findViewById(R.id.deviceListView);
        new DeviceInfoPersistenceHandler(this).loadDeviceList(deviceList);
        Log.d(TAG, "Loaded device list: " + deviceList.toString());
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
        intent.putExtra(DeviceControlActivity.EXTRA_DEVICE_NAME, deviceInfo.getDeviceName());
        startActivity(intent);
    }

    public void openAddDeviceDialog(MenuItem item) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.fragment_dialog_edit_device, null);
        final EditText deviceNameText = (EditText) promptsView.findViewById(R.id.edit_device_name);
        final EditText ipAddressText = (EditText) promptsView.findViewById(R.id.edit_ip_address);
        final ArrayAdapter<DeviceInfo> adapter = deviceInfoAdapter;
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(promptsView)
                .setTitle("New Device")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeviceInfo newDevice = new DeviceInfo(ipAddressText.getText().toString(), deviceNameText.getText().toString());
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

    public void showAbout(MenuItem item) {
        showAbout(this);
    }

    static void showAbout(Activity activity) {
        View messageView = activity.getLayoutInflater().inflate(R.layout.about, null, false);

        TextView descriptionView = (TextView) messageView.findViewById(R.id.app_description);
        descriptionView.setText(Html.fromHtml(activity.getString(R.string.app_descrip)));
        TextView creditsView = (TextView) messageView.findViewById(R.id.about_credits);
        creditsView.setMovementMethod(LinkMovementMethod.getInstance());
        creditsView.setText(Html.fromHtml(activity.getString(R.string.app_credits)));

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

}
