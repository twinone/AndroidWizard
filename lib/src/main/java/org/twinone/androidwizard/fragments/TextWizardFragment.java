package org.twinone.androidwizard.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.twinone.androidwizard.R;
import org.twinone.androidwizard.WizardFragment;

/**
 * Created by twinone on 11/07/2017.
 */

public class TextWizardFragment extends WizardFragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "text";

    public static TextWizardFragment newInstance(String title, String text) {
        TextWizardFragment f = new TextWizardFragment();
        Bundle b = new Bundle();
        b.putString(KEY_TITLE, title);
        b.putString(KEY_TEXT, text);
        f.setArguments(b);
        return f;
    }


    @Override
    protected View setup(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments().getString(KEY_TITLE) == null || getArguments().getString(KEY_TEXT) == null) {
            throw new RuntimeException("You must instantiate TextWizardFragment with newInstance(String,String)");
        }

        setTitle(getArguments().getString("title"));

        TextView text = new TextView(getActivity());
        text.setGravity(Gravity.CENTER);
        text.setTextSize(20);
        text.setText(getArguments().getString(KEY_TEXT));
        return text;
    }
}
