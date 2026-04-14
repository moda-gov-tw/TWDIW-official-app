package tw.gov.moda.digitalwallet

import android.app.Activity
import android.app.Application
import android.os.Bundle
import dagger.hilt.android.HiltAndroidApp
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.ui.main.MainActivity
import javax.inject.Inject

@HiltAndroidApp
class AppApplication : Application() {
    private var mCurrentActivity: Activity? = null

    @Inject
    lateinit var mPref: ModaSharedPreferences

    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("sqlcipher")
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                mCurrentActivity = activity
            }

            override fun onActivityStarted(activity: Activity) {
                mCurrentActivity = activity
            }

            override fun onActivityResumed(activity: Activity) {
                mCurrentActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }

    fun getCurrentActivity(): MainActivity? = mCurrentActivity as? MainActivity
}