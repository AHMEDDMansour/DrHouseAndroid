import androidx.compose.foundation.layout.* // For layout components like Column, Row, etc.
import androidx.compose.foundation.lazy.LazyRow // For LazyRow
import androidx.compose.foundation.lazy.items // For LazyRow items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable // For composables
import androidx.compose.ui.Alignment // For alignment
import androidx.compose.ui.Modifier // For Modifier
import androidx.compose.ui.graphics.Color // For colors
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview // For previews
import androidx.compose.ui.unit.dp // For defining dimensions

@Composable
fun WellBeingTrackerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title Section
        Text(
            text = "Well-Being Tracker",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Steps Section
        WellBeingCard(
            title = "Steps",
            value = "10,000",
            icon = Icons.Default.Hotel,
            unit = "steps"
        )

        // Water Intake Section
        WellBeingCard(
            title = "Water",
            value = "2.5",
            icon = Icons.Default.Hotel,
            unit = "liters"
        )

        // Sleep Hours Section
        WellBeingCard(
            title = "Sleep",
            value = "8",
            icon = Icons.Default.Hotel,
            unit = "hours"
        )

        // Coffee Cups Section
        WellBeingCard(
            title = "Coffee Cups",
            value = "3",
            icon = Icons.Default.Coffee,
            unit = "cups"
        )

        // Workout Section
        WellBeingCard(
            title = "Workout",
            value = "45",
            icon = Icons.Default.FitnessCenter,
            unit = "minutes"
        )

        Spacer(modifier = Modifier.weight(1f))

        // Save Button
        Button(
            onClick = { /* Save data action */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Save Data")
        }
    }
}

@Composable
fun WellBeingCard(title: String, value: String, icon: ImageVector, unit: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "$value $unit", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WellBeingDashboardPreview() {
    MaterialTheme {
        WellBeingTrackerScreen()
    }
}
