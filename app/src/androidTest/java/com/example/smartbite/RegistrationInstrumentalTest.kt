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
import com.example.smartbite.view.RegistrationActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<RegistrationActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testSuccessfulRegistrationNavigatesToLogin() {

        // ✅ unique email every run (firebase blocks duplicate emails)
        val uniqueEmail = "ram${System.currentTimeMillis()}@gmail.com"

        // Fill form (use tags from your UI)
        composeRule.onNodeWithTag("fullName", useUnmergedTree = true)
            .performTextInput("Ram Shrestha")

        composeRule.onNodeWithTag("email", useUnmergedTree = true)
            .performTextInput(uniqueEmail)

        composeRule.onNodeWithTag("password", useUnmergedTree = true)
            .performTextInput("passwords")

        composeRule.onNodeWithTag("confirmPassword", useUnmergedTree = true)
            .performTextInput("passwords")

        // Click register button (your tag is "register")
        composeRule.onNodeWithTag("register", useUnmergedTree = true)
            .performClick()

        // Wait until it navigates to LoginActivity (your code opens LoginActivity)
        composeRule.waitUntil(timeoutMillis = 15000) {
            try {
                intended(hasComponent(LoginActivity::class.java.name))
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }
}