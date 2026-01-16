package android.app.faunadex.presentation.components

import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.White
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * IUCN Conservation Status Categories with official colors
 * Based on IUCN Red List standards
 */
data class ConservationStatus(
    val code: String,
    val fullName: String,
    val color: Color
)

object IUCNStatus {
    // Extinct
    val EX = ConservationStatus("EX", "Extinct", Color(0xFF000000))
    val EW = ConservationStatus("EW", "Extinct in the Wild", Color(0xFF3D1951))

    // Threatened
    val CR = ConservationStatus("CR", "Critically Endangered", Color(0xFFD81E05))
    val EN = ConservationStatus("EN", "Endangered", Color(0xFFFC7F3F))
    val VU = ConservationStatus("VU", "Vulnerable", Color(0xFFFFC107))

    // Lower Risk
    val NT = ConservationStatus("NT", "Near Threatened", Color(0xFFCCE226))
    val LC = ConservationStatus("LC", "Least Concern", Color(0xFF60C659))

    // Data Deficient / Not Evaluated
    val DD = ConservationStatus("DD", "Data Deficient", Color(0xFFD1D1C6))
    val NE = ConservationStatus("NE", "Not Evaluated", Color(0xFFDDDDDD))

    /**
     * Get conservation status from code or full name
     */
    fun fromString(status: String): ConservationStatus {
        return when (status.uppercase().trim()) {
            "EX", "EXTINCT" -> EX
            "EW", "EXTINCT IN THE WILD" -> EW
            "CR", "CRITICALLY ENDANGERED" -> CR
            "EN", "ENDANGERED" -> EN
            "VU", "VULNERABLE" -> VU
            "NT", "NEAR THREATENED" -> NT
            "LC", "LEAST CONCERN" -> LC
            "DD", "DATA DEFICIENT" -> DD
            "NE", "NOT EVALUATED" -> NE
            else -> LC
        }
    }

    fun getAllStatuses(): List<ConservationStatus> {
        return listOf(EX, EW, CR, EN, VU, NT, LC, DD, NE)
    }
}

@Composable
fun ConservationStatusBadge(
    status: String,
    modifier: Modifier = Modifier,
    showFullName: Boolean = false
) {
    val conservationStatus = IUCNStatus.fromString(status)

    Box(
        modifier = modifier
            .background(
                color = conservationStatus.color,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = if (showFullName) conservationStatus.fullName.uppercase() else conservationStatus.code,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (conservationStatus == IUCNStatus.DD || conservationStatus == IUCNStatus.NE) {
                Color.Black
            } else {
                White
            },
            fontFamily = JerseyFont
        )
    }
}

@Preview(showBackground = true, name = "All IUCN Statuses")
@Composable
fun ConservationStatusBadgePreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("IUCN Conservation Status Badges", fontWeight = FontWeight.Bold)

        IUCNStatus.getAllStatuses().forEach { status ->
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                ConservationStatusBadge(status = status.code)
                ConservationStatusBadge(status = status.code, showFullName = true)
            }
        }
    }
}

@Preview(showBackground = true, name = "Individual Examples")
@Composable
fun ConservationStatusBadgeIndividualPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ConservationStatusBadge(status = "CR")
        ConservationStatusBadge(status = "Endangered")
        ConservationStatusBadge(status = "VU", showFullName = true)
        ConservationStatusBadge(status = "Least Concern", showFullName = true)
    }
}
