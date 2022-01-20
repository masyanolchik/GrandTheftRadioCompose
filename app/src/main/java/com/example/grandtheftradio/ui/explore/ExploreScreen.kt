package com.example.grandtheftradio.ui.explore

import android.util.ArrayMap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import com.example.grandtheftradio.entities.Game
import com.example.grandtheftradio.entities.Radio
import com.example.grandtheftradio.radiochooser.RadioChooserViewModel
import com.example.grandtheftradio.ui.theme.ColorPrimary
import com.example.grandtheftradio.ui.theme.White
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@ExperimentalCoroutinesApi
@FlowPreview
@ExperimentalCoilApi
@Composable
fun ExploreScreen(viewModel: RadioChooserViewModel, radioClickListener: RadioClickListener) {
   val radioByGame by viewModel.radioByGame.observeAsState(ArrayMap<Game,List<Radio>>())
    if(radioByGame.values.count()>0) {
        val gameList = radioByGame.keys.toList()
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .background(ColorPrimary)
        ) {
            items(gameList) {
                Game(it, radioByGame[it], radioClickListener, gameList.indexOf(it))
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun Game(game : Game, stations:List<Radio>?, radioClickListener: RadioClickListener, gamePosition: Int) {
    Column (modifier = Modifier.background(ColorPrimary)){
        Text(
            text=game.gameName,
            fontSize = 24.sp,
            color= White,
            modifier = Modifier
                .wrapContentSize()
                .padding(start = 16.dp, top = 16.dp)
        )
        RadioStationList(
            list = stations ?: listOf(),
            radioClickListener = radioClickListener,
            gamePosition = gamePosition
        )
    }
}