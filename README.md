[![](https://jitpack.io/v/twinone/AndroidWizard.svg)](https://jitpack.io/#twinone/AndroidWizard)


# AndroidWizard
Simple to use welcome screen for your Android app.

# Demo
Here is the library running in my [Intruder Selfie app](https://play.google.com/store/apps/details?id=org.twinone.intruderselfie&hl=en):

<img src="https://user-images.githubusercontent.com/4309591/28085460-1a41183e-667c-11e7-9dcc-3e8cb1fc5731.gif" height="450">



# Features:
* Simple to use
* Attractive design
* Request runtime permissions in an elegant way
* Example classes
* Enable/disable next/previous buttons


# Installation

Using [JitPack](https://jitpack.io/#twinone/AndroidWizard), an awesome build service,
you can just copy the below few lines into your project.

Add jitpack.io in your **root** build.gradle at the end of repositories:
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

In your **app's build.gradle**, add the dependency:
```
dependencies {
  compile 'com.github.twinone:AndroidWizard:b4546bd712'
}
```

# Usage

Just extend WizardActivity

```java
public class MyWizardActivity extends WizardActivity {

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        
        // The WelcomeWizardScreen shows a line, your icon and
        // another line. This should be the first screen in most apps
        WelcomeWizardFragment.newInstance(
                "Android Wizard",
                "Welcome to Android Wizard Demo application",
                "Tap next to continue",
                R.mipmap.ic_launcher
        ).addTo(this);
        
        // A TextWizardFragment is a very simple information screen
        TextWizardFragment.newInstance(
                "Another screen", "This is a very useful string"
        ).addTo(this);
        
        TextWizardFragment.newInstance(
                "Almost done", "Yet another screen"
        ).addTo(this);
    }
}
```

... and launch it from your MainActivity:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  ...
  // show the welcome screen only once
  WizardActivity.show(this, MyWizardActivity.class);


  findViewById(R.id.button).setOnClickListener((v) -> {
    // force to show it always, useful for a "Tutorial" button
    WizardActivity.show(this, MyWizardActivity.class, true);
  });
}
```

This will show you the following welcome screen:

<img src="https://user-images.githubusercontent.com/4309591/28090846-093c4466-668e-11e7-8180-ab7a65611018.jpg" height="450">


# Requesting permissions

TODO
