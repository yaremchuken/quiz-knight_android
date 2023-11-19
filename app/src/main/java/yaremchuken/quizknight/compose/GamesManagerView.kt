package yaremchuken.quizknight.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import yaremchuken.quizknight.R
import yaremchuken.quizknight.activity.MAX_GAMES
import yaremchuken.quizknight.activity.MainActivity
import yaremchuken.quizknight.dimensions.FontDimensions
import yaremchuken.quizknight.entity.GameStatsEntity

@Composable
fun GamesManagerView(activity: MainActivity, games: List<GameStatsEntity>) {
    Column(
        Modifier
            .fillMaxSize()
            .background(colorResource(R.color.palette_cb)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        games.forEach { GameEntityView(activity, it) }
        for(i in games.size until MAX_GAMES) { GameEntityView(activity) }
    }
}

@Composable
fun GameEntityView(activity: MainActivity, game: GameStatsEntity? = null) {
    val color = if (game != null) R.color.button_primary else R.color.dark_gray
    Button(
        onClick = { activity.gameEntityClickListener(game)},
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(color)),
        border = BorderStroke(1.dp, colorResource(R.color.white))
    ) {
        Text(
            text = if (game != null) "${game.game} - ${game.original}/${game.studied}" else "new game",
            fontSize = FontDimensions.DEFAULT
        )
    }
}