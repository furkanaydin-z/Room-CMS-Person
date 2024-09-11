package org.fako.roomapp

import androidx.compose.ui.window.ComposeUIViewController
import org.fako.roomapp.appDatabase.DBFactory

fun MainViewController() = ComposeUIViewController {
    val db = DBFactory().createDatabase()
    val gitHubRepoDao = db.getDao()

    // App fonksiyonuna DAO'yu geçiriyoruz
    App(gitHubRepoDao)
}