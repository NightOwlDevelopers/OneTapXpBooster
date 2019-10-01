package com.nightowldevelopers.onetapxpboost


import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_google.*
import kotlinx.android.synthetic.main.activity_main.products


/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */
class GoogleSignInActivity : BaseActivity(), PurchasesUpdatedListener, View.OnClickListener {

    private lateinit var billingClient: BillingClient
    private lateinit var productsAdapter: ProductsAdapter

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]
    protected val RC_LEADERBOARD_UI = 9004
    private val RC_ACHIEVEMENT_UI = 9003


    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google)
        setupBillingClient()

        products.visibility = View.GONE
        leaderboard.visibility = View.GONE
        achievement.visibility = View.GONE

        // Button listeners
        signInButton.setOnClickListener(this)
        signOutButton.setOnClickListener(this)

        achievement.setOnClickListener { showAchievements() }
        leaderboard.setOnClickListener { showLeaderboard() }

        instagram.setOnClickListener {
            val uri = Uri.parse("http://instagram.com/nightowldevelopers")
            val likeIng = Intent(Intent.ACTION_VIEW, uri)

            likeIng.setPackage("com.instagram.android")

            try {

                startActivity(likeIng)
                Toast.makeText(
                    this@GoogleSignInActivity,
                    "Follow Us \n& Unlock your Achievement",
                    Toast.LENGTH_LONG
                ).show()
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .unlock(getString(R.string.achievement_instagram_achievement))
                Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .submitScore(getString(R.string.leaderboard_leaderboard), 50000)
                Handler().postDelayed(Runnable {
                    // Do something after 5s = 5000ms
                    val mPlayer =
                        MediaPlayer.create(this@GoogleSignInActivity, R.raw.ta_da_sound_click)
                    mPlayer.start()
                    Toast.makeText(
                        this@GoogleSignInActivity,
                        "Hurrah! Your Instagram Achievement is Unlocked !!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }, 13000)
            } catch (e: ActivityNotFoundException) {

                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/nightowldevelopers")
                    )
                )
                Toast.makeText(
                    this@GoogleSignInActivity,
                    "Follow Us \n& Unlock your Achievement",
                    Toast.LENGTH_LONG
                ).show()
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .unlock(getString(R.string.achievement_instagram_achievement))
                Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .submitScore(getString(R.string.leaderboard_leaderboard), 200000)
                Handler().postDelayed(Runnable {
                    // Do something after 5s = 5000ms
                    val mPlayer =
                        MediaPlayer.create(this@GoogleSignInActivity, R.raw.ta_da_sound_click)
                    mPlayer.start()
                    Toast.makeText(
                        this@GoogleSignInActivity,
                        "Hurrah! Your Instagram Achievement is Unlocked !!",
                        Toast.LENGTH_LONG
                    ).show()
                }, 13000)
            }

        }

        rateApp.setOnClickListener {
            Toast.makeText(
                this@GoogleSignInActivity,
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
                    .unlock(getString(R.string.achievement_rate_on_playstore))
                Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .submitScore(getString(R.string.leaderboard_leaderboard), 150000)
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .unlock(getString(R.string.achievement_rate_on_playstore))
                Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .submitScore(getString(R.string.leaderboard_leaderboard), 150000)
            }

        }

        disconnectButton.setOnClickListener {
            val developerurl =
                "4619988116632070762" // getPackageName() from Context or Activity object
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        //Uri.parse("market://dev?id=$developerurl")
                        Uri.parse("market://details?id=com.nightowldevelopers.levelup2")
                    )
                )
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .unlock(getString(R.string.achievement_more_xp))
                Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .submitScore(getString(R.string.leaderboard_leaderboard), 80000)
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/dev?id=$developerurl")
                    )
                )
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .unlock(getString(R.string.achievement_more_xp))
                Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .submitScore(getString(R.string.leaderboard_leaderboard), 80000)
            }

        }


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
        // [END initialize_auth]

        //autopopup for login on startup
        signIn()

    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]

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

                var gamesClient = Games.getGamesClient(this@GoogleSignInActivity, account)
                gamesClient =
                    Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                gamesClient.setViewForPopups(findViewById(android.R.id.content))
                gamesClient.setGravityForPopups(Gravity.TOP or Gravity.CENTER_HORIZONTAL)
                onLoadProductsClicked()
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // [START_EXCLUDE]
                updateUI(null)
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        // [START_EXCLUDE silent]
        showProgressDialog()
        // [END_EXCLUDE]

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                    onLoadProductsClicked()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT)
                        .show()
                    updateUI(null)
                }

                // [START_EXCLUDE]
                hideProgressDialog()
                // [END_EXCLUDE]
            }
    }
    // [END auth_with_google]

    // [START signin]
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            updateUI(null)
            products.visibility = View.GONE
            leaderboard.visibility = View.GONE
            achievement.visibility = View.GONE
        }
    }

    private fun revokeAccess() {
        // Firebase sign out
        auth.signOut()

        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(this) {
            updateUI(null)

        }
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            status.text = getString(R.string.google_status_fmt, user.email)
            //detail.text = getString(R.string.firebase_status_fmt, user.uid)
            onLoadProductsClicked()
            signInButton.visibility = View.GONE
            signOutAndDisconnect.visibility = View.VISIBLE
            homeLogo.visibility = View.GONE

            textView4.visibility = View.VISIBLE
            textView3.visibility = View.VISIBLE
            instagram.visibility = View.VISIBLE
            rateApp.visibility = View.VISIBLE
            textViewRate.visibility = View.VISIBLE
            textViewIG.visibility = View.VISIBLE

        } else {
            status.setText(R.string.signed_out)
            detail.text = null

            homeLogo.visibility = View.VISIBLE
            signInButton.visibility = View.VISIBLE
            signOutAndDisconnect.visibility = View.GONE
            textView4.visibility = View.GONE
            textView3.visibility = View.GONE
            instagram.visibility = View.GONE
            rateApp.visibility = View.GONE
            textViewRate.visibility = View.GONE
            textViewIG.visibility = View.GONE

        }
    }


    override fun onClick(v: View) {
        val i = v.id
        when (i) {
            R.id.signInButton -> signIn()
            R.id.signOutButton -> signOut()
            R.id.disconnectButton -> revokeAccess()
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
        private val skuList = listOf("premium")
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
                } else {
                    println("BILLING | startConnection | RESULT: $billingResponseCode")
                }
            }

            override fun onBillingServiceDisconnected() {
                println("BILLING | onBillingServiceDisconnected | DISCONNECTED")
            }
        })
    }

    fun onLoadProductsClicked(view: View) {
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

    fun onLoadProductsClicked() {
        products.visibility = View.VISIBLE
        leaderboard.visibility = View.VISIBLE
        achievement.visibility = View.VISIBLE
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
        Toast.makeText(
            this, "onPurchasesUpdated:$responseCode", Toast.LENGTH_LONG
        )
        if (responseCode == 0) {
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .unlock(getString(R.string.achievement_level_1))
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .submitScore(getString(R.string.leaderboard_leaderboard), 10000)
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .unlock(getString(R.string.achievement_level_2))
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .submitScore(getString(R.string.leaderboard_leaderboard), 20000)
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .unlock(getString(R.string.achievement_level_3))
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .submitScore(getString(R.string.leaderboard_leaderboard), 30000)
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .unlock(getString(R.string.achievement_level_4))
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .submitScore(getString(R.string.leaderboard_leaderboard), 40000)
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .unlock(getString(R.string.achievement_level_5__rate_app_and_win_additional_xp))
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .submitScore(getString(R.string.leaderboard_leaderboard), 50000)
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .unlock(getString(R.string.achievement_level_6__follow_us_on_instagram_and_unlock_level_7))
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .submitScore(getString(R.string.leaderboard_leaderboard), 60000)
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .unlock(getString(R.string.achievement_level_10))
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .submitScore(getString(R.string.leaderboard_leaderboard), 100000)
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .unlock(getString(R.string.achievement_level_11))
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .submitScore(getString(R.string.leaderboard_leaderboard), 110000)
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .unlock(getString(R.string.achievement_level_12))
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .submitScore(getString(R.string.leaderboard_leaderboard), 120000)
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .unlock(getString(R.string.achievement_level_13))
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .submitScore(getString(R.string.leaderboard_leaderboard), 130000)
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .unlock(getString(R.string.achievement_level_14))
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .submitScore(getString(R.string.leaderboard_leaderboard), 140000)
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .unlock(getString(R.string.achievement_level_15))
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .submitScore(getString(R.string.leaderboard_leaderboard), 150000)



        } else {
            //loadProducts.setText("Payment Failed!")
        }

        allowMultiplePurchases(purchases)
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

    private fun showLeaderboard() {
        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .getLeaderboardIntent(getString(R.string.leaderboard_leaderboard))
            .addOnSuccessListener { intent -> startActivityForResult(intent, RC_LEADERBOARD_UI) }
    }


    private fun showAchievements() {
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .achievementsIntent
            .addOnSuccessListener { intent -> startActivityForResult(intent, RC_ACHIEVEMENT_UI) }
    }

}