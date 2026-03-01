package com.example.smartbite

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import com.example.smartbite.view.LoginActivity
import com.example.smartbite.view.UserDashboardActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentalTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testLoginNavigatesToUserDashboard() {

        // ✅ Use NON-admin email here, otherwise it will go to AdminDashboardActivity
        composeRule.onNodeWithTag("email")
            .performTextInput("shresthasangya23@gmail.com") // put your valid user email

        composeRule.onNodeWithTag("password")
            .performTextInput("sangya2063") // put your valid password

        composeRule.onNodeWithTag("login")
            .performClick()

        // ✅ Wait until dashboard intent is fired (up to 10 seconds)
        composeRule.waitUntil(timeoutMillis = 10000) {
            try {
                intended(hasComponent(UserDashboardActivity::class.java.name))
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }
}