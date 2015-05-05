package eu.geekgasm.kintrol;

/**
 * Created by d037698 on 5/1/15.
 */
public interface KinosNotificationListener {

    public static final String NOT_AVAILABLE = "---";
    public static final String OPERATIONAL_STATUS_TEXT = "Operational";
    public static final String STANDBY_STATUS_TEXT = "Standby";

    void handleOperationStatusUpdate(String operationState);

    void handleVolumeUpdate(String volumeValue);

    void handleSourceUpdate(String sourceName);

    void handlePowerCounterUpdate(String powerCounterValue);

    void handleDeviceIdUpdate(String deviceId);

    void handleSoftwareVersionUpdate(String softwareVersion);

    void handleSurroundModeUpdate(String currentSurroundMode);
}
