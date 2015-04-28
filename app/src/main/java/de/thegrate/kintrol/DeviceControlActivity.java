package de.thegrate.kintrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class DeviceControlActivity extends ActionBarActivity {

    private static final String KONTROLLER = "KINOS_KONTROLLER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String deviceName = intent.getStringExtra(DeviceChooserActivity.EXTRA_DEVICE_NAME);
        TextView deviceNameView = getTextView(R.id.device_name);
        deviceNameView.setText(deviceName);
        String ipAddress = intent.getStringExtra(DeviceChooserActivity.EXTRA_IP_ADDRESS);
        KinosKontroller.getKontroller().start(ipAddress, getTextView(R.id.volume), getTextView(R.id.operation_state), getTextView(R.id.current_source), getTextView(R.id.traceData));
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void decreaseVolume(View view) {
        KinosKontroller.getKontroller().decreaseVolume();
    }

    public void increaseVolume(View view) {
        KinosKontroller.getKontroller().increaseVolume();
    }

    public void switchOn(View view) {
        KinosKontroller.getKontroller().switchOn();
    }

    public void switchOff(View view) {
        KinosKontroller.getKontroller().switchOff();
    }

    public void previousSource(View view) {
        KinosKontroller.getKontroller().previousInputProfile();
    }

    public void nextSource(View view) {
        KinosKontroller.getKontroller().nextInputProfile();
    }

    private TextView getTextView(int textViewId) {
        setContentView(R.layout.activity_device_control);
        return (TextView) findViewById(textViewId);
    }
}
