package eu.geekgasm.kintrol;

public class DeviceStatus {

    private String currentVolume;
    private boolean isMuted = false;
    private boolean isOperational = false;
    private boolean isUnityGainOn = false;

    public DeviceStatus() {
        this.currentVolume = NotificationListener.NOT_AVAILABLE;
    }

    public synchronized String getCurrentVolume() {
        return currentVolume;
    }

    public synchronized void setCurrentVolume(String currentVolume) {
        this.currentVolume = currentVolume;
    }

    public synchronized boolean isMuted() {
        return isMuted;
    }

    public synchronized void setMuted(boolean muted) {
        isMuted = muted;
    }


    public synchronized boolean isOperational() {
        return isOperational;
    }

    public synchronized void setOperational(boolean operational) {
        isOperational = operational;
    }


    public synchronized boolean isUnityGainOn() {
        return isUnityGainOn;
    }

    public synchronized void setUnityGainOn(boolean unityGainOn) {
        isUnityGainOn = unityGainOn;
    }

}