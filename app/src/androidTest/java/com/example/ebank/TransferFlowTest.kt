package com.example.ebank

import android.Manifest
import android.os.Build
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Lab 8: drives the real Dashboard -> Transfer form -> Confirmation flow.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TransferFlowTest {

    // DashboardFragment asks for POST_NOTIFICATIONS (Lab 6) as soon as it opens on Android 13+.
    // That system dialog would sit on top of the app and block Espresso, so grant it up front.
    // order = 0 makes this rule run before the activity is launched.
    @get:Rule(order = 0)
    val notificationPermissionRule: GrantPermissionRule =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            GrantPermissionRule.grant()
        }

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun validTransferShowsConfirmationScreen() {
        // Dashboard quick-action "Transfer" opens the Transfer form.
        onView(withId(R.id.btnTransfer)).perform(click())

        // replaceText (not typeText): the form starts pre-filled with a default recipient,
        // and typeText would append to it instead of replacing it.
        onView(withId(R.id.etRecipientAccount))
            .perform(replaceText("8001234567"), closeSoftKeyboard())
        onView(withId(R.id.etRecipientName))
            .perform(replaceText("Kasun Silva"), closeSoftKeyboard())
        // The amount field sits inside a scroll view, so make sure it is on screen first.
        onView(withId(R.id.etAmount))
            .perform(scrollTo(), replaceText("2500"), closeSoftKeyboard())

        onView(withId(R.id.btnSubmit)).perform(click())

        // Confirmation screen: recipient name and amount TextViews are visible.
        onView(withId(R.id.tv_recipient_name)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_amount_value)).check(matches(isDisplayed()))
    }
}
