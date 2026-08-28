package com.example.ebank

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, DashboardFragment())
                .commit()
        }
    }

    /**
     * Dashboard is home base. Whenever we navigate back to it explicitly (Cancel from
     * Confirmation, or after a successful transfer), clear the whole back stack first so
     * the system Back button exits the app from here instead of replaying Transfer/Confirm.
     */
    fun showDashboardFragment() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragmentContainer, DashboardFragment())
            .commit()
    }

    fun showTransferFragment() {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragmentContainer, TransferFragment())
            .addToBackStack(null)
            .commit()
    }

    fun showConfirmationFragment(request: TransferRequest) {
        val fragment = ConfirmationFragment()
        fragment.arguments = Bundle().apply {
            putSerializable(ConfirmationFragment.ARG_TRANSFER_REQUEST, request)
        }
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun showHistoryFragment() {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragmentContainer, HistoryFragment())
            .addToBackStack(null)
            .commit()
    }
}
