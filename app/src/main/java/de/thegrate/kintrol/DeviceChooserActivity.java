package de.thegrate.kintrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.Gson;


public class DeviceChooserActivity extends ActionBarActivity {

    private static final String TAG = DeviceChooserActivity.class.getSimpleName();

    public static final String EXTRA_IP_ADDRESS = "de.thegrate.kintrol.IP_ADDRESS";
    public static final String EXTRA_DEVICE_NAME = "de.thegrate.kintrol.DEVICE_NAME";
    public static final String DEVICES_PREF_KEY = "Devices";

    private DeviceInfo[] deviceList;
    private ListView deviceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_choser);
        deviceListView = (ListView) findViewById(R.id.deviceListView);
        loadDeviceList();
        ListAdapter deviceInfoAdapter = new ArrayAdapter<DeviceInfo>(this, R.layout.devicerow, deviceList);
        deviceListView.setAdapter(deviceInfoAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceInfo deviceInfo = (DeviceInfo) parent.getItemAtPosition(position);
                startControlActivity(deviceInfo);
            }
        });
    }

    private void loadDeviceList() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String deviceJson = preferences.getString(DEVICES_PREF_KEY, null);
        if (deviceJson == null) {
            deviceJson = initializeDeviceList();
        }
        Gson gson = new Gson();
        deviceList = gson.fromJson(deviceJson, DeviceInfo[].class);
    }

    private String initializeDeviceList() {
        String initialDeviceList = "[{'deviceName':'Kinos Heimkino','ipAddress':'192.168.178.77'}," +
                "{'deviceName':'Kinos Andy','ipAddress':'192.168.178.81'}]".replaceAll("'", "\"");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DEVICES_PREF_KEY, initialDeviceList);
        editor.commit();
        return initialDeviceList;
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startControlActivity(DeviceInfo deviceInfo) {
        Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(EXTRA_IP_ADDRESS, deviceInfo.getIpAddress());
        intent.putExtra(EXTRA_DEVICE_NAME, deviceInfo.getDeviceName());
        startActivity(intent);
    }

}
