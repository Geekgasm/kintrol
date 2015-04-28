package de.thegrate.kintrol;

/**
 * Created by d037698 on 4/26/15.
 */
public class DeviceInfo {
    private final String ipAddress;
    private final String deviceName;

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
}
