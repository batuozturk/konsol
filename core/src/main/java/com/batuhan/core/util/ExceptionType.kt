package com.batuhan.core.util

enum class ExceptionType(val code: Int) {
    // Firebase Exceptions

    FIREBASE_API_EXCEPTION(1200),

    // Google Cloud Exceptions

    GOOGLE_CLOUD_API_EXCEPTION(1500),

    // AppAuth Exceptions

    APPAUTH_INTERNAL_ERROR(2000),

    APPAUTH_TOKEN_REQUEST_EXCEPTION(2001),

    APPAUTH_SERVICE_CONFIG_EXCEPTION(2002),

    APPAUTH_SERVICE_CONFIG_INVALID(2003),

    APPAUTH_REFRESH_TOKEN_EXCEPTION(2004),

    // Other Exceptions
    ROOM_DB_ERROR(3000)
}
