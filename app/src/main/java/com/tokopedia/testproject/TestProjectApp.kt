package com.tokopedia.testproject

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class TestProjectApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        var realmConfiguration: RealmConfiguration = RealmConfiguration.Builder().
                name("testproject").
                deleteRealmIfMigrationNeeded().
                build()

        Realm.setDefaultConfiguration(realmConfiguration)
    }
}