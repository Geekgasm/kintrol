package de.thegrate.kintrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class DeviceChooserActivity extends ActionBarActivity {

    public static final String EXTRA_IP_ADDRESS = "de.thegrate.kintrol.IP_ADDRESS";
    public static final String EXTRA_DEVICE_NAME = "de.thegrate.kintrol.DEVICE_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_choser);
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

    public void choseDevice1(View view) {
        startControlActivity(new DeviceInfo("192.168.178.77", "Kinos Heimkino"));
    }

    public void choseDevice2(View view) {
        startControlActivity(new DeviceInfo("192.168.178.81", "Kinos Andy"));
    }

    private void startControlActivity(DeviceInfo deviceInfo) {
        Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(EXTRA_IP_ADDRESS, deviceInfo.getIpAddress());
        intent.putExtra(EXTRA_DEVICE_NAME, deviceInfo.getDeviceName());
        startActivity(intent);
    }

}
