package com.adgh.rabiesreminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.adgh.rabiesreminder.ui.components.MgText

@Composable
fun CreditsPage(
    basicFontSize: TextUnit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(basicFontSize.value.dp),
        verticalArrangement = Arrangement.spacedBy(basicFontSize.value.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MgText(
            text = "CREDITS",
            basicFontSize = basicFontSize,
            isBold = true,
            times = 1.21f
        )
        MgText(
            text = """
                THIS IS AN OPEN SOURCE PROJECT CREATED BY Dr ABHISHEK DAS G H.
            """.trimIndent(),
            basicFontSize= basicFontSize
        )
    }
}