package com.batuhan.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val KonsolFontFamily = FontFamily(
    Font(R.font.nunito_black, weight = FontWeight.Black),
    Font(R.font.nunito_bold, weight = FontWeight.Bold),
    Font(R.font.nunito_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.nunito_light, weight = FontWeight.Light),
    Font(R.font.nunito_medium, weight = FontWeight.Medium),
    Font(R.font.nunito_regular, weight = FontWeight.Normal)
)

val GoogleSignInFontFamily = FontFamily(
    Font(R.font.roboto_medium, weight = FontWeight.Black),
    Font(R.font.roboto_medium, weight = FontWeight.Bold),
    Font(R.font.roboto_medium, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.roboto_medium, weight = FontWeight.Light),
    Font(R.font.roboto_medium, weight = FontWeight.Medium),
    Font(R.font.roboto_medium, weight = FontWeight.Normal)
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = KonsolFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
