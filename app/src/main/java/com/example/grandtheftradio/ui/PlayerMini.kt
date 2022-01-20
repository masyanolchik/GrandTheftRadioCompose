package com.example.grandtheftradio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.grandtheftradio.R
import com.example.grandtheftradio.entities.Game
import com.example.grandtheftradio.entities.Radio
import com.example.grandtheftradio.radiochooser.PlaybackState
import com.example.grandtheftradio.ui.theme.ColorPrimaryDark
import com.example.grandtheftradio.ui.theme.White
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.Flow

@Composable
fun PlayerMiniControls(
    modifier: Modifier = Modifier,
    isDismissedLiveData: LiveData<Boolean>,
    radioIsLoadingFlow: Flow<Boolean>,
    currentRadioLiveData: LiveData<Radio>,
    currentGameLiveData: LiveData<Game>,
    currentPlayState: LiveData<PlaybackState>,
    onDismiss: () -> Unit,
    onPlayPause: (PlaybackState) -> Unit
){
    val currentGame by currentGameLiveData.observeAsState(Game())
    val currentRadio by currentRadioLiveData.observeAsState(Radio())
    val isPlayerLoading = remember {radioIsLoadingFlow}.collectAsState(initial = false)
    DismissableWrapper(modifier = modifier,onDismiss = onDismiss,isDismissedLiveData = isDismissedLiveData) {
        Surface(
            color = Color.Transparent,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                        .background(ColorPrimaryDark)
                ) {
                        GlideImage(
                            imageModel = currentRadio.picLink,
                            placeHolder = ImageBitmap.imageResource(R.drawable.radio_placeholder),
                            error = ImageBitmap.imageResource(R.drawable.radio_sa_ls),
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(1f)
                                .weight(0.15f)
                        )

                    PlaybackNowPlaying(artistName = currentGame.gameName, trackName = currentRadio.name)
                    PlaybackPlayPause(currentPlaybackState = currentPlayState,modifier = Modifier.weight(0.15f), onPlayPause)
                }
                if(isPlayerLoading.value) {
                    LinearProgressIndicator(
                        color = White,
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.PlaybackNowPlaying(artistName:String,trackName:String) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(8.dp)
            .weight(0.7f)
    ) {
        Text(
            trackName,
            color = White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold)
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                artistName,
                maxLines = 1,
                color = White,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
private fun PlaybackPlayPause(
    currentPlaybackState: LiveData<PlaybackState>,
    modifier: Modifier,
    onPlayPause: (PlaybackState) -> Unit,
) {
    val playState by currentPlaybackState.observeAsState(PlaybackState.PAUSED)
    IconButton(
        onClick = {
            if(playState != PlaybackState.LOADING && playState!=PlaybackState.ERROR) {
                onPlayPause(if (playState == PlaybackState.PLAYING) PlaybackState.PAUSED else PlaybackState.PLAYING)
            }
        },
        modifier = Modifier
            .padding(8.dp)
            .then(modifier)
    ) {
        Icon(
            imageVector = when (playState) {
                PlaybackState.ERROR -> Icons.Filled.ErrorOutline
                PlaybackState.PLAYING -> Icons.Filled.Pause
                PlaybackState.PAUSED -> Icons.Filled.PlayArrow
                else -> Icons.Filled.HourglassBottom
            },
            tint = White,
            modifier = Modifier.size(36.dp),
            contentDescription = null
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DismissableWrapper(
    modifier: Modifier = Modifier,
    isDismissedLiveData: LiveData<Boolean>,
    onDismiss: () -> Unit,
    directions: Set<DismissDirection> = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
    content: @Composable () -> Unit
) {
    val isDismissed by isDismissedLiveData.observeAsState(true)
    if(!isDismissed){
        val dismissState = rememberDismissState {value ->
            if (value != DismissValue.Default) {
                onDismiss.invoke()
            }
            true
        }
        SwipeToDismiss(
            modifier = modifier,
            state = dismissState,
            directions = directions,
            background = {},
            dismissContent = { content() }
        )
    }

}