package android.app.faunadex.presentation.components

import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import android.app.faunadex.ui.theme.PrimaryGreenLight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FilterOption(
    val id: String,
    val label: String,
    val isSelected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    title: String,
    description: String,
    filterOptions: List<FilterOption>,
    onFilterToggle: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkForest,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .background(
                            color = PrimaryGreen,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    ) {
        FilterBottomSheetContent(
            title = title,
            description = description,
            filterOptions = filterOptions,
            onFilterToggle = onFilterToggle,
            onSave = onSave
        )
    }
}

@Composable
private fun FilterBottomSheetContent(
    title: String,
    description: String,
    filterOptions: List<FilterOption>,
    onFilterToggle: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            text = title,
            fontFamily = JerseyFont,
            fontWeight = FontWeight.Bold,
            color = PastelYellow,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            fontFamily = JerseyFont,
            color = MediumGreenSage,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        val scrollState = rememberScrollState()
        val showScrollbar by remember {
            derivedStateOf {
                scrollState.maxValue > 0
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = if (showScrollbar) 8.dp else 0.dp)
                    .verticalScroll(scrollState)
            ) {
                filterOptions.forEach { option ->
                    FilterCheckboxItem(
                        label = option.label,
                        isChecked = option.isSelected,
                        onCheckedChange = { onFilterToggle(option.id) }
                    )
                }
            }

            if (showScrollbar) {
                val scrollbarHeight = 300.dp
                val contentHeightDp = (filterOptions.size * 40).dp
                val thumbHeight = remember(scrollbarHeight, contentHeightDp) {
                    val ratio = scrollbarHeight.value / contentHeightDp.value
                    (scrollbarHeight.value * ratio).coerceAtLeast(40f).dp
                }

                val scrollPercentage by remember {
                    derivedStateOf {
                        if (scrollState.maxValue > 0) {
                            scrollState.value.toFloat() / scrollState.maxValue.toFloat()
                        } else {
                            0f
                        }
                    }
                }

                val thumbOffset = remember(scrollPercentage, scrollbarHeight, thumbHeight) {
                    ((scrollbarHeight.value - thumbHeight.value) * scrollPercentage).dp
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .width(4.dp)
                        .height(scrollbarHeight)
                        .padding(vertical = 4.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .offset(y = thumbOffset)
                            .width(4.dp)
                            .height(thumbHeight)
                            .background(
                                color = PrimaryGreenLight.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onSave,
                modifier = Modifier
                    .width(120.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Save",
                    fontFamily = JerseyFont,
                    fontSize = 24.sp,
                    color = PastelYellow
                )
            }
        }
    }
}

@Composable
private fun FilterCheckboxItem(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = PrimaryGreenLight,
                uncheckedColor = PrimaryGreenLight,
                checkmarkColor = DarkForest
            )
        )

        Text(
            text = label,
            fontFamily = JerseyFont,
            color = MediumGreenSage,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun FilterBottomSheetPreview() {
    val sampleFilterOptions = listOf(
        FilterOption("mammal", "Mammals", true),
        FilterOption("bird", "Birds", false),
        FilterOption("reptile", "Reptiles", true),
        FilterOption("amphibian", "Amphibians", false),
        FilterOption("fish", "Fish", false),
        FilterOption("endangered", "Endangered Species", true),
        FilterOption("endemic", "Endemic to Indonesia", false),
        FilterOption("nocturnal", "Nocturnal Animals", false),
        FilterOption("aquatic", "Aquatic Animals", true),
        FilterOption("terrestrial", "Terrestrial Animals", false),
        FilterOption("carnivore", "Carnivores", false),
        FilterOption("herbivore", "Herbivores", true)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkForest)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .width(32.dp)
                    .height(4.dp)
                    .background(
                        color = PrimaryGreen,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }

        FilterBottomSheetContent(
            title = "Filter Fauna",
            description = "Select the filter categories you want to select to see on your home screen. You can update this anytime.",
            filterOptions = sampleFilterOptions,
            onFilterToggle = {},
            onSave = {}
        )
    }
}
