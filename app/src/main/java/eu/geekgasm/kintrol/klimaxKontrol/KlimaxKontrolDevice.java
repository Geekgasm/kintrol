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
package eu.geekgasm.kintrol.klimaxKontrol;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

import eu.geekgasm.kintrol.Device;
import eu.geekgasm.kintrol.KommandKey;
import eu.geekgasm.kintrol.ResponseValueKey;

public class KlimaxKontrolDevice implements Device {

    private static final Map<KommandKey, String> COMMANDS = new EnumMap<KommandKey, String>(KommandKey.class);
    static {
        COMMANDS.put(KommandKey.checkVolume, "VOL ?");
        COMMANDS.put(KommandKey.checkMuteStatus, "MUTE ?");
        COMMANDS.put(KommandKey.checkInputProfile, "LISTEN ?");
        COMMANDS.put(KommandKey.checkInputName, "INPUT %s NAME ?");
        COMMANDS.put(KommandKey.checkPowerCounter, "COUNTER POWER ?");
        COMMANDS.put(KommandKey.checkSoftwareVersion, "VERSION SOFTWARE ?");
        COMMANDS.put(KommandKey.checkSoftwareVersion, "VERSION HARDWARE ?");
        COMMANDS.put(KommandKey.checkOperationStatus, "STANDBY ?");
        COMMANDS.put(KommandKey.switchOn, "STANDBY N");
        COMMANDS.put(KommandKey.switchOff, "STANDBY Y");
        COMMANDS.put(KommandKey.decreaseVolume, "VOL -");
        COMMANDS.put(KommandKey.increaseVolume, "VOL +");
        COMMANDS.put(KommandKey.previousInputProfile, "LISTEN -");
        COMMANDS.put(KommandKey.nextInputProfile, "LISTEN +");
        COMMANDS.put(KommandKey.muteOn, "MUTE Y");
        COMMANDS.put(KommandKey.muteOff, "MUTE N");
        COMMANDS.put(KommandKey.setVolume, "VOL = ");
    }

    private static final Map<ResponseValueKey, Pattern> RESPONSE_PATTERNS = new EnumMap<ResponseValueKey, Pattern>(ResponseValueKey.class);
    static {
        RESPONSE_PATTERNS.put(ResponseValueKey.VOLUME_STATUS, compile("VOL ([^\\$]+)"));
        RESPONSE_PATTERNS.put(ResponseValueKey.MUTE_STATUS, compile("MUTE ([^\\$]+)"));
        RESPONSE_PATTERNS.put(ResponseValueKey.INPUT_PROFILE_STATUS, compile("LISTEN (\\d+)"));
        RESPONSE_PATTERNS.put(ResponseValueKey.INPUT_NAME, compile("INPUT \\d+ NAME ([^\\$]+)"));
        RESPONSE_PATTERNS.put(ResponseValueKey.POWER_COUNTER, compile("COUNTER POWER ([^\\$]+)"));
        RESPONSE_PATTERNS.put(ResponseValueKey.SOFTWARE_VERSION, compile("VERSION (SOFTWARE [^\\$]+)"));
        RESPONSE_PATTERNS.put(ResponseValueKey.HARDWARE_VERSION, compile("VERSION (HARDWARE [^\\$]+)"));
        RESPONSE_PATTERNS.put(ResponseValueKey.STANDBY_STATUS, compile("STANDBY ([^\\$]+)"));
    }

    private static Pattern compile(String responsePattern) {
        return Pattern.compile(".*\\!?(\\#[^#]*\\#)?\\$"+responsePattern+"\\$.*", Pattern.DOTALL);
    }

    @Override
    public String getDeviceName() {
        return "klimax kontrol";
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
        return false;
    }
}
