package android.app.faunadex.presentation.components

import android.app.faunadex.R
import android.app.faunadex.ui.theme.BlackAlpha50
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.GreenGradient
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.White
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun FaunaCard(
    modifier: Modifier = Modifier,
    faunaName: String = "Sample Fauna",
    latinName: String = "Fauna scientificus",
    imageUrl: String? = null,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onCardClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        onClick = onCardClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AsyncImage(
                    model = imageUrl ?: R.drawable.animal_dummy,
                    contentDescription = faunaName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.animal_dummy),
                    error = painterResource(R.drawable.animal_dummy)
                )

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(46.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = BlackAlpha50,
                        modifier = Modifier
                            .size(46.dp)
                            .border(
                                width = 2.dp,
                                color = White,
                                shape = RoundedCornerShape(50)
                            )
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) Color.Red else White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = GreenGradient)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = faunaName,
                        fontSize = when {
                            faunaName.length <= 12 -> 24.sp
                            faunaName.length <= 16 -> 20.sp
                            faunaName.length <= 20 -> 18.sp
                            else -> 16.sp
                        },
                        fontWeight = FontWeight.Bold,
                        fontFamily = JerseyFont,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = PastelYellow
                    )

                    Text(
                        text = latinName,
                        fontSize = when {
                            latinName.length <= 15 -> 20.sp
                            latinName.length <= 20 -> 18.sp
                            latinName.length <= 25 -> 16.sp
                            else -> 14.sp
                        },
                        fontStyle = FontStyle.Italic,
                        fontFamily = JerseyFont,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MediumGreenSage
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FaunaCardPreview() {
    FaunaDexTheme {
        var isFavorite by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FaunaCard(
                faunaName = "Sumatran Tiger",
                latinName = "Panthera tigris sumatrae",
                isFavorite = isFavorite,
                onFavoriteClick = { isFavorite = !isFavorite }
            )

            FaunaCard(
                faunaName = "Komodo Dragon",
                latinName = "Varanus komodoensis",
                isFavorite = false,
                onFavoriteClick = {}
            )
        }
    }
}

