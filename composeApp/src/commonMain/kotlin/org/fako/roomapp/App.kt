package org.fako.roomapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.fako.roomapp.appDatabase.GitHubRepoDao
import org.fako.roomapp.appDatabase.GithubRepoEntity
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import roomapp.composeapp.generated.resources.Res
import roomapp.composeapp.generated.resources.compose_multiplatform

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App(gitHubRepoDao: GitHubRepoDao) {
    val repos by gitHubRepoDao.getAllAsFlow().collectAsState(initial = emptyList())

    MaterialTheme {
        val navController = rememberNavController()
        NavHost(navController, startDestination = "main") {
            composable("main") {
                MainScreen(
                    state = repos,
                    onItemClick = { user ->
                        navController.navigate("detail/${user.id}")
                    },
                    onAddClick = {
                        navController.navigate("add")
                    }
                )
            }
            composable("detail/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                DetailScreen(userId = id, gitHubRepoDao = gitHubRepoDao) {
                    navController.navigateUp()
                }
            }
            composable("add") {
                AddScreen(gitHubRepoDao = gitHubRepoDao) {
                    navController.popBackStack() // Geri dönme işlemi
                }
            }
        }
    }
}
@Composable
fun MainScreen(state:List<GithubRepoEntity>,
               onItemClick:(GithubRepoEntity)-> Unit,
               onAddClick: () -> Unit) {

    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                content = { Icon(Icons.Default.Add, contentDescription = "Add") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()

        ) {
            items(state, key = { list -> list.id }) { user ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    backgroundColor = androidx.compose.ui.graphics.Color.Black,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth()
                        .clickable {
                            onItemClick(user)
                        }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f).padding(8.dp)) {
                            Text(text = user.title, style = MaterialTheme.typography.h6)
                            Text(text = user.description, style = MaterialTheme.typography.body2)
                        }
                        IconButton(onClick = {


                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailScreen(userId:Int,gitHubRepoDao: GitHubRepoDao, goItemBack:() -> Unit) {
    val scope = rememberCoroutineScope()

    val repo = remember { mutableStateOf<GithubRepoEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        scope.launch {
            val user = gitHubRepoDao.getAll().find { it.id == userId }
            user?.let {
                repo.value = it
                title = it.title
                description = it.description
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(repo.value?.title ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = goItemBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                repo.value?.let {
                    val updatedRepo = it.copy(title = title, description = description)
                    scope.launch {
                        gitHubRepoDao.update(updatedRepo) // Update işlemi burada yapılıyor
                    }
                    goItemBack() // Geri dönme işlemi
                }
            }) {
                Text("Save")
            }
        }
    }
}
@Composable
fun AddScreen(gitHubRepoDao: GitHubRepoDao, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Repo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                scope.launch {
                    gitHubRepoDao.insert(GithubRepoEntity(id = 0, title = title, description = description))
                    onBack() // Geri dönme işlemi
                }
            }) {
                Text("Save")
            }
        }
    }
}