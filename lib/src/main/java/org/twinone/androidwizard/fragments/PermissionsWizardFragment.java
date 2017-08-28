package org.twinone.androidwizard.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.twinone.androidwizard.R;
import org.twinone.androidwizard.WizardFragment;

/**
 * Created by twinone on 11/07/2017.
 */

public class PermissionsWizardFragment extends WizardFragment{

    public static final String KEY_TITLE = "title";
    public static final String KEY_TEXT1 = "text";
    public static final String KEY_TEXT2 = "text2";
    public static final String KEY_ICON_RES_ID = "icon";

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

    private Button mGrant;

    @Override
    protected View setup(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        setTitle(R.string.wizard_tit_perms);
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.wizard_perms, null);

        mGrant = (Button) v.findViewById(R.id.wizard_b_grant);
        mGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] perms = {
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                };
                requestPermissions(perms, 0);
            }
        });


        return v;
    }

    public void onEnterFromBack() {
        if (hasPermissions(getActivity())) next();
    }

    public static boolean hasPermissions(Context c) {
        return hasPermission(c, Manifest.permission.CAMERA) &&
                hasPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static boolean hasPermission(Context c, String permission) {
        return ContextCompat.checkSelfPermission(
                c, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void updateLayout() {
        boolean complete = hasPermissions(getActivity());
        setCanGoNext(complete);
        setComplete(R.id.wizard_b_grant, complete);
    }
}
