package de.thegrate.kintrol;

/**
 * Created by d037698 on 5/1/15.
 */
public interface KinosNotificationListener {
    void handleOperationStatusUpdate(String operationState);

    void handleVolumeUpdate(String volumeValue);

    void handleSourceUpdate(String sourceName);
}
