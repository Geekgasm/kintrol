package eu.geekgasm.kintrol;

/**
 * Created by d037698 on 5/5/15.
 */
public class SurroundModes {
    private static final String[] SURROUND_MODES = new String[42];

    static {
        SURROUND_MODES[0] = "Auto";
        SURROUND_MODES[1] = "Stereo";
        SURROUND_MODES[2] = "Analog Stereo";
        SURROUND_MODES[3] = "SACD Stereo";
        SURROUND_MODES[4] = "DVD-A Stereo";
        SURROUND_MODES[5] = "Stereo Sub";
        SURROUND_MODES[6] = "Analog Stereo Sub";
        SURROUND_MODES[7] = "SACD Stereo Sub";
        SURROUND_MODES[8] = "DVD-A Stereo Sub";
        SURROUND_MODES[9] = "Phantom";
        SURROUND_MODES[10] = "Analog Phantom";
        SURROUND_MODES[11] = "SACD Phantom";
        SURROUND_MODES[12] = "DVD-A Phantom";
        SURROUND_MODES[13] = "3 Stereo";
        SURROUND_MODES[14] = "Analog 3 Stereo";
        SURROUND_MODES[15] = "SACD 3 Stereo";
        SURROUND_MODES[16] = "DVD-A 3 Stereo";
        SURROUND_MODES[17] = "Multi-Channel";
        SURROUND_MODES[18] = "Analog Multi-Channel";
        SURROUND_MODES[19] = "SACD Multi-Channel";
        SURROUND_MODES[20] = "DVD-A Multi-Channel";
        SURROUND_MODES[21] = "Dolby Digital";
        SURROUND_MODES[22] = "Dolby Digital EX";
        SURROUND_MODES[23] = "Dolby Pro Logic II";
        SURROUND_MODES[24] = "Dolby Pro Logic II Music";
        SURROUND_MODES[25] = "Dolby Pro Logic II EX";
        SURROUND_MODES[26] = "Dolby Pro Logic II Music EX";
        SURROUND_MODES[27] = "Dolby Headphones";
        SURROUND_MODES[28] = "Dolby Headphones Room 1";
        SURROUND_MODES[29] = "Dolby Headphones Room 2";
        SURROUND_MODES[30] = "Dolby Headphones Room 3";
        SURROUND_MODES[31] = "DTS CD";
        SURROUND_MODES[32] = "DTS Digital Surround";
        SURROUND_MODES[33] = "DTS ES Matrix";
        SURROUND_MODES[34] = "DTS ES Discrete";
        SURROUND_MODES[35] = "DTS 96/24";
        SURROUND_MODES[36] = "MPEG Stereo";
        SURROUND_MODES[37] = "MPEG Surround";
        SURROUND_MODES[38] = "AAC Stereo";
        SURROUND_MODES[39] = "AAC Surround";
        SURROUND_MODES[40] = "Limbik Party";
        SURROUND_MODES[41] = "Lip Sync";
    }

    public static String renderSurroundModeString(String surroundModeCode) {
        if (surroundModeCode == null || surroundModeCode.equals(KinosNotificationListener.NOT_AVAILABLE))
            return KinosNotificationListener.NOT_AVAILABLE;
        try {
            int code = Integer.parseInt(surroundModeCode);
            if (code >= 0 && code < SURROUND_MODES.length) {
                return SURROUND_MODES[code];
            } else {
                return surroundModeCode;
            }
        } catch (NumberFormatException nfe) {
            return surroundModeCode;
        }
    }
}
