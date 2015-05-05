package eu.geekgasm.kintrol;

/**
 * Created by d037698 on 5/1/15.
 */
public interface KinosStatusChecker {
    public void checkDeviceStatus(long delayMillis);

    public void checkDeviceStatus();

    public void checkForOperationStatus();

    public void checkVolume();

    public void checkInputProfile();

    public void checkSurroundMode();
}
