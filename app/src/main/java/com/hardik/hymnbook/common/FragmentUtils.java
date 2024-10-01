package com.hardik.hymnbook.common;

import static com.hardik.hymnbook.common.Constants.BASE_TAG;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hardik.hymnbook.R;
import com.hardik.hymnbook.presentation.MainActivity;


public class FragmentUtils {
    private static final String TAG = BASE_TAG + FragmentUtils.class.getSimpleName();

    private FragmentUtils() {
        Log.i(TAG, "FragmentUtils: ");
    }

    // Singleton instance
    private static FragmentUtils instance = null;

    // Method to get the singleton instance
    public static FragmentUtils getInstance() {
        if (instance == null) {
            synchronized (FragmentUtils.class) {
                if (instance == null) {
                    instance = new FragmentUtils();
                }
            }
        }
        return instance;
    }

    // Fragment transaction management
    public static void switchFragment(FragmentManager fragmentManager, Fragment fragment, boolean addToBackStack, boolean refreshFragment) {
        /// Obtain FragmentManager  \\FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager == null) {
            return;
        }

        /// get tag name from fragment
        String tag = fragment.getClass().getSimpleName();
        Log.i(TAG, "switchFragment: " + tag);

        /// Save the current fragment state
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment);
        Bundle fragmentState = null;
        if (currentFragment != null) {
            fragmentState = new Bundle();
            currentFragment.onSaveInstanceState(fragmentState);
        }

        /// Begin FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);

        /// If the user wants to add a piece to the backstack, even if it is already in the stack
        if (existingFragment != null)
        {
            /// If the fragment with the given tag already exists
            Log.i(TAG, "switchFragment: the fragment tag is already exists: " + tag + " existingFragment:isNotNull");
            if (addToBackStack)
            {
                Log.i(TAG, "switchFragment: add more clones fragment");
                String tagRepeat = tag + fragmentManager.getBackStackEntryCount();
                Log.i(TAG, "switchFragment: " + tag + "Renamed to " + tagRepeat);
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment, tagRepeat);// Replace the content of the fragment container with the new fragment
                fragmentTransaction.addToBackStack(tagRepeat);
            } else
            {
                if (refreshFragment) {/// so we refresh the fragment to the transaction
                    Log.i(TAG, "switchFragment: contain unique fist fragment but RefreshFragment it ");
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment, tag);// Replace the content of the fragment container with the new fragment
                } else {
                    Log.i(TAG, "switchFragment: contain unique fist fragment");
                    fragment = existingFragment; // Use the existing fragment instance
                    //fragmentManager.popBackStack();// we need to pop the back stack to this fragment directly
                    fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                fragmentManager.popBackStack(tag, 0);
                    /// Clear the back stack manually
                    int backStackEntryCount = fragmentManager.getBackStackEntryCount();
                    for (int i = 0; i < backStackEntryCount; i++) {
                        fragmentManager.popBackStack();
                    }
                }
            }

        } else {
            /// If the fragment with the given tag doesn't exist in the back stack
            Log.i(TAG, "switchFragment: the fragment tag doesn't exist: " + tag);
            /// so we add the (first time / new) fragment to the transaction
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment, tag);// Replace the content of the fragment container with the new fragment
            if (addToBackStack) {/// If adding to back stack, add the transaction to back stack
                Log.i(TAG, "switchFragment: contain unique fist fragment");
                boolean fragmentInBackStack = fragmentManager.popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);// go back to the target tag(which is in stack) and clear all back stack of up on including it's self
//                boolean fragmentInBackStack = fragmentManager.popBackStackImmediate(tag, 0);
                if (!fragmentInBackStack) {/// If the fragment is not in the back stack, add it
                    fragmentTransaction.addToBackStack(tag);// Add the transaction to the back stack
                    Log.i(TAG, "switchFragment: add to the back stack: if not in BackStack: " + tag);
                }
                Log.i(TAG, "switchFragment: add to the back stack: " + tag);
//                fragmentTransaction.addToBackStack(tag);// Add the transaction to the back stack
            }
        }

        /// Commit the transaction
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();// Use commitAllowingStateLoss() instead of commit()
        Log.i(TAG, "switchFragment: countOfStackAdd: " + (fragmentManager.getBackStackEntryCount()));

        /// Restore the state of the replaced fragment
        FragmentManager.FragmentLifecycleCallbacks callbacks = null;
        if (fragmentState != null) {
            Bundle finalFragmentState = fragmentState;
            Fragment finalFragment = fragment;
            FragmentManager.FragmentLifecycleCallbacks finalCallbacks = callbacks;
            callbacks = new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                    super.onFragmentCreated(fm, f, savedInstanceState);
                    if (f == finalFragment && f.getView() != null) { // Add null check for f.getView()
                        f.getView().post(new Runnable() {
                            @Override
                            public void run() {
                                f.onViewStateRestored(finalFragmentState);
                                if (finalCallbacks != null) {
                                    fm.unregisterFragmentLifecycleCallbacks(finalCallbacks);
                                }
                            }
                        });
                    }
                }
            };
            fragmentManager.registerFragmentLifecycleCallbacks(callbacks, false);
        }
    }

    public static void handleBackPressed(Activity activity) {
        Log.i(TAG, "handleBackPressed: ");
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
            int stackCount = fragmentManager.getBackStackEntryCount();
            Log.i(TAG, "handleBackPressed: stackCount: " + stackCount);
            if (stackCount >= 1) {
                Log.i(TAG, "handleBackPressed: go back");
                fragmentManager.popBackStack();
            } else {
                Log.i(TAG, "handleBackPressed: finish activity");
                fragmentManager.popBackStackImmediate();
                activity.finish();
            }
        }
    }
}




