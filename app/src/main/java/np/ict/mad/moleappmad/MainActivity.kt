package np.ict.mad.moleappmad

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import np.ict.mad.moleappmad.ui.theme.MoleAppMADTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current

            MoleAppMADTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(title = { Text("Wack-a-Mole") },
                            actions = {
                                IconButton(onClick = {
                                    val intent = Intent(context, Settings::class.java)
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
                    Whackamole(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Whackamole(modifier: Modifier = Modifier) {
    // This is your Root Layout Column
    Column(
        modifier = modifier
            .fillMaxSize(), // Fills the remaining screen space
        horizontalAlignment = Alignment.CenterHorizontally // Centers items horizontally
    ) {}
}

