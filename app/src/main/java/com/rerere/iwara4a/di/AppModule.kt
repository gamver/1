package com.rerere.iwara4a.di

import android.content.Context
import androidx.room.Room
import com.rerere.iwara4a.data.dao.AppDatabase
import com.rerere.iwara4a.data.model.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSessionManager() = SessionManager()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, AppDatabase::class.java, "iwaradb"
    ).build()
}