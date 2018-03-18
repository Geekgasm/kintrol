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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class AboutDialog {
    static void showAbout(Activity activity) {
        View messageView = activity.getLayoutInflater().inflate(R.layout.about, null, false);

        TextView descriptionView = (TextView) messageView.findViewById(R.id.app_description);
        String descriptionText = activity.getString(R.string.app_descrip);
        descriptionView.setText(Html.fromHtml(descriptionText + "Version: " + getAppVersion(activity)));
        TextView creditsView = (TextView) messageView.findViewById(R.id.about_credits);
        creditsView.setMovementMethod(LinkMovementMethod.getInstance());
        creditsView.setText(Html.fromHtml(activity.getString(R.string.app_credits)));

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

    static String getAppVersion(Activity activity) {
        String version;
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "unknown";
        }
        return version;
    }
}
