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
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lb.auto_fit_textview.AutoResizeTextView;

public class AutoSizeText {

    private Activity parentActivity;
    private ViewGroup viewGroup;

    public AutoSizeText(Activity parentActivity, int viewGroupId) {
        this.parentActivity = parentActivity;
        this.viewGroup = (ViewGroup) parentActivity.findViewById(viewGroupId);
    }

    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    public void setText(String text) {
        if (viewGroup.getChildCount() > 0) {
            View firstChild = viewGroup.getChildAt(0);
            if (firstChild instanceof AutoResizeTextView) {
                String oldText = String.valueOf(((TextView) firstChild).getText());
                if (text != null && text.equals(oldText))
                    return;
            }
        }
        viewGroup.removeAllViews();
        final int width = viewGroup.getWidth();
        final int height = viewGroup.getHeight() * 80 / 100;
        final AutoResizeTextView textView = new AutoResizeTextView(parentActivity);
        textView.setGravity(Gravity.CENTER);
        final int maxLinesCount = 2;
        textView.setMaxLines(maxLinesCount);
        textView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, height, parentActivity.getResources().getDisplayMetrics()));
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setEnableSizeCache(false);
        textView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        textView.setText(text);
        viewGroup.addView(textView);
    }

}
