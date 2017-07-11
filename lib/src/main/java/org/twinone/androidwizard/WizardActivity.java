package org.twinone.androidwizard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
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
    private final List<WizardFragment> mFragments = new ArrayList<>();
    private CustomFragmentStatePagerAdapter mAdapter;

    private boolean mDefaultCanGoBack = true;
    private boolean mDefaultCanGoNext = true;

    private int mPage = -1;
    private boolean mCalledOnPageSelected = false;

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
                    return mFragments.get(position);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public int getCount() {
                return mFragments.size();
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

        setPagingEnabled(false);
        setCanGoNext(false);
    }

    public void collapseActionBarIfNeeded() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float h = dm.heightPixels / dm.density;
        //boolean land = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        // Collapse if bigger than 600dp height
        if (h < 600) {
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
            next();
        }
        if (v.getId() == R.id.wizardpager_bprev) {
            back();
        }
    }

    public void back() {
        if (getSelectedFragment() != null) {
            getSelectedFragment().onBack();
        }
        selectPage(mPager.getCurrentItem() - 1);
    }

    public void next() {
        if (getSelectedFragment() != null) {
            getSelectedFragment().onNext();
        }
        selectPage(mPager.getCurrentItem() + 1);
    }

    protected int getCount() {
        return mFragments.size();
    }


    protected void onPageSelected(int position) {
        collapseActionBarIfNeeded();

        mCalledOnPageSelected = true;

        boolean left = mPage < position;
        mPage = position;
        WizardFragment f = getSelectedFragment();
        if (f != null) {
            f.setComesFrom(left ? WizardFragment.BACK : WizardFragment.NEXT);
            Log.d("OnEnter", "page=" + getSelectedPage());
            if (f.isAdded()) {
                f.onEnter();
                if (f.comesFrom(WizardFragment.BACK)) f.onEnterFromBack();
                if (f.comesFrom(WizardFragment.NEXT)) f.onEnterFromNext();
            }
        }

        updateLayout();
    }

    public void selectPage(final int page) {
        WizardFragment f = getSelectedFragment();
        if (f != null) {
            f.onLeave();
        }

        Log.d("Select", "Page=" + page);
        if (page < 0) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        if (page >= getCount()) {
            setResult(RESULT_OK);
            setShouldShowWizard(this, false, getIntent().getStringExtra(EXTRA_PREF_KEY));
            finish();
            return;
        }

        // Workaround ViewPager's bug
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(page);
            }
        });
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

        setCanGoBack(mDefaultCanGoBack);
        setCanGoNext(mDefaultCanGoNext);

        WizardFragment f = getSelectedFragment();
        if (f != null) {
            mToolbar.setTitle(f.getTitle());
            if (f.isAdded()) {
                f.updateLayout();
            }
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

    protected void setFragments(List<WizardFragment> fragments) {
        mFragments.clear();
        mFragments.addAll(fragments);

        onFragmentsChanged();
    }

    private void onFragmentsChanged() {
        mAdapter.notifyDataSetChanged();

        if (mFragments.size() > 0) {
            selectPage(0);
        }
    }

    public void onFragmentAdded() {
        if (!mCalledOnPageSelected) {
            onPageSelected(getSelectedPage());
        }
        updateLayout();
    }

//    public void addPage(Class<? extends WizardFragment> fragment) {
//        addPage(mFragments.size(), fragment);
//    }
//
//    public void addPage(int location, Class<? extends WizardFragment> fragment) {
//        mFragments.add(location, fragment);
//        onFragmentsChanged();
//    }

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

    public void setShowDots(boolean showDots) {
        mProgress.setShowDots(showDots);
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

    public static boolean show(Activity a, Class<? extends WizardActivity> wizard, boolean force) {
        return show(a, wizard, DEFAULT_KEY, force);
    }

    public static boolean show(Activity a, Class<? extends WizardActivity> wizard, String key, boolean force) {
        if (!shouldShowWizard(a, key) && !force) return false;
        Intent i = new Intent(a, wizard);
        i.putExtra(EXTRA_PREF_KEY, key);
        a.startActivityForResult(i, REQUEST_CODE);
        return true;
    }

    public int getPosition(WizardFragment f) {
        return mAdapter.getItemPosition(f);
    }


    public List<WizardFragment> getFragments() {
        return mFragments;
    }

    public void addFragment(Class<? extends WizardFragment> fragmentClass) {
        try {
            mFragments.add(fragmentClass.newInstance());
        } catch (Exception e) {
            Log.e("WizardActivity", "Your classes must be instantiable with a default constructor", e);
        }
        onFragmentsChanged();
    }

    public void addFragment(WizardFragment fragment) {
        mFragments.add(fragment);
        onFragmentsChanged();
    }

}
