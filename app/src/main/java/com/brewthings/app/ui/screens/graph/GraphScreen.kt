package com.brewthings.app.ui.screens.graph

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.brewthings.app.ui.screens.navigation.Screen

@Composable
fun GraphScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Graph",
                color = Color.Red,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.clickable {
                    navController.navigate(route = Screen.Scanning.route){
                        popUpTo(Screen.Scanning.route) {
                            inclusive = true
                        }
                    }
                },
                text = "Click here to go back",
                color = Color.DarkGray,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
@Composable
@Preview(showBackground = true)
fun GraphScreenPreview() {
    GraphScreen(navController = rememberNavController())
}