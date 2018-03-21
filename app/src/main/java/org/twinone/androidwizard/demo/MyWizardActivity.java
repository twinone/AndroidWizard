package org.twinone.androidwizard.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.twinone.androidwizard.WizardActivity;
import org.twinone.androidwizard.WizardFragment;
import org.twinone.androidwizard.fragments.TextWizardFragment;
import org.twinone.androidwizard.fragments.WelcomeWizardFragment;

/**
 * Created by twinone on 11/07/2017.
 */

public class MyWizardActivity extends WizardActivity {

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);

        setShowLines(false);
        setProgressPadding(100); //dp
        setProgressDotsRadius(5); //dp

        WelcomeWizardFragment.newInstance(
                "Android Wizard",
                "Welcome to Android Wizard Demo application",
                "Tap next to continue",
                R.mipmap.ic_launcher
        ).addTo(this);

        TextWizardFragment.newInstance(
                "Welcome", "Welcome to my awesome app!"
        ).addTo(this);

        TextWizardFragment.newInstance(
                "Ehm", "This is a test screen"
        ).addTo(this);


    }

    public static class WelcomeFragment extends WizardFragment {

        @Override
        protected View setup(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setTitle(R.string.app_name);

            TextView text = new TextView(getActivity());
            text.setGravity(Gravity.CENTER);
            text.setTextSize(20);
            text.setText("Welcome to my awesome app!");
            return text;
        }
    }
}
