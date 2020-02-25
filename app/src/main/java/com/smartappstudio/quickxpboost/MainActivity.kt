package com.smartappstudio.quickxpboost

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FirebaseConfig() {
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig
    private var VersionCode = "versionCode"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        get_started.setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }
        //endregion
        mFirebaseRemoteConfig=getRemoteConfigValues()
        setRemoteConfigValues()

    }


    //endregion

    //region Firebase Config Method 2
    private fun setRemoteConfigValues() {
        //region Fetching Values
        val remoteCodeVersion = mFirebaseRemoteConfig.getLong(VersionCode)
        val AlertTitle = mFirebaseRemoteConfig.getString("Alert_Title")
        val AlertMessage = mFirebaseRemoteConfig.getString("Alert_Message")
        val Alert_Ok_btn = mFirebaseRemoteConfig.getString("Alert_Ok_Btn")
        val Alert_No_btn = mFirebaseRemoteConfig.getString("Alert_No_Btn")
        val Rate_Text = mFirebaseRemoteConfig.getString("rate_us")
        //endregion
        System.out.println(Rate_Text)
        if (remoteCodeVersion > 0) {
            val versionCode = BuildConfig.VERSION_CODE
            if (remoteCodeVersion > versionCode) {
                val dialogBuilder = AlertDialog.Builder(this)


                // set message of alert dialog
                dialogBuilder.setMessage(AlertMessage)
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton(Alert_Ok_btn, DialogInterface.OnClickListener { _, _ ->
                        val uri = Uri.parse("market://details?id=" + this@MainActivity.packageName)
                        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(
                            Intent.FLAG_ACTIVITY_NO_HISTORY or
                                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        )
                        try {
                            startActivity(goToMarket)
                        } catch (e: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + this@MainActivity.packageName)
                                )
                            )
                        }
                    })
                    // negative button text and action
                    .setNegativeButton(Alert_No_btn, // do something when the button is clicked
                        DialogInterface.OnClickListener { _, _ ->
                            finishAffinity()
                        })


                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle(AlertTitle)
                // show alert dialog
                alert.show()
            }
            //  main_layout!!.setBackgroundColor(Color.parseColor(remoteValueText))
        }

    }
    //endregion

    //region Firebase Config Method 3
    override fun onStart() {

        super.onStart()
        mFirebaseRemoteConfig=getRemoteConfigValues()
        //region Startup Notification Firebase Config
        val remoteCodeVersion = mFirebaseRemoteConfig.getLong(VersionCode)
        val versionCode = BuildConfig.VERSION_CODE

        if (remoteCodeVersion > versionCode) {
            getRemoteConfigValues()
        }
        //endregion
    }
    //endregion
}

