/*
 Kintrol: Remote control app for LINN(R) KINOS(TM), KISTO(TM) and
 Klimax Kontrol(TM) system controllers.
 Copyright (C) 2015-2018 Oliver Götz

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

import java.util.Arrays;

public class DeviceInfo {
    public String deviceType;
    public String deviceName;
    public String ipAddress;
    public String port;
    public String[] discreteVolumeValues;
    public String probeCycleMillis;
    public String reconnectDelayMillis;

    public DeviceInfo() {
    }

    public DeviceInfo(String ipAddress, String port, String deviceType, String deviceName,
                      String probeCycleMillis, String reconnectDelayMillis, String... discreteVolumeValues) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.deviceType = deviceType;
        this.deviceName = deviceName;
        this.probeCycleMillis = probeCycleMillis;
        this.reconnectDelayMillis = reconnectDelayMillis;
        this.discreteVolumeValues = discreteVolumeValues;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getPort() {
        return port;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public int getDeviceTypeId() {
        return DeviceDirectory.getDevice(deviceType).getDeviceTypeRadioButtonId();
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String[] getDiscreteVolumeValues() {
        return discreteVolumeValues;
    }

    public String getProbeCycleMillis() {
        return probeCycleMillis;
    }

    public String getReconnectDelayMillis() {
        return reconnectDelayMillis;
    }

    public int getFirstDiscreteVolumeValue() {
        int volume = -1;
        if (discreteVolumeValues != null && discreteVolumeValues.length > 0) {
            String volumeString = discreteVolumeValues[0];
            if (volumeString != null) {
                try {
                    volume = Integer.parseInt(volumeString);
                } catch (NumberFormatException e) {
                    volume = -1;
                }
            }
        }
        return volume;
    }

    @Override
    public String toString() {
        return deviceName.toUpperCase();
//        return "DeviceInfo{" +
//                "deviceName='" + deviceName + '\'' +
//                ", ipAddress='" + ipAddress + '\'' +
//                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceInfo that = (DeviceInfo) o;

        return deviceName != null ? deviceName.equals(that.deviceName) : that.deviceName == null;
    }

    @Override
    public int hashCode() {
        return deviceName != null ? deviceName.hashCode() : 0;
    }
}
