package yaremchuken.quizknight.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import yaremchuken.quizknight.R
import yaremchuken.quizknight.activity.MAX_GAMES
import yaremchuken.quizknight.dimensions.FontDimensions
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
    var showDialog = remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(colorResource(R.color.palette_cb)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        games.sortedByDescending { it.playedAt }.forEach { GameBtnView({ runGame(it) }, it) }
        for(i in games.size until MAX_GAMES) { GameBtnView({ showDialog.value = true }) }
    }
    GameCreationDialog(
        { showDialog.value = false },
        createGame,
        games.map { it.game },
        showDialog.value
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
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .height(if (game == null) 52.dp else 76.dp),
        shape = RoundedCornerShape(12.dp),
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

@Preview
@Composable
fun GameCreationDialog(
    onDismiss: () -> Unit = {},
    onApprove: (name: String, original: Locale, studied: Locale) -> Unit = { _: String, _: Locale, _: Locale -> {} },
    existingGames: List<String> = listOf(),
    visible: Boolean = true
) {
    var gameName by remember { mutableStateOf("") }
    var original by remember { mutableStateOf(Locale("ru")) }
    var studied by remember { mutableStateOf(Locale.ENGLISH) }
    var error by remember { mutableStateOf("") }

    val nameExistsError = stringResource(R.string.such_name_already_exists)

    if (visible) {
        Dialog(onDismissRequest = { gameName = ""; error = ""; onDismiss() }) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.palette_b7))
            ) {
                Text(
                    text = stringResource(R.string.create_new_game),
                    Modifier
                        .padding(vertical = 10.dp)
                        .align(Alignment.CenterHorizontally),
                    fontSize = FontDimensions.MEDIUM_X,
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = gameName,
                    onValueChange = { gameName = it },
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    textStyle = TextStyle.Default.copy(fontSize = FontDimensions.MEDIUM),
                    placeholder = { Text(text = stringResource(R.string.game_name)) },
                    shape = RoundedCornerShape(12.dp)
                )
                if (error.isNotBlank()) {
                    Text(
                        text = error,
                        Modifier.padding(start = 16.dp),
                        color = colorResource(R.color.red),
                        fontSize = FontDimensions.SMALL
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.original_language),
                            Modifier
                                .padding(all = 10.dp)
                                .align(Alignment.CenterHorizontally),
                            fontSize = FontDimensions.LARGE,
                            fontWeight = FontWeight.Bold
                        )
                        Column(modifier = Modifier.selectableGroup()) {
                            listOf(Locale("ru"), Locale("en")).forEach { locale ->
                                LanguageSelectBtn(locale, { original = locale }, locale == original)
                            }
                        }
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.studied_language),
                            Modifier
                                .padding(all = 10.dp)
                                .align(Alignment.CenterHorizontally),
                            fontSize = FontDimensions.LARGE,
                            fontWeight = FontWeight.Bold
                        )
                        Column(modifier = Modifier.selectableGroup()) {
                            listOf(Locale("en"), Locale("ru")).forEach { locale ->
                                LanguageSelectBtn(locale, { studied = locale }, locale == studied)
                            }
                        }
                    }
                }
                Button(
                    onClick = { if (existingGames.contains(gameName)) error = nameExistsError
                                else { onApprove(gameName, original, studied); onDismiss() } },
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.button_primary)),
                    border = BorderStroke(1.dp, colorResource(R.color.white)),
                    enabled = gameName.isNotBlank()
                ) {
                    Text(
                        text = stringResource(R.string.btn_title_lets_go),
                        fontSize = FontDimensions.LARGE
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageSelectBtn(
    locale: Locale,
    onClick: () -> Unit,
    selected: Boolean = false
) {

    val flagSrc = when (locale.language) {
        "ru" -> R.drawable.ic_flag_ru
        "en" -> R.drawable.ic_flag_uk
        else -> R.drawable.ic_flag_us
    }

    val colorSrc = if (selected) R.color.palette_dd else R.color.white

    Row(
        Modifier
            .padding(bottom = 10.dp)
            .background(colorResource(colorSrc), shape = RoundedCornerShape(12.dp))
            .border(if (selected) 1.dp else 0.dp, colorResource(R.color.black), RoundedCornerShape(12.dp))
            .selectable(selected, true, onClick = { onClick() })
    ) {
        Text(
            text = locale.language.uppercase(),
            Modifier
                .align(alignment = Alignment.CenterVertically)
                .padding(start = 10.dp, top = 10.dp, end = 30.dp, bottom = 10.dp),
            fontSize = FontDimensions.LARGE,
            fontWeight = FontWeight.Bold
        )
        Image(
            bitmap = ImageBitmap.imageResource(flagSrc),
            contentDescription = locale.language.uppercase(),
            Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}