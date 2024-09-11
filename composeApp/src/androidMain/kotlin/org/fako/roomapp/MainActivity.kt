package org.fako.roomapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.fako.roomapp.appDatabase.AppDatabase
import org.fako.roomapp.appDatabase.DBFactory
import org.fako.roomapp.appDatabase.GitHubRepoDao

class MainActivity : ComponentActivity() {
    private lateinit var gitHubRepoDao: GitHubRepoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Veritabanını başlatma
        val db: AppDatabase = DBFactory(applicationContext).createDatabase()
        gitHubRepoDao = db.getDao()

        setContent {
            // App fonksiyonuna DAO'yu geçiriyoruz
            App(gitHubRepoDao)
        }
    }
}