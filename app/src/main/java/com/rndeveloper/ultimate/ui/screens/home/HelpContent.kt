package com.rndeveloper.ultimate.ui.screens.home

import android.os.Build.VERSION.SDK_INT
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.rndeveloper.ultimate.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HelpContent(onSetHelpValue: (Boolean) -> Unit) {
    val onBoarding = listOf(
        HelpExample(
            title = null,
            description = null,
            image = R.drawable.ic_nothing,
        ),
        HelpExample(
            title = R.string.home_text_show_spots,
            description = R.string.home_text_help_description_two,
            image = R.drawable.tutorial_1_show_spots,
            imageVector = Icons.Default.Visibility
        ),
        HelpExample(
            title = R.string.home_text_navigate,
            description = R.string.home_text_help_description_three,
            image = R.drawable.tutorial_2_navigate,
            imageVector = Icons.Default.Navigation
        ),
        HelpExample(
            title = R.string.home_text_add_spot,
            description = R.string.home_text_help_description_four,
            image = R.drawable.tutorial_3_add_spot,
            imageVector = Icons.Default.AddLocationAlt
        ),
        HelpExample(
            title = R.string.home_text_park_your_car,
            description = R.string.home_text_help_description_five,
            image = R.drawable.tutorial_4_park_car,
            imageVector = Icons.Default.DirectionsCar
        ),
        HelpExample(
            title = R.string.home_text_show_ad,
            description = R.string.home_text_help_description_six,
            image = R.drawable.tutorial_5_show_ad,
            imageVector = Icons.Default.Slideshow
        ),
    )
    val pagerState = rememberPagerState { onBoarding.size }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 20.dp
    ) {
        Box {
            HorizontalPager(state = pagerState) {

                if (pagerState.currentPage == 0) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 18.dp, start = 18.dp, end = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.ic_ultimate_foreground),
                            contentDescription = R.drawable.ic_ultimate_foreground.toString(),
                            modifier = Modifier.size(160.dp)
                        )
                        Text(
                            text = stringResource(R.string.login_text_paparcar),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        Text(
                            text = stringResource(id = R.string.home_text_help_description_one),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 5.dp)
                        )

                    }

                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    GifImage(
                        idImage = onBoarding[it].image,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 5.dp)
                    ) {
                        Icon(
                            imageVector = onBoarding[it].imageVector,
                            contentDescription = onBoarding[it].imageVector.toString(),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        onBoarding[it].title?.let { title ->
                            Text(
                                text = stringResource(id = title),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold, fontSize = 20.sp,
                                ),
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = onBoarding[it].imageVector,
                            contentDescription = onBoarding[it].imageVector.toString(),
                            tint = Color.Transparent
                        )
                    }
                    onBoarding[it].description?.let { description ->
                        Text(
                            text = stringResource(id = description),
                            textAlign = TextAlign.Justify,
                            fontSize = 14.sp

                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                val scope = rememberCoroutineScope()
                if (pagerState.currentPage != 5) {
                    NextContent(
                        onSetHelpValue,
                        onBoarding,
                        pagerState
                    ) { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }
                } else {
                    ElevatedButton(
                        onClick = { onSetHelpValue(false) },
                    ) {
                        Text(text = stringResource(R.string.home_text_let_s_go), fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NextContent(
    onSetHelpValue: (Boolean) -> Unit,
    onBoarding: List<HelpExample>,
    pagerState: PagerState,
    onNextPage: () -> Job
) {

    ElevatedButton(onClick = { onSetHelpValue(false) }) {
        Text(
            text = stringResource(R.string.home_text_skip),
            color = MaterialTheme.colorScheme.secondary,
        )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        repeat(onBoarding.size) {
            val color =
                if (pagerState.currentPage == it) MaterialTheme.colorScheme.primary else Color.Gray
            Icon(
                imageVector = Icons.Default.Circle,
                contentDescription = Icons.Default.Circle.toString(),
                tint = color,
                modifier = Modifier.size(8.dp)
            )
        }
    }
    ElevatedButton(
        onClick = { onNextPage() }
    ) {
        Text(text = stringResource(R.string.home_text_next))
    }

}

@Composable
fun GifImage(@DrawableRes idImage: Int) {

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context).components {
        if (SDK_INT >= 28) {
            add(ImageDecoderDecoder.Factory())
        } else {
            add(GifDecoder.Factory())
        }
    }.build()
    Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(data = idImage)
                .build(),
            imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = Modifier
            .size(480.dp)
            .padding(top = 12.dp, bottom = 12.dp, start = 25.dp, end = 25.dp)
            .border(
                border = BorderStroke(width = 0.7.dp, color = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(5.dp)
            ),
        contentScale = ContentScale.Crop
    )
}
