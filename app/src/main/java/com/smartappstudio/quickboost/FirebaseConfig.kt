package com.smartappstudio.quickboost

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

open class FirebaseConfig : AppCompatActivity() {

    var firebaseLogged = false
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig

    fun firebaseRemoteConfig() {
        //region Firebase Config Setup
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()
        mFirebaseRemoteConfig.setConfigSettings(configSettings)
        mFirebaseRemoteConfig.setDefaults(R.xml.firebasedefaults)
    }

    //region Firebase Config Method 1
    internal fun getRemoteConfigValues(): FirebaseRemoteConfig {

        if (!firebaseLogged) {
            firebaseRemoteConfig()
        }
        var cacheExpiration: Long = 7200//2 hours

        // Allow fetch on every call for now - remove/comment on production builds
        if (mFirebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //   Toast.makeText(this, "Fetch Succeeded", Toast.LENGTH_SHORT).show()
                    mFirebaseRemoteConfig.activateFetched()
                    firebaseLogged = true
                } else {
                    //   Toast.makeText(this, "Fetch Failed", Toast.LENGTH_SHORT).show()
                }

            }
        return mFirebaseRemoteConfig
    }
}
