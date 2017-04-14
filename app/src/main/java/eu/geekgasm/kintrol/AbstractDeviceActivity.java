/*
 Kintrol: Remote control app for LINN(R) KINOS(TM), KISTO(TM) and
 Klimax Kontrol(TM) system controllers.
 Copyright (C) 2015-2017 Oliver Götz

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

import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public abstract class AbstractDeviceActivity extends ActionBarActivity {
    public void showAbout(MenuItem item) {
        AboutDialog.showAbout(this);
    }

    String[] getDiscreteVolumes(EditText discreteVolumeText) {
        String discreteVolume = discreteVolumeText.getText().toString();
        if (discreteVolume == null) return null;
        try {
            Integer.parseInt(discreteVolume);
        } catch (NumberFormatException e) {
            return null;
        }
        return new String[]{discreteVolume};
    }

}
