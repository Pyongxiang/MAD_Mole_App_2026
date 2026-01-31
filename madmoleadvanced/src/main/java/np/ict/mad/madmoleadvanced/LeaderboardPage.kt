package np.ict.mad.madmoleadvanced

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class LeaderboardPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val db = remember { AppDB.get(context) }
            var scoreList by remember { mutableStateOf(emptyList<Score>()) }

            // Load scores once when activity opens
            LaunchedEffect(Unit) {
                scoreList = db.dao().getAllScores()
            }

            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Leaderboard")

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(scoreList) { item ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Displays the username (userId) and the score
                            Text(text = "${item.userId}: ")
                            Text(text = "${item.score}")
                        }
                    }
                }
            }
        }
    }
}