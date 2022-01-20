package com.example.grandtheftradio.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grandtheftradio.R
import com.example.grandtheftradio.entities.Radio
import com.example.grandtheftradio.radiochooser.RadioChooserViewModel
import com.example.grandtheftradio.ui.theme.ColorPrimary
import com.example.grandtheftradio.ui.theme.ColorPrimaryDark
import com.example.grandtheftradio.ui.theme.White
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
@Composable
fun SearchScreen(viewModel: RadioChooserViewModel) {
    var stations by remember { mutableStateOf(viewModel.searchQuery(query = ""))}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPrimary)
            .padding(4.dp)
    ) {
        Text (
            text = "Search",
            fontSize = 24.sp,
            color= White,
            modifier = Modifier
                .wrapContentSize()
                .padding(start = 12.dp, top = 8.dp)
        )

        var text by remember{ mutableStateOf("")}
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20)
        ) {
            Column(modifier = Modifier.background(ColorPrimary)){
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White),
                    value = text,
                    onValueChange = {
                        text = it
                        stations = viewModel.searchQuery(query = it)
                    },
                    label = {
                        Text("Station, game")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "")
                    },
                    textStyle = TextStyle(color = ColorPrimary),
                )
            }
        }

        val tags by viewModel.tags.observeAsState()
        val selectedTags by viewModel.selectedTags.collectAsState()
        LazyRow (modifier = Modifier.padding(start = 8.dp)){
            items(tags ?: listOf()) {
                Surface(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable(onClick = {
                            if (selectedTags.contains(it)) {
                                viewModel.removeSelectedTag(it)
                                stations = viewModel.searchQuery(query = text)
                            } else {
                                viewModel.addSelectedTag(it)
                                stations = viewModel.searchQuery(query = text)
                            }
                        }),
                    shape = RoundedCornerShape(100),
                    color = if(selectedTags.contains(it)) ColorPrimaryDark else Color.Gray,
                ) {
                    Text(
                        it.tagName,
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp,end=8.dp)
                    )
                }
            }
        }


        LazyColumn(modifier =
            Modifier.padding(start = 8.dp, bottom= 60.dp, top = 8.dp)) {
            items(stations){
                TrackRow(viewModel,it)
            }
        }
    }
}

@Composable
fun SearchBar() {
    var text by remember{ mutableStateOf("")}
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(20)
    ) {
        Column(modifier = Modifier.background(Color.Black)){
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                value = text,
                onValueChange = {
                    text = it

                },
                label = {
                    Text("Station, game")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "")
                },
                textStyle = TextStyle(color = Color.Black),
            )
        }
    }
}

@ExperimentalCoroutinesApi
@FlowPreview
@Composable
fun ChipGroup(viewModel: RadioChooserViewModel) {
    val tags by viewModel.tags.observeAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    LazyRow (modifier = Modifier.padding(start = 8.dp)){
        items(tags ?: listOf()) {
            Surface(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable(onClick = {
                        if (selectedTags.contains(it)) {
                            viewModel.removeSelectedTag(it)
                        } else {
                            viewModel.addSelectedTag(it)
                        }
                    }),
                shape = RoundedCornerShape(100),
                color = if(selectedTags.contains(it)) colorResource(id = R.color.colorPrimaryDark) else Color.Gray,
            ) {
                Text(
                    it.tagName,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp,end=8.dp)
                )
            }
        }
    }

}

@ExperimentalCoroutinesApi
@FlowPreview
@Composable
fun TrackRow(viewModel: RadioChooserViewModel, radio: Radio) {
    var isFavorite by remember{mutableStateOf(radio.favorite)}
    Column(modifier = Modifier
        .clickable{
            viewModel.playRadio(radio)
        }) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(72.dp)
                .fillMaxWidth()
        ) {
            GlideImage(
                imageModel = radio.picLink,
                placeHolder = ImageBitmap.imageResource(R.drawable.radio_placeholder),
                error = painterResource(R.drawable.ic_round_error_outline),
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .width(40.dp)
                    .height(40.dp)
                    .weight(0.15f)
            )

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.7f)
            ) {
                Text(
                    radio.name,
                    maxLines = 1,
                    fontSize = 16.sp,
                    color = White,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    radio.game.gameName,
                    maxLines = 1,
                    fontSize = 16.sp,
                    color = White,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            IconButton(
                onClick = {
                    viewModel.updateRadio(radio.apply{
                        favorite = !isFavorite
                    })
                    isFavorite = !isFavorite
                },
                modifier = Modifier.weight(0.15f)) {
                Icon(
                    imageVector = if(isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    modifier = Modifier.size(24.dp),
                    contentDescription = null,
                    tint = White,
                )
            }
        }
    }

}