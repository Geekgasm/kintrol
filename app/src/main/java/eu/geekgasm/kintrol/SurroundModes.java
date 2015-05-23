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

import android.util.SparseArray;

public class SurroundModes {
    private static final SparseArray<String> SURROUND_MODES = new SparseArray<>(43);

    static {
        SURROUND_MODES.put(0, "Auto");
        SURROUND_MODES.put(1, "Stereo");
        SURROUND_MODES.put(2, "Analog Stereo");
        SURROUND_MODES.put(3, "SACD Stereo");
        SURROUND_MODES.put(4, "DVD-A Stereo");
        SURROUND_MODES.put(5, "Stereo Sub");
        SURROUND_MODES.put(6, "Analog Stereo Sub");
        SURROUND_MODES.put(7, "SACD Stereo Sub");
        SURROUND_MODES.put(8, "DVD-A Stereo Sub");
        SURROUND_MODES.put(9, "Phantom");
        SURROUND_MODES.put(10, "Analog Phantom");
        SURROUND_MODES.put(11, "SACD Phantom");
        SURROUND_MODES.put(12, "DVD-A Phantom");
        SURROUND_MODES.put(13, "3 Stereo");
        SURROUND_MODES.put(14, "Analog 3 Stereo");
        SURROUND_MODES.put(15, "SACD 3 Stereo");
        SURROUND_MODES.put(16, "DVD-A 3 Stereo");
        SURROUND_MODES.put(17, "Multi-Channel");
        SURROUND_MODES.put(18, "Analog Multi-Channel");
        SURROUND_MODES.put(19, "SACD Multi-Channel");
        SURROUND_MODES.put(20, "DVD-A Multi-Channel");
        SURROUND_MODES.put(21, "Dolby Digital");
        SURROUND_MODES.put(22, "Dolby Digital EX");
        SURROUND_MODES.put(23, "Dolby PLII");
        SURROUND_MODES.put(24, "Dolby PLII Music");
        SURROUND_MODES.put(25, "Dolby PLII EX");
        SURROUND_MODES.put(26, "Dolby PLII Music EX");
        SURROUND_MODES.put(27, "Dolby Headphones");
        SURROUND_MODES.put(28, "Dolby Headphones Room 1");
        SURROUND_MODES.put(29, "Dolby Headphones Room 2");
        SURROUND_MODES.put(30, "Dolby Headphones Room 3");
        SURROUND_MODES.put(31, "DTS CD");
        SURROUND_MODES.put(32, "DTS Digital Surround");
        SURROUND_MODES.put(33, "DTS ES Matrix");
        SURROUND_MODES.put(34, "DTS ES Discrete");
        SURROUND_MODES.put(35, "DTS 96/24");
        SURROUND_MODES.put(36, "MPEG Stereo");
        SURROUND_MODES.put(37, "MPEG Surround");
        SURROUND_MODES.put(38, "AAC Stereo");
        SURROUND_MODES.put(39, "AAC Surround");
        SURROUND_MODES.put(40, "Limbik Party");
        SURROUND_MODES.put(41, "Lip Sync");
        SURROUND_MODES.put(146, "NEO:6 Music");
    }

    public static String renderSurroundModeString(String surroundModeCode) {
        if (surroundModeCode == null || surroundModeCode.equals(KinosNotificationListener.NOT_AVAILABLE))
            return KinosNotificationListener.NOT_AVAILABLE;
        try {
            int code = Integer.parseInt(surroundModeCode);
            String surroundMode = SURROUND_MODES.get(code);
            return surroundMode != null ? surroundMode : surroundModeCode;
        } catch (NumberFormatException nfe) {
            return surroundModeCode;
        }
    }
}
