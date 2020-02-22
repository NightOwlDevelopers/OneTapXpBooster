package com.smartappstudio.quickxpboost

import android.animation.Animator
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.android.billingclient.api.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.Games
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_payment.*

class PaymentActivity : BaseActivity(), PurchasesUpdatedListener {

    private lateinit var billingClient: BillingClient
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    private val RC_ACHIEVEMENT_UI = 9003

    private val TAG = "GoogleActivity"
    private val RC_SIGN_IN = 9001
    private val skuList = listOf("premium")

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBillingClient()
        //region RemoteConfig
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        //Enable Debug mode for frequent fetches
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()
        mFirebaseRemoteConfig.setConfigSettings(configSettings)
        mFirebaseRemoteConfig.setDefaults(R.xml.firebasedefaults)
        //getRemoteConfigValues()
        //endregion


        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestScopes(Games.SCOPE_GAMES_LITE)
            .requestEmail()
            .build()
        // [END config_signin]


        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        signIn()
        setContentView(R.layout.activity_payment)
        unlockStatus.visibility = View.INVISIBLE

        achieve.setOnClickListener {
            showAchievements()
        }

        rate.setOnClickListener {
            Toast.makeText(
                this,
                "Give 5-star Rating \n& Check your Achievement",
                Toast.LENGTH_SHORT
            ).show()
            val appPackageName = packageName // getPackageName() from Context or Activity object
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .unlock(getString(R.string.achievement_level_19))
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .unlock(getString(R.string.achievement_level_20))
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .unlock(getString(R.string.achievement_level_19))
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .unlock(getString(R.string.achievement_level_20))
            }
        }

    }

    private fun setupBillingClient() {
        billingClient = BillingClient
            .newBuilder(this)
            .setListener(this)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    println("BILLING | startConnection | RESULT OK")
                    //progressBar2.visibility=View.GONE
                    onLoadProductsClicked()
                } else {
                    println("BILLING | startConnection | RESULT: $billingResponseCode")
                }
            }

            override fun onBillingServiceDisconnected() {
                println("BILLING | onBillingServiceDisconnected | DISCONNECTED")
            }
        })
    }
    fun onLoadProductsClicked() {
        if (billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build()
            billingClient.querySkuDetailsAsync(params) { responseCode, skuDetailsList ->
                if (responseCode == BillingClient.BillingResponse.OK) {
                    println("querySkuDetailsAsync, responseCode: $responseCode")
                    initProductAdapter(skuDetailsList)
                } else {
                    println("Can't querySkuDetailsAsync, responseCode: $responseCode")
                }
            }
        } else {
            println("Billing Client not ready")
        }
    }
    private fun initProductAdapter(skuDetailsList: List<SkuDetails>) {
        productsAdapter = ProductsAdapter(skuDetailsList) {
            val billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(it)
                .build()
            billingClient.launchBillingFlow(this, billingFlowParams)
        }
        products.adapter = productsAdapter
    }
    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        println("onPurchasesUpdated: $responseCode")
        allowMultiplePurchases(purchases)
        Toast.makeText(
            this, "onPurchasesUpdated:$responseCode", Toast.LENGTH_LONG
        )
        if (responseCode == 0) {
            //signIn()
            loader.visibility = View.VISIBLE
            unlockStatus.visibility = View.VISIBLE
            buttonTap.addAnimatorListener(object :
                Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    Log.e("Animation:", "start")
                }

                override fun onAnimationEnd(animation: Animator) {
                    Log.e("Animation:", "end")
                    //Your code for remove the fragment
                }

                override fun onAnimationCancel(animation: Animator) {
                    Log.e("Animation:", "cancel")
                }

                override fun onAnimationRepeat(animation: Animator) {
                    Log.e("Animation:", "repeat")
                    loader.visibility = View.VISIBLE
                    products.visibility = View.INVISIBLE
                    buttonTap.visibility = View.INVISIBLE
                    buttonTap.removeAllAnimatorListeners()
                }
            })
            trophy.visibility = View.VISIBLE
            trophy.repeatCount = 18
            trophy.playAnimation()
            trophy.addAnimatorListener(object :
                Animator.AnimatorListener {
                var i = 1
                override fun onAnimationStart(animation: Animator) {
                    Log.e("Animation:", "start")
                    unlockAchievements()
                    unlockStatus.visibility = View.VISIBLE
                    unlockStatus.text = "Unlocking achievemnet " + i
                    i++
                }

                override fun onAnimationEnd(animation: Animator) {
                    Log.e("Animation:", "end")
                    //Your code for remove the fragment
                    trophy.visibility = View.INVISIBLE
                    successStar.visibility = View.VISIBLE
                    successStar.playAnimation()
                }

                override fun onAnimationCancel(animation: Animator) {
                    Log.e("Animation:", "cancel")
                }

                override fun onAnimationRepeat(animation: Animator) {
                    Log.e("Animation:", "repeat")
                    unlockAchievements()
                    unlockStatus.text = "Unlocking achievemnet " + i
                    i++
                }
            })
            successStar.addAnimatorListener(object :
                Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    Log.e("Animation:", "start")
                    unlockStatus.text = "Hurray! All achievements unlocked!"
                }

                override fun onAnimationEnd(animation: Animator) {
                    Log.e("Animation:", "end")
                    //Your code for remove the fragment
                    products.visibility = View.VISIBLE
                    buttonTap.visibility = View.VISIBLE
                    loader.visibility = View.INVISIBLE
                    unlockStatus.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator) {
                    Log.e("Animation:", "cancel")
                }

                override fun onAnimationRepeat(animation: Animator) {
                    Log.e("Animation:", "repeat")
                }
            })

            println("Purchase Done!")
        }

    }
    private fun allowMultiplePurchases(purchases: MutableList<Purchase>?) {
        val purchase = purchases?.first()
        if (purchase != null) {
            billingClient.consumeAsync(purchase.purchaseToken) { responseCode, purchaseToken ->
                if (responseCode == BillingClient.BillingResponse.OK && purchaseToken != null) {
                    println("AllowMultiplePurchases success, responseCode: $responseCode")
                    Toast.makeText(
                        this, "MultiplePurchase:$responseCode", Toast.LENGTH_LONG
                    )
                } else {
                    println("Can't allowMultiplePurchases, responseCode: $responseCode")
                }
            }
        }
    }

    private fun clearHistory() {
        billingClient.queryPurchases(BillingClient.SkuType.INAPP).purchasesList
            .forEach {
                billingClient.consumeAsync(it.purchaseToken) { responseCode, purchaseToken ->
                    if (responseCode == BillingClient.BillingResponse.OK && purchaseToken != null) {
                        println("onPurchases Updated consumeAsync, purchases token removed: $purchaseToken")
                    } else {
                        println("onPurchases some troubles happened: $responseCode")
                    }
                }
            }
    }

    // [START signin]
    private fun signIn() {
        //signInButton!!.startLoading()
        //send login request

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    private fun signOut() {
        // Firebase sign out
        //signInButton.reset()
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //updateUI(null)
            products.visibility = View.GONE
        }
    }

    private fun revokeAccess() {
        // Firebase sign out
        auth.signOut()

        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(this) {
            //updateUI(null)

        }
    }

    // [START onactivityresult]
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)

                var gamesClient = Games.getGamesClient(this, account)
                gamesClient =
                    Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                gamesClient.setViewForPopups(findViewById(android.R.id.content))
                gamesClient.setGravityForPopups(Gravity.TOP or Gravity.CENTER_HORIZONTAL)
                //onLoadProductsClicked()
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "AfterPayment-Google sign in failed", e)
                startActivity(Intent(this, MainActivity::class.java))
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("TAG", "AfterPayment-firebaseAuthWithGoogle:" + acct.id!!)
        // [START_EXCLUDE silent]
        showProgressDialog()
        // [END_EXCLUDE]

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "AfterPayment-signInWithCredential:success")

                    var user = auth.currentUser?.displayName
                    var firstname= user!!.split(" ").first()
                    if(firstname!=null) {
                        //welcomeText.text = "Welcome\n$firstname !"
                        welcomeText.text = "Welcome $firstname !"
                    }
                } else {
                    //signInButton!!.loadingFailed()
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "AfterPaymentsignInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Google SignIn Failed!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    //updateUI(null)
                }
                // [START_EXCLUDE]
                hideProgressDialog()
                // [END_EXCLUDE]
            }
    }
    // [END auth_with_google]

    private fun showAchievements() {
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .achievementsIntent
            .addOnSuccessListener { intent -> startActivityForResult(intent, RC_ACHIEVEMENT_UI) }
    }

    private fun unlockAchievements() {
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_1))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_2))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_3))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_4))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_5))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_6))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_7))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_8))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_9))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_10))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_11))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_12))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_13))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_14))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_15))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_16))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_17))
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .unlock(getString(R.string.achievement_level_18))
    }

    override fun onBackPressed(){
        signOut()
        Toast.makeText(this, "You have been signed out!!", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, MainActivity::class.java))
        /*finishAffinity()
        finish()*/
    }
}
