package com.example.grandtheftradio.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import coil.annotation.ExperimentalCoilApi
import com.example.grandtheftradio.radiochooser.PlaybackState
import com.example.grandtheftradio.radiochooser.RadioChooserViewModel
import com.example.grandtheftradio.ui.explore.ExploreScreen
import com.example.grandtheftradio.ui.explore.RadioClickListener
import com.example.grandtheftradio.ui.favoritesscreen.FavoritesScreen
import com.example.grandtheftradio.ui.search.SearchScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
@ExperimentalCoilApi
@Composable
fun MainScreen(
    viewModel: RadioChooserViewModel,
    onDismiss: () -> Unit,
    onPlayPause: (PlaybackState) -> Unit,
    radioClickListener: RadioClickListener
){
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {BottomMenuBar(navController)}
    ){
        Box(modifier = Modifier.fillMaxSize()) {
            Navigation(navController = navController, viewModel = viewModel, radioClickListener = radioClickListener)
            PlayerMiniControls(
                modifier = Modifier.align(Alignment.BottomStart)
                    .padding(bottom = 65.dp),
                viewModel.isPlayerHidden,
                viewModel.isPlayerLoading,
                viewModel.currentRadio,
                viewModel.currentGame,
                viewModel.currentPlaybackState,
                onPlayPause = onPlayPause,
                onDismiss = onDismiss
            )
        }
    }
}

@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalCoilApi
@Composable
fun Navigation(navController: NavHostController, viewModel:RadioChooserViewModel, radioClickListener: RadioClickListener) {
    NavHost(navController = navController,
        startDestination = NavigationItem.Explore.route,
        modifier = Modifier.fillMaxHeight()
    ) {
        composable(NavigationItem.Explore.route) {
            ExploreScreen(viewModel, radioClickListener)
        }
        composable(NavigationItem.Search.route) {
            SearchScreen(viewModel)
        }
        composable(NavigationItem.Favorites.route) {
            FavoritesScreen(viewModel)
        }
    }
}