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

import eu.geekgasm.kintrol.kinos.KinosDevice;
import eu.geekgasm.kintrol.kinos.KistoDevice;
import eu.geekgasm.kintrol.klimaxKontrol.KlimaxKontrolDevice;

public class DeviceDirectory {

    private static String denullify(String inputString) {
        return inputString == null ? "" : inputString;
    }

    public static Device getDevice(String deviceType) {
        switch (denullify(deviceType)) {
            case "kisto":
                return new KistoDevice();
            case "klimax kontrol":
                return new KlimaxKontrolDevice();
            case "kinos":
            default:
                return new KinosDevice();
        }
    }

    public static Device getDeviceById(int id) {
        switch (id) {
            case R.id.radio_kisto:
                return new KistoDevice();
            case R.id.radio_klimax_kontrol:
                return new KlimaxKontrolDevice();
            case R.id.radio_kinos:
            default:
                return new KinosDevice();
        }
    }

}
