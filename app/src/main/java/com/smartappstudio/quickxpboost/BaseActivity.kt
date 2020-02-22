package com.smartappstudio.quickxpboost


import android.app.ProgressDialog
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_payment.*

open class BaseActivity : AppCompatActivity() {

    @VisibleForTesting
    val progressDialog by lazy {
        ProgressDialog(this)
    }

    fun showProgressDialog() {
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.isIndeterminate = true
        //set progress loading animation
        //progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

    public override fun onPause() {
        super.onPause()
        if (loader != null) {
            loader.visibility = View.VISIBLE
            products.visibility = View.INVISIBLE
            buttonTap.visibility = View.INVISIBLE
        }
    }

    public override fun onResume() {
        super.onResume()
        if (loader != null) {
            loader.visibility = View.INVISIBLE
            products.visibility = View.VISIBLE
            buttonTap.playAnimation()
            buttonTap.visibility = View.VISIBLE
        }
    }
}