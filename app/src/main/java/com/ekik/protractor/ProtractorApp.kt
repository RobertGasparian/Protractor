package com.ekik.protractor

import android.app.Application
import com.ekik.protractor.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ProtractorApp: Application() {
    override fun onCreate() {
        super.onCreate()
        //Koin
        startKoin {
            androidLogger()
            androidContext(this@ProtractorApp)
            modules(appModule)
        }
    }
}