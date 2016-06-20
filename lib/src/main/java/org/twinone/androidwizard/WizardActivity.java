package org.twinone.androidwizard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Luuk W. (Twinone).
 */
public abstract class WizardActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 0x93F9;
    public static final String EXTRA_PREF_KEY = "org.twinone.wizardpager.extra.pref_key";
    private static final String DEFAULT_KEY = "org.twinone.wizardpager.pref.should_show";

    private FloatingActionButton mPrev;
    private FloatingActionButton mNext;
    private DottedProgressView mProgress;
    private CustomViewPager mPager;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mToolbar;
    private final List<Class<? extends WizardFragment>> mFragmentClasses = new ArrayList<>();
    private CustomFragmentStatePagerAdapter mAdapter;

    private boolean mDefaultCanGoBack = true;
    private boolean mDefaultCanGoNext = false;

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);

        setTheme(R.style.WizardPagerTheme);

        setContentView(R.layout.wizardpager_activity);


        mPager = (CustomViewPager) findViewById(R.id.wizardpager_pager);
        mAdapter = new CustomFragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                try {
                    return mFragmentClasses.get(position).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public int getCount() {
                return mFragmentClasses.size();
            }
        };

        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                WizardActivity.this.onPageSelected(position);
            }
        });
        mPrev = (FloatingActionButton) findViewById(R.id.wizardpager_bprev);
        mPrev.setOnClickListener(this);
        mNext = (FloatingActionButton) findViewById(R.id.wizardpager_bnext);
        mNext.setOnClickListener(this);
        mProgress = (DottedProgressView) findViewById(R.id.wizardpager_progress);
        mToolbar = (CollapsingToolbarLayout) findViewById(R.id.wizardpager_collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        collapseAppBarIfSmall();


        setPagingEnabled(false);
        setCanGoNext(false);
    }

    private void collapseAppBarIfSmall() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float h = dm.heightPixels / dm.density;
        boolean land = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        // Collapse if in landscape and bigger than 600dp height
        if (land && h < 600) {
            mAppBarLayout.setExpanded(false, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLayout();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.wizardpager_bnext) {
            selectPage(mPager.getCurrentItem() + 1);
        }
        if (v.getId() == R.id.wizardpager_bprev) {
            selectPage(mPager.getCurrentItem() - 1);
        }
    }

    protected int getCount() {
        return mFragmentClasses.size();
    }


    protected void onPageSelected(int position) {
        updateLayout();
    }

    public void selectPage(int page) {
        if (page < 0) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        if (page >= getCount()) {
            setResult(RESULT_OK);
            finish();
            setShouldShowWizard(this, false, getIntent().getStringExtra(EXTRA_PREF_KEY));
            return;
        }

        mPager.setCurrentItem(page);
    }

    public int getSelectedPage() {
        return mPager.getCurrentItem();
    }

    public void updateLayout() {

        mProgress.setCount(getCount());
        mProgress.setCurrent(getSelectedPage() + 1);


        mNext.setImageResource(getSelectedPage() == getCount() - 1
                ? R.drawable.ic_check_white_24dp
                : R.drawable.ic_arrow_forward_white_24dp);
        mPrev.setImageResource(getSelectedPage() == 0
                ? R.drawable.ic_close_white_24dp
                : R.drawable.ic_arrow_back_white_24dp);
        //setCanGoBack(getSelectedPage() != 0);

        setCanGoBack(mDefaultCanGoBack);
        setCanGoNext(mDefaultCanGoNext);

        WizardFragment f = getSelectedFragment();
        if (f != null) {
            mToolbar.setTitle(f.getTitle());
            if (f.isAdded()) f.updateLayout();
        }
    }

    protected void setDefaultCanGoBack(boolean defaultCanGoBack) {
        mDefaultCanGoBack = defaultCanGoBack;
    }

    protected void setDefaultCanGoNext(boolean defaultCanGoNext) {
        mDefaultCanGoNext = defaultCanGoNext;
    }

    private WizardFragment getSelectedFragment() {
        return (WizardFragment) mAdapter.getFragment(getSelectedPage());
    }

    public void setCanGoBack(boolean canGoBack) {
        mPrev.setVisibility(canGoBack ? View.VISIBLE : View.INVISIBLE);

    }

    public void setCanGoNext(boolean canGoNext) {
        mNext.setVisibility(canGoNext ? View.VISIBLE : View.INVISIBLE);
    }

    public void setPagingEnabled(boolean enabled) {
        mPager.setPagingEnabled(enabled);
    }

    public CollapsingToolbarLayout getToolbar() {
        return mToolbar;
    }

    protected void setFragments(List<Class<? extends WizardFragment>> fragments) {
        mFragmentClasses.clear();
        mFragmentClasses.addAll(fragments);

        mPager.getAdapter().notifyDataSetChanged();

        if (mFragmentClasses.size() > 0) {
            mPager.setCurrentItem(0);
        }
    }

    public void onFragmentAdded() {
        updateLayout();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    private static SharedPreferences getPrefs(Context c) {
        return c.getSharedPreferences("org.twinone.wizardpager.prefs", Context.MODE_PRIVATE);
    }

    public static void setShouldShowWizard(Context c, boolean shouldShowWizard, String key) {
        getPrefs(c).edit().putBoolean(key, shouldShowWizard).apply();
    }

    public static boolean shouldShowWizard(Context c, String key) {
        return getPrefs(c).getBoolean(key, true);
    }

    /**
     * Shows the wizard if needed<br>
     * Usage example:
     * <pre>{@code
     * onCreate() {
     *     super.onCreate();
     *     ...
     *     WizardActivity.show(this, YourWizardActivity.class)
     *     ...
     *     setContentView();
     * }
     * } </pre>
     * <br><br>
     * If the user doesn't complete the wizard, RESULT_CANCELED will be returned in your onActivityResult<br>
     * Since they didn't complete setup, you can finish the calling activity:
     * <p/>
     * <pre>{@code
     * onActivityResult(requestCode, resultCode, data) {
     *     if (requestCode == WizardActivity.REQUEST_CODE) {
     *         if (resultCode == RESULT_OK) {
     *             // Wizard completed
     *         } else {
     *             // Wizard not completed
     *             finish();
     *         }
     *     }
     * }
     *
     *
     * }</pre>
     *
     * @param a      An Activity
     * @param wizard The class of your WizardActivity extension
     * @return true if the wizard has been shown
     */
    public static boolean show(Activity a, Class<? extends WizardActivity> wizard) {
        return show(a, wizard, DEFAULT_KEY, false);
    }

    public static boolean show(Activity a, Class<? extends WizardActivity> wizard, String key, boolean force) {
        if (!shouldShowWizard(a, key) && !force) return false;
        Intent i = new Intent(a, wizard);
        i.putExtra(EXTRA_PREF_KEY, key);
        a.startActivityForResult(i, REQUEST_CODE);
        return true;
    }

}
