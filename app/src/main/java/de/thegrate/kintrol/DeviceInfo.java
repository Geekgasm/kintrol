package de.thegrate.kintrol;

/**
 * Created by d037698 on 4/26/15.
 */
public class DeviceInfo {
    public String deviceName;
    public String ipAddress;

    public DeviceInfo() {
    }

    public DeviceInfo(String ipAddress, String deviceName) {
        this.ipAddress = ipAddress;
        this.deviceName = deviceName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public String toString() {
        return deviceName.toUpperCase();
//        return "DeviceInfo{" +
//                "deviceName='" + deviceName + '\'' +
//                ", ipAddress='" + ipAddress + '\'' +
//                '}';
    }
}
