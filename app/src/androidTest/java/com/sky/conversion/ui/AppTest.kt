package com.sky.conversion.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sky.conversion.utils.LIST_VIEW
import com.sky.conversion.utils.getCurrencyApp
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppTest {

    @get:Rule
    val testRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun test_symbol_page_load() {
        IdlingRegistry.getInstance().register(testRule.activity.getCurrencyApp().idlingRes)
        testRule.onNodeWithText("Search").assertExists()
        testRule.onNodeWithText("AED").assertExists()
    }

    @Test
    fun test_search_currency_code() {
        IdlingRegistry.getInstance().register(testRule.activity.getCurrencyApp().idlingRes)
        testRule.onNodeWithText("Search").performTextInput("EUR")

        testRule.onNode(hasContentDescription(LIST_VIEW))
            .onChildren()
            .filter(hasText("EUR"))
            .assertCountEquals(1)

        testRule.onNodeWithText("Search").performTextClearance()
        testRule.onNodeWithText("Search").performTextInput("INR")

        testRule.onNode(hasContentDescription(LIST_VIEW))
            .onChildren()
            .filter(hasText("INR"))
            .assertCountEquals(1)
    }

    @Test
    fun test_select_currency_and_show_conversion_screen() {
        IdlingRegistry.getInstance().register(testRule.activity.getCurrencyApp().idlingRes)
        testRule.onNodeWithText("Search").performTextInput("EUR")

        testRule.onNode(hasContentDescription(LIST_VIEW))
            .onChildren()
            .filterToOne(hasText("EUR"))
            .performClick()

        testRule.onNodeWithText("Enter the Amount").assertExists()
    }

    @Test
    fun test_conversion() {
        IdlingRegistry.getInstance().register(testRule.activity.getCurrencyApp().idlingRes)
        testRule.onNodeWithText("Search").performTextInput("EUR")

        testRule.onNode(hasContentDescription(LIST_VIEW))
            .onChildren()
            .filterToOne(hasText("EUR"))
            .performClick()

        testRule.onNodeWithText("Enter the Amount").assertExists()

        testRule.onNodeWithText("Enter the Amount").performTextInput("10")

        testRule.onNode(hasContentDescription(LIST_VIEW))
            .performScrollToNode(hasText("ZWL"))
            .assertExists()
    }

}