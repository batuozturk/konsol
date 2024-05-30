package com.batuhan.core.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import net.openid.appauth.AuthState

@ProvidedTypeConverter
class AuthStateConverter {
    @TypeConverter
    fun fromAuthState(authState: AuthState): String {
        return authState.jsonSerializeString()
    }

    @TypeConverter
    fun toAuthState(authStateString: String): AuthState {
        return AuthState.jsonDeserialize(authStateString)
    }
}
