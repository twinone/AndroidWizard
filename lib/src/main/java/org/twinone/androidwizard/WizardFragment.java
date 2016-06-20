package org.twinone.androidwizard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Luuk W. (Twinone).
 */
public class WizardFragment extends Fragment {

    public static final int BACK = 1;
    public static final int NEXT = 2;

    private int mComesFrom = BACK;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(getActivity() instanceof WizardActivity))
            throw new IllegalStateException("WizardFragment should be added to a WizardActivity");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle(getTitle());
    }

    protected WizardActivity getWizardActivity() {
        return (WizardActivity) getActivity();
    }

    protected void setCanGoNext(boolean canGoNext) {
        getWizardActivity().setCanGoNext(canGoNext);
    }

    protected void setCanGoBack(boolean canGoBack) {
        getWizardActivity().setCanGoBack(canGoBack);
    }

    protected void setTitle(String title) {
        getWizardActivity().getToolbar().setTitle(title);
    }

    protected void setTitle(int titleResId) {
        setTitle(getString(titleResId));
    }

    protected String getTitle() {
        return getString(getTitleResId());
    }

    protected int getTitleResId() {
        return 0;
    }

    public void setComesFrom(int from) {
        mComesFrom = from;
    }

    public int comesFrom() {
        return mComesFrom;
    }

    public boolean comesFrom(int from) {
        return comesFrom() == from;
    }

    @Override
    public void onResume() {
        super.onResume();
        getWizardActivity().onFragmentAdded();
    }

    /**
     * Called when onResume is called, when this fragment is navigated to, and at startup<br>
     * Override this to update the navigation buttons, and any state in your view
     */
    protected void updateLayout() {
    }

    /**
     * Replaces the passed view with a green button with a checkmark
     */
    public void setComplete(int id, boolean complete) {
        View root = getView();
        if (root == null) return;
        View v = root.findViewById(id);
        ViewGroup parent = (ViewGroup) v.getParent();
        int index = parent.indexOfChild(v);
        if (v.getVisibility() == View.VISIBLE) {
            if (!complete) return;
            // parent.removeView(v);
            v.setVisibility(View.GONE);
            parent.addView(getCompleteButton(), index);
            return;
        } else {
            if (complete) return;
            v.setVisibility(View.VISIBLE);
            if (index > 0)
                parent.removeView(parent.getChildAt(index - 1));
        }

    }

    protected View getCompleteButton() {
        FloatingActionButton fab = new FloatingActionButton(getActivity());
        fab.setImageResource(R.drawable.ic_check_white_24dp);
        fab.setBackgroundTintList(ColorStateList.valueOf(getThemeAccentColor(getContext())));
        return fab;
    }

    public static int getThemeAccentColor(final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        return value.data;
    }

    /**
     * Called when the user navigates back (or exits) from this fragment
     */
    public void onNext() {

    }

    /**
     * Called when the user navigates back (or exits) from this fragment
     */
    public void onBack() {

    }


    /**
     * Convenience method called after both onBack() and onNext() (Similar to onPause())
     */
    public void onLeave() {
    }

    /**
     * Called when this fragment has become active (Similar to onResume())
     */
    public void onEnter() {

    }

    /**
     * Called when this fragment is navigated to from back
     */
    public void onEnterFromBack() {

    }

    /**
     * Called when this fragment is navigated to from next
     */
    public void onEnterFromNext() {

    }

    public int getPosition() {
        return getWizardActivity().getPosition(this);
    }

    protected void next() {
        if (!isSelected())
            throw new IllegalStateException("You can only call next() when this fragment isSelected()");
        getWizardActivity().next();
    }

    protected void back() {
        if (!isSelected())
            throw new IllegalStateException("You can only call back() when this fragment isSelected()");
        getWizardActivity().back();
    }

    protected boolean isSelected() {
        return getPosition() == getWizardActivity().getSelectedPage();
    }
}
