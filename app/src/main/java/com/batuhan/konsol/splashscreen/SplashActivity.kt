package com.batuhan.konsol.splashscreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.batuhan.konsol.MainActivity
import com.batuhan.theme.KonsolTheme
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun attachBaseContext(newBase: Context) {
        val configuration = newBase.resources.configuration
        val sharedPreferences = newBase.getSharedPreferences("lang_pref", MODE_PRIVATE)
        val localeList = LocaleList.forLanguageTags(sharedPreferences.getString("selected_lang", "en"))
        configuration.setLocales(localeList)
        val newContext = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(newContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        setContent {
            val viewModel = hiltViewModel<SplashViewModel>()
            val context = LocalContext.current
            KonsolTheme {
                SplashScreen(
                    viewModel = viewModel,
                    navigate = { screen ->
                        navigateToMainActivity(context, screen)
                    }
                )
            }
        }
    }

    private fun navigateToMainActivity(context: Context, screen: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("screen_name", screen)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}
