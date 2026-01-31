package np.ict.mad.madmoleadvanced

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.launch
import np.ict.mad.madmoleadvanced.ui.theme.MoleAppMADTheme

class LoginPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoleAppMADTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDB.get(context) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Wack-a-Mole Login")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(value = username, onValueChange = { username = it }, label = { Text("Username") })

        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation() // Hides text
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                val user = db.dao().signIn(username, password)
                if (user != null) {
                    val intent = Intent(context, MainActivity::class.java).apply {
                        putExtra("USER_ID", user.userId)
                    }
                    context.startActivity(intent)
                }
            }
        }) {
            Text("Sign In")
        }

        // Sign Up Logic
        Button(onClick = {
            scope.launch {
                val newUser = User(username = username, pass = password)
                db.dao().signUp(newUser)
            }
        }) {
            Text("Sign Up")
        }
    }
}

@Entity(tableName = "Users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val username: String,
    val pass: String
)

@Entity(
    tableName = "Score",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Score(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface UserDao {
    @Insert
    suspend fun signUp(user: User)

    @Query("SELECT * FROM Users WHERE username = :u AND pass = :p LIMIT 1")
    suspend fun signIn(u: String, p: String): User?

    @Query("SELECT * FROM Users WHERE userId = :id LIMIT 1")
    suspend fun getUserById(id: Int): User?
}

@Database(entities = [User::class, Score::class], version = 1)
abstract class AppDB : RoomDatabase() {
    abstract fun dao(): UserDao
    companion object {
        fun get(context: Context) = Room.databaseBuilder(
            context, AppDB::class.java, "db"
        ).build()
    }
}