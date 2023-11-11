package com.example.permissions

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.permissions.data.AppDatabase
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext

import javax.inject.Singleton

private const val MAPKIT_API_KEY = "7259d672-5e46-4807-b174-d17893cc36a5"

    @Module
    @InstallIn(SingletonComponent::class)
    object AppModule {

        @Singleton // Tell Dagger-Hilt to create a singleton accessible everywhere in ApplicationComponent (i.e. everywhere in the application)
        @Provides
        fun providePhotoDatabase(
            @ApplicationContext app: Context
        ) = Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "photos_db"
        ).build() // The reason we can construct a database for the repo

        @Singleton
        @Provides
        fun providePhotoDao(db: AppDatabase) = db.getPhotoDao() // The reason we can implement a Dao for the database

        @Singleton
        @Provides
        fun provideMapKit(
            @ApplicationContext app: Context
        ): MapKit {
            MapKitFactory.setApiKey(MAPKIT_API_KEY)
            MapKitFactory.initialize(app)
            return MapKitFactory.getInstance()
        }

        @Singleton
        @Provides
        fun provideContext(
            @ApplicationContext app: Context
        ): Context {
            return app
        }
    }
