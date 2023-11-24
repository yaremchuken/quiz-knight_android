package yaremchuken.quizknight.compose.gamesmanager

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import yaremchuken.quizknight.R
import yaremchuken.quizknight.activity.MAX_GAMES
import yaremchuken.quizknight.dimensions.FontDimensions
import yaremchuken.quizknight.dimensions.UIDefaults
import yaremchuken.quizknight.entity.GameStatsEntity
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

@Preview
@Composable
fun GamesManagerView(
    games: List<GameStatsEntity> = listOf(),
    runGame: (game: GameStatsEntity) -> Unit = { _: GameStatsEntity -> {} },
    createGame: (name: String, original: Locale, studied: Locale) -> Unit = { _: String, _: Locale, _: Locale -> {} }
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(colorResource(R.color.palette_cb)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        games.sortedByDescending { it.playedAt }.forEach { GameBtnView({ runGame(it) }, it) }
        for(i in games.size until MAX_GAMES) { GameBtnView({ showDialog = true }) }
    }
    GameCreationDialog(
        { showDialog = false },
        createGame,
        games.map { it.game },
        showDialog
    )
}

@Preview
@Composable
fun GameBtnView(
    onClick: () -> Unit = {},
    game: GameStatsEntity? = null
) {
    val color = if (game != null) R.color.button_primary else R.color.dark_gray

    val playedAt = if (game != null) Instant.ofEpochSecond(game.playedAt) else Instant.EPOCH
    val format = SimpleDateFormat("dd MMM", Locale.getDefault())

    val gameNameText =
        if (game != null) "${game.game} - ${game.original.language.uppercase()}/${game.studied.language.uppercase()}"
        else stringResource(R.string.new_game_btn_title)

    val gameStatsText = "${format.format(Date.from(playedAt))} - ${game?.gold}g"

    Button(
        onClick = { onClick() },
        Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_default),
                vertical = dimensionResource(R.dimen.padding_small)
            )
            .requiredHeightIn(min = 52.dp),
        shape = UIDefaults.ROUNDED_CORNER,
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(color)),
        border = BorderStroke(1.dp, colorResource(R.color.white))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = gameNameText,
                fontSize = FontDimensions.LARGE
            )
            if (game != null) {
                Text(
                    text = gameStatsText,
                    fontSize = FontDimensions.LARGE
                )
            }
        }
    }
}