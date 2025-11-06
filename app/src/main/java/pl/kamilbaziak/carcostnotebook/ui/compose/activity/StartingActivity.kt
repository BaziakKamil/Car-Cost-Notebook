package pl.kamilbaziak.carcostnotebook.ui.compose.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import pl.kamilbaziak.carcostnotebook.ui.activity.OldMainActivity
import pl.kamilbaziak.carcostnotebook.ui.compose.theme.CarCostNotebookTheme

class StartingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarCostNotebookTheme {
                StartingScreen()
            }
        }
    }
}

@Composable
fun StartingScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            context.startActivity(Intent(context, OldMainActivity::class.java))
        }) {
            Text("Old Version")
        }
        Button(onClick = {
            context.startActivity(Intent(context, MainActivity::class.java))
        }) {
            Text("New Version")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CarCostNotebookTheme {
        StartingScreen()
    }
}