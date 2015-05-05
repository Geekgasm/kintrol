package eu.geekgasm.kintrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by d037698 on 5/3/15.
 */
public class DeviceInfoPersistenceHandler {
    public static final String DEVICES_PREF_KEY = "Devices";
    private Context context;

    public DeviceInfoPersistenceHandler(Context context) {
        this.context = context;
    }

    public synchronized void loadDeviceList(List<DeviceInfo> deviceList) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String deviceJson = preferences.getString(DEVICES_PREF_KEY, null);
        if (deviceJson == null) {
            deviceJson = initializeDeviceList(context);
        }
        Gson gson = new Gson();
        DeviceInfo[] deviceArray = gson.fromJson(deviceJson, DeviceInfo[].class);
        deviceList.clear();
        deviceList.addAll(Arrays.asList(deviceArray));
    }

    private String initializeDeviceList(Context context) {
//        String initialDeviceList = "[{'deviceName':'Kinos Heimkino','ipAddress':'192.168.178.77'}," +
//                "{'deviceName':'Kinos Andy','ipAddress':'192.168.178.81'}]".replaceAll("'", "\"");
        String initialDeviceList = "[]";
        save(context, initialDeviceList);
        return initialDeviceList;
    }

    private void save(Context context, String deviceListString) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DEVICES_PREF_KEY, deviceListString);
        editor.commit();
    }

    public synchronized void saveDeviceList(List<DeviceInfo> deviceList) {
        String deviceListString = new Gson().toJson(deviceList);
        save(context, deviceListString);
    }

    public void updateDevice(DeviceInfo oldDevice, DeviceInfo newDevice) {
        List<DeviceInfo> deviceInfos = new ArrayList<>();
        loadDeviceList(deviceInfos);
        int index = deviceInfos.indexOf(oldDevice);
        if (index > -1) {
            deviceInfos.set(index, newDevice);
        } else {
            deviceInfos.add(newDevice);
        }
        saveDeviceList(deviceInfos);
    }

    public void deleteDevice(DeviceInfo deviceInfo) {
        List<DeviceInfo> deviceInfos = new ArrayList<>();
        loadDeviceList(deviceInfos);
        deviceInfos.remove(deviceInfo);
        saveDeviceList(deviceInfos);
    }
}
