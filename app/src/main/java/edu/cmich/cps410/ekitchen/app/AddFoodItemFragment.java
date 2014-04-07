package edu.cmich.cps410.ekitchen.app;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * Based on the DynamicFormFragment from
 * <a href="http://android.codeandmagic.org/android-dynamic-form-fragment/">Android Magic</a>.
 */
public class AddFoodItemFragment extends Fragment implements LoaderCallbacks<Void> {
    public static final String TAG = "AddFoodItemFragment";

    private ScrollView mScrollView;
    private LinearLayout mFormView;
    private ProgressBar mProgressView;

    private boolean mDone;

    private static int sId = 0;

    private static int id() {
        return sId++;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach(): activity = " + activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(): savedInstanceState = " + savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView(): container = " + container
                + "savedInstanceState = " + savedInstanceState);

        if (mScrollView == null) {
            // normally inflate the view hierarchy
            mScrollView = (ScrollView) inflater.inflate(R.layout.fragment_add_fooditem,
                    container, false);
            mFormView = (LinearLayout) mScrollView.findViewById(R.id.form);
            mProgressView = (ProgressBar) mScrollView
                    .findViewById(R.id.loading);
        } else {
            // mScrollView is still attached to the previous view hierarchy
            // we need to remove it and re-attach it to the current one
            ViewGroup parent = (ViewGroup) mScrollView.getParent();
            parent.removeView(mScrollView);
        }
        return mScrollView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated(): savedInstanceState = "
                + savedInstanceState);

        toggleLoading(true);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
    }

    public Loader<Void> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader(): id=" + id);

        AsyncTaskLoader<Void> loader = new AsyncTaskLoader<Void>(getActivity()) {
            @Override
            public Void loadInBackground() {
                Log.d(TAG, "loadInBackground(): simulating operation in background...!");

                // this is a simple way of simulating a time consuming operation
                // the two threads (the background thread created by the
                // AsyncTaskLoader and the one created by the Timer) need to be
                // synchronized
                final AtomicBoolean lock = new AtomicBoolean(false);

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (lock) {
                            lock.set(true);
                            lock.notifyAll();
                        }
                    }
                };

                // simulating a background operation that takes about 5 seconds
                Timer timer = new Timer();
                timer.schedule(task, 5000);

                synchronized (lock) {
                    while (!lock.get()) {
                        try {
                            lock.wait();
                        } catch (Exception e) {
                        }
                    }
                }
                Log.d(TAG, "loadInBackground(): DONE!");
                return null;
            }
        };
        loader.forceLoad();
        return loader;
    }

    public void onLoadFinished(Loader<Void> id, Void result) {
        Log.d(TAG, "onLoadFinished(): id=" + id);
        toggleLoading(false);
        buildForm();
    }

    public void onLoaderReset(Loader<Void> loader) {
        Log.d(TAG, "onLoaderReset(): id=" + loader.getId());
    }

    private void toggleLoading(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mFormView.setGravity(show ? Gravity.CENTER : Gravity.TOP);
    }

    private void buildForm() {
        // if the view hierarchy was already build, skip this
        if (mDone)
            return;

        addFormField("Item Name", InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        addFormField("Expiration Date", InputType.TYPE_DATETIME_VARIATION_DATE);

        addFormField("Quantity", InputType.TYPE_CLASS_NUMBER);

        addFormField("Quantity Unit", InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

        // the view hierarchy is now complete
        mDone = true;
    }

    private void addFormField(String label, int type) {
        TextView tvLabel = new TextView(getActivity());
        tvLabel.setLayoutParams(getDefaultParams(true));
        tvLabel.setText(label);

        EditText editView = new EditText(getActivity());
        editView.setLayoutParams(getDefaultParams(false));
        // setting a unique id is important in order to save the state
        // (content) of this view across screen configuration changes
        editView.setId(id());
        editView.setInputType(type);

        mFormView.addView(tvLabel);
        mFormView.addView(editView);
    }

    private LayoutParams getDefaultParams(boolean isLabel) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        if (isLabel) {
            params.bottomMargin = 5;
            params.topMargin = 10;
        }
        return params;
    }

}
