package com.batuhan.core.di

import android.content.Context
import androidx.room.Room
import com.batuhan.core.db.AuthDatabase
import com.batuhan.core.db.AuthStateConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAuthDatabase(@ApplicationContext context: Context): AuthDatabase {
        return Room.databaseBuilder(context, AuthDatabase::class.java, "authusers")
            .addTypeConverter(AuthStateConverter())
            .build()
    }
}
