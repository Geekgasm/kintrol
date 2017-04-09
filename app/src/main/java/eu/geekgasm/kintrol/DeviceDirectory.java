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

import eu.geekgasm.kintrol.kinos.KinosDevice;
import eu.geekgasm.kintrol.kinos.KistoDevice;
import eu.geekgasm.kintrol.klimaxKontrol.KlimaxKontrolDevice;

public class DeviceDirectory {

    public Device getDevice(String deviceType) {
        switch (deviceType) {
            case "kisto":
                return new KistoDevice();
            case "klimax kontrol":
                return new KlimaxKontrolDevice();
            case "kinos":
            default:
                return new KinosDevice();
        }

    }
}
