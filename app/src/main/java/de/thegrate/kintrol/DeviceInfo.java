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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceInfo that = (DeviceInfo) o;

        if (deviceName != null ? !deviceName.equals(that.deviceName) : that.deviceName != null)
            return false;
        return !(ipAddress != null ? !ipAddress.equals(that.ipAddress) : that.ipAddress != null);

    }

    @Override
    public int hashCode() {
        int result = deviceName != null ? deviceName.hashCode() : 0;
        result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
        return result;
    }
}
