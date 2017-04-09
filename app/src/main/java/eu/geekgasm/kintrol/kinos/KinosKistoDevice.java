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

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

import eu.geekgasm.kintrol.Device;
import eu.geekgasm.kintrol.KommandKey;
import eu.geekgasm.kintrol.ResponseValueKey;

public abstract class KinosKistoDevice implements Device {

    private static final Map<KommandKey, String> COMMANDS = new EnumMap<KommandKey, String>(KommandKey.class);
    static {
        COMMANDS.put(KommandKey.checkVolume, "VOLUME ?");
        COMMANDS.put(KommandKey.checkMuteStatus, "MUTE ?");
        COMMANDS.put(KommandKey.checkInputProfile, "INPUT PROFILE ?");
        COMMANDS.put(KommandKey.checkSurroundMode, "SURROUND ?");
        COMMANDS.put(KommandKey.checkDeviceId, "ID ?");
        COMMANDS.put(KommandKey.checkPowerCounter, "COUNTER POWER ?");
        COMMANDS.put(KommandKey.checkSoftwareVersion, "VERSION SOFTWARE ?");
        COMMANDS.put(KommandKey.checkOperationStatus, "STANDBY ?");
        COMMANDS.put(KommandKey.switchOn, "STANDBY OFF");
        COMMANDS.put(KommandKey.switchOff, "STANDBY ON");
        COMMANDS.put(KommandKey.decreaseVolume, "VOLUME -");
        COMMANDS.put(KommandKey.increaseVolume, "VOLUME +");
        COMMANDS.put(KommandKey.previousInputProfile, "INPUT PROFILE -");
        COMMANDS.put(KommandKey.nextInputProfile, "INPUT PROFILE +");
        COMMANDS.put(KommandKey.toggleMute, "MUTE TOGGLE");
        COMMANDS.put(KommandKey.setVolume, "VOLUME = ");
        COMMANDS.put(KommandKey.previousSurroundMode, "SURROUND -");
        COMMANDS.put(KommandKey.nextSurroundMode, "SURROUND +");
    }

    private static final Map<ResponseValueKey, Pattern> RESPONSE_PATTERNS = new EnumMap<ResponseValueKey, Pattern>(ResponseValueKey.class);
    static {
        RESPONSE_PATTERNS.put(ResponseValueKey.VOLUME_STATUS, compile("VOLUME ([^\\$]+)"));
        RESPONSE_PATTERNS.put(ResponseValueKey.MUTE_STATUS, compile("MUTE ([^\\$]+)"));
        RESPONSE_PATTERNS.put(ResponseValueKey.INPUT_PROFILE_STATUS, compile("INPUT PROFILE (\\d+) \\(([^\\$]+)\\)"));
        RESPONSE_PATTERNS.put(ResponseValueKey.SURROUND_MODE, compile("SURROUND (\\d+)[\\d\\s]*"));
        RESPONSE_PATTERNS.put(ResponseValueKey.DEVICE_ID, compile("ID ([^\\$]+)"));
        RESPONSE_PATTERNS.put(ResponseValueKey.POWER_COUNTER, compile("COUNTER POWER ([^\\$]+)"));
        RESPONSE_PATTERNS.put(ResponseValueKey.SOFTWARE_VERSION, compile("VERSION (SOFTWARE [^\\$]+)"));
        RESPONSE_PATTERNS.put(ResponseValueKey.STANDBY_STATUS, compile("STANDBY ([^\\$]+)"));
    }

    private static Pattern compile(String responsePattern) {
        return Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$"+responsePattern+"\\$.*", Pattern.DOTALL);
    }

    @Override
    public Map<KommandKey, String> getCommands() {
        return Collections.unmodifiableMap(COMMANDS);
    }

    @Override
    public Map<ResponseValueKey, Pattern> getResponsePatterns() {
        return Collections.unmodifiableMap(RESPONSE_PATTERNS);
    }

    @Override
    public boolean hasSurround() {
        return true;
    }
}
