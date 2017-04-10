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
package eu.geekgasm.kintrol.kinos;

import eu.geekgasm.kintrol.R;

public class KinosDevice extends KinosKistoDevice {
    @Override
    public String getDeviceName() {
        return "kinos";
    }

    @Override
    public int getDeviceTypeRadioButtonId() {
        return R.id.radio_kinos;
    }
}
