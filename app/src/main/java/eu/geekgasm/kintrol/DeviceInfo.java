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

import java.util.Arrays;

public class DeviceInfo {
    public String deviceName;
    public String ipAddress;
    public String[] discreteVolumeValues;

    public DeviceInfo() {
    }

    public DeviceInfo(String ipAddress, String deviceName, String... discreteVolumeValues) {
        this.ipAddress = ipAddress;
        this.deviceName = deviceName;
        this.discreteVolumeValues = discreteVolumeValues;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String[] getDiscreteVolumeValues() {
        return discreteVolumeValues;
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

        if (deviceName != null ? !deviceName.equals(that.deviceName) : that.deviceName != null)
            return false;
        if (ipAddress != null ? !ipAddress.equals(that.ipAddress) : that.ipAddress != null)
            return false;
        return Arrays.equals(discreteVolumeValues, that.discreteVolumeValues);
    }

    @Override
    public int hashCode() {
        int result = deviceName != null ? deviceName.hashCode() : 0;
        result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
        result = 31 * result + (discreteVolumeValues != null ? Arrays.hashCode(discreteVolumeValues) : 0);
        return result;
    }
}
