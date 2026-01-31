package np.ict.mad.madmoleadvanced

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import np.ict.mad.madmoleadvanced.ui.theme.MoleAppMADTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val db = remember { AppDB.get(context) }
            val userId = intent.getIntExtra("USER_ID", -1)

            var currentUserName by remember { mutableStateOf("Guest") }

            LaunchedEffect(userId) {
                if (userId != -1) {
                    val user = db.dao().getUserById(userId)
                    if (user != null) {
                        currentUserName = user.username
                    }
                }
            }

            MoleAppMADTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(title = { Text("Wack-a-Mole - $currentUserName") },
                            actions = {
                                IconButton(onClick = {
                                    val intent = Intent(context, np.ict.mad.madmoleadvanced .LeaderboardPage::class.java)
                                    context.startActivity(intent)})
                                {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings"
                                    )
                                }

                            }
                        )
                    }
                ) { innerPadding ->
                    Whackamole(
                        modifier = Modifier.padding(innerPadding),
                        username = currentUserName, // Pass ID
                        db = db          // Pass Database
                    )
                }
            }
        }
    }
}

@Composable
fun Whackamole(modifier: Modifier = Modifier, username: String, db: AppDB) {

    var score by remember { mutableStateOf(0) }
    var time by remember { mutableStateOf(30) }
    var currentMoleIndex by remember { mutableStateOf((0..8).random()) }
    var activeGame by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("MolePrefs", Context.MODE_PRIVATE) }
    var highScore by remember { mutableStateOf(sharedPref.getInt("high_score", 0)) }


    LaunchedEffect(username) {
        if (username != "Guest") {
            highScore = db.dao().getHighScoreForUser(username) ?: 0
        }
    }

    LaunchedEffect(activeGame) {
        while (activeGame) {
            delay(1000L)
            time--

            if (time == 0) {
                activeGame = false
                if (username != "Guest" && score > highScore) {
                    // Call directly because LaunchedEffect is a coroutine scope
                    db.dao().insertScore(
                        Score(
                            userId = username, // The username String
                            score = score,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    highScore = score
                }
            }
        }
    }

    LaunchedEffect(activeGame) {
        while (activeGame == true) {
            currentMoleIndex = Random.nextInt(9)
            delay(Random.nextLong(700, 1001))
        }
        currentMoleIndex = -1

    }


    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = "Score: $score")
            Text(text = "Time: $time")
        }

        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(3)
        ) {
            items(9) { index ->
                Button(
                    onClick = {
                        if (index == currentMoleIndex){
                            score++
                            currentMoleIndex = (0..8).random()
                        }
                    },
                    modifier = Modifier.padding(4.dp)
                ) {
                    if (index == currentMoleIndex) {
                        Text("Mole")
                    }
                }
            }
        }

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center
        ){
            Button(onClick = {
                if (!activeGame) {
                    score = 0
                    time = 30
                    activeGame = true
                }
            }) {
                Text("start")
            }

            Button(onClick = {
                activeGame = false
                score = 0
                time = 30
                currentMoleIndex = -1 //so no mole shows up
            }) {
                Text("reset")
            }


        }

        if (time == 0 && activeGame == false) {
            Text(
                text = "GAME OVER",
            )
            Text(
                text = "Final Score: $score",
            )
        }

        Text(
            text = "High Score: $highScore"
        )

    }
}

