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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceInfoPersistenceHandler {
    public static final String DEVICES_PREF_KEY = "Devices";
    private Context context;

    public DeviceInfoPersistenceHandler(Context context) {
        this.context = context;
    }

    public synchronized void loadDeviceList(List<DeviceInfo> deviceList) {
        deviceList.clear();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String deviceJson = preferences.getString(DEVICES_PREF_KEY, null);
        Gson gson = new Gson();
        DeviceInfo[] deviceArray = gson.fromJson(deviceJson, DeviceInfo[].class);
        if (deviceArray != null)
            deviceList.addAll(Arrays.asList(deviceArray));
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
