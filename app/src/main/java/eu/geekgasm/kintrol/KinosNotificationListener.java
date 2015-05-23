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

public interface KinosNotificationListener {

    public static final String NOT_AVAILABLE = "---";
    public static final String OPERATIONAL_STATUS_TEXT = "Operational";
    public static final String NOT_CONNECTED_STATUS_TEXT = "Not Connected";
    public static final String STANDBY_STATUS_TEXT = "Standby";

    void handleOperationStatusUpdate(String operationState);

    void handleVolumeUpdate(String volumeValue);

    void handleSourceUpdate(String sourceName);

    void handlePowerCounterUpdate(String powerCounterValue);

    void handleDeviceIdUpdate(String deviceId);

    void handleSoftwareVersionUpdate(String softwareVersion);

    void handleSurroundModeUpdate(String currentSurroundMode);

    void handleNoConnectionStatusUpdate();
}
