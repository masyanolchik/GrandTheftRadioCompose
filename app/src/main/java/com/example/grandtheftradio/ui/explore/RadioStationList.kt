package com.example.grandtheftradio.ui.explore

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.grandtheftradio.R
import com.example.grandtheftradio.entities.Radio
import com.example.grandtheftradio.ui.theme.White
import com.google.android.material.composethemeadapter.MdcTheme
import com.skydoves.landscapist.glide.GlideImage

interface RadioClickListener{
    fun onClick(radio:Radio)
}

@Composable
fun getContext(): Context = LocalContext.current

@ExperimentalCoilApi
@Composable
fun RadioStationList(list: List<Radio>, radioClickListener: RadioClickListener, gamePosition:Int) {
    LazyRow(
        modifier = Modifier.padding(start=8.dp, top=8.dp, end=8.dp)
    ) {
        items(list) {
            RadioStation(it,radioClickListener, gamePosition, list.indexOf(it))
        }
    }
}

@ExperimentalCoilApi
@Composable
fun RadioStation(radioStation: Radio, radioClickListener: RadioClickListener, gamePosition: Int, radioPosition: Int) {
    MdcTheme {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .wrapContentSize()
                .clickable(onClick = {
                    radioClickListener.onClick(radioStation)
                })
                .padding(end=8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(150.dp)
            ) {
                Image(
                    rememberImagePainter(
                        ContextCompat.getDrawable(
                            getContext(),
                            R.drawable.customborder
                        )
                    ),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .width(120.dp)
                        .height(150.dp)
                )
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    RadioIcon(radioStation.picLink)
                    RadioName(radioStation.name)
                }
            }
        }
    }
}

@Composable
fun RadioIcon(picLink: String) {
    GlideImage(
        imageModel = picLink,
        contentScale = ContentScale.Inside,
        modifier = Modifier
            .height(100.dp)
            .width(100.dp)
            .padding(8.dp)
    )
}

@Composable
fun RadioName(radioName: String) {
    AutoSizeText(
        text = radioName,
        textStyle = TextStyle(
            color = White,
            fontSize = 15.sp,
        ),
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun AutoSizeText(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    var scaledTextStyle by remember { mutableStateOf(textStyle) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text,
        modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        style = scaledTextStyle,
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                scaledTextStyle =
                    scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        }
    )
}