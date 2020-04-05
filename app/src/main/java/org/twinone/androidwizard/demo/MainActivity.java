package org.twinone.androidwizard.demo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import org.twinone.androidwizard.WizardActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // show the welcome screen only once
        WizardActivity.show(this, MyWizardActivity.class);


        findViewById(R.id.button).setOnClickListener((v) -> {
            // force to show it always, useful for a "Tutorial" button
            WizardActivity.show(this, MyWizardActivity.class, true);
        });

    }
}
