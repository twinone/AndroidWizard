package org.twinone.androidwizard.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.twinone.androidwizard.R;
import org.twinone.androidwizard.WizardFragment;

/**
 * Created by twinone on 11/07/2017.
 */

public class WelcomeWizardFragment extends WizardFragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT1 = "text";
    private static final String KEY_TEXT2 = "text2";
    private static final String KEY_ICON_RES_ID = "icon";

    public static WelcomeWizardFragment newInstance(String title, String text1, String text2, int iconResId) {
        WelcomeWizardFragment f = new WelcomeWizardFragment();
        Bundle b = new Bundle();
        b.putString(KEY_TITLE, title);
        b.putString(KEY_TEXT1, text1);
        b.putString(KEY_TEXT2, text2);
        b.putInt(KEY_ICON_RES_ID, iconResId);
        f.setArguments(b);
        return f;
    }


    @Override
    protected View setup(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments().getString(KEY_TITLE) == null
                || getArguments().getString(KEY_TEXT1) == null
                || getArguments().getString(KEY_TEXT2) == null
                || getArguments().getInt(KEY_ICON_RES_ID, 0) == 0) {
            throw new RuntimeException("You must instantiate WelcomeWizardFragment with newInstance(String,String,int)");
        }

        setTitle(getArguments().getString(KEY_TITLE));

        View v = inflater.inflate(R.layout.wizard_welcome, null);

        ((ImageView) v.findViewById(R.id.androidwizard_welcome_app_icon))
                .setImageResource(getArguments().getInt(KEY_ICON_RES_ID, 0));
        ((TextView) v.findViewById(R.id.androidwizard_welcome_text1)).setText(
                getArguments().getString(KEY_TEXT1));
        ((TextView) v.findViewById(R.id.androidwizard_welcome_text2)).setText(
                getArguments().getString(KEY_TEXT2));
        return v;
    }
}
