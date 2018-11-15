package io.virtualapp.delegate;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.lody.virtual.client.core.AppCallback;


public class MyComponentDelegate implements AppCallback {
    Activity mProcessTopActivity;

    @Override
    public void beforeApplicationCreate(Application application) {
    }

    @Override
    public void afterApplicationCreate(Application application) {
        //TODO: listen activity lifecycle
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                //fix crash of youtube#sound keys move to ActivityFixer
            }

            @Override
            public void onActivityResumed(Activity activity) {
                mProcessTopActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (mProcessTopActivity == activity) {
                    mProcessTopActivity = null;
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @Override
    public void onSendBroadcast(Intent intent) {

    }
}
