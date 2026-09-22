package com.adgh.rabiesreminder

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.adgh.rabiesreminder.ui.components.EmailText
import com.adgh.rabiesreminder.ui.components.ExpandableSection
import com.adgh.rabiesreminder.ui.components.MgButton
import com.adgh.rabiesreminder.ui.components.MgText

@Composable
fun PrivacyPolicyScreen(
    base: TextUnit,
    context: Context,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            MgText(
                text = "PRIVACY POLICY",
                basicFontSize = base,
                times = 1.6f,
                isBold = true
            )

            Spacer(Modifier.height(8.dp))

            MgText(
                text = "Last updated April 1, 2026",
                basicFontSize = base,
                times = 0.9f
            )

            Spacer(Modifier.height(12.dp))

            MgText(
                text = "Rabies Reminder works entirely on your device and does not use internet connectivity.",
                basicFontSize = base
            )
        }

        item {
            ExpandableSection("SUMMARY OF KEY POINTS", base) {
                MgText(
                    text = "No personal information is collected.",
                    basicFontSize = base
                )
                MgText(
                    text = "All data stays on your device.",
                    basicFontSize = base
                )
                MgText(
                    text = "No internet connection is used.",
                    basicFontSize = base
                )
                MgText(
                    text = "No third-party services are used.",
                    basicFontSize = base
                )
            }
        }

        item {
            ExpandableSection(
                "1. INFORMATION YOU PROVIDE",
                base
            ) {
                MgText(
                    text = "You may enter reminder-related inputs.",
                    basicFontSize = base
                )
                MgText(
                    text = "All inputs remain on your device.",
                    basicFontSize = base
                )
            }
        }

        item {
            ExpandableSection("2. HOW DATA IS USED", base) {
                MgText(
                    text = "All processing happens locally.",
                    basicFontSize = base
                )
                MgText(
                    text = "No data is sent externally.",
                    basicFontSize = base
                )
            }
        }

        item {
            ExpandableSection("3. NOTIFICATIONS", base) {
                MgText(
                    text = "App may send reminder notifications.",
                    basicFontSize = base
                )
                MgText(
                    text = "You can disable them in settings.",
                    basicFontSize = base
                )
            }
        }

        item {
            ExpandableSection("4. DATA SHARING", base) {
                MgText(
                    text = "We do not share any data.",
                    basicFontSize = base
                )
            }
        }

        item {
            ExpandableSection("5. STORAGE & SECURITY", base) {
                MgText(
                    text = "Data is stored on your device.",
                    basicFontSize = base
                )
                MgText(
                    text = "No system is completely secure. Users are responsible for maintaining the security of their device.",
                    basicFontSize = base
                )
            }
        }

        item {
            ExpandableSection("6. YOUR CONTROL", base) {
                MgText(
                    text = "You can stop using the app anytime.",
                    basicFontSize = base
                )
                MgText(
                    text = "You can clear app data anytime.",
                    basicFontSize = base
                )
            }
        }

        item {
            ExpandableSection("7. CHILDREN", base) {
                MgText(
                    text = "Not intended for children under 13.",
                    basicFontSize = base
                )
            }
        }

        item {
            ExpandableSection("8. UPDATES", base) {
                MgText(
                    text = "Policy may be updated periodically.",
                    basicFontSize = base
                )
            }
        }

        item {
            ExpandableSection("9. CONTACT", base) {
                EmailText(
                    "rajeev.a@aiimsmangalagiri.edu.in",
                    base
                )
                MgText(
                    text = "Dr. Rajeev A",
                    basicFontSize = base
                )
                MgText(
                    text = "Department of Community and Family Medicine",
                    basicFontSize = base
                )
                MgText(
                    text = "AIIMS Mangalagiri",
                    basicFontSize = base
                )
                MgText(
                    text = "Andhra Pradesh, India",
                    basicFontSize = base
                )
            }
        }
        item {
            MgButton(
                text = "View online",
                basicFontSize = base,
                onClick = {
                    val intent =
                        Intent(Intent.ACTION_DEFAULT).apply {
                            data =
                                "https://abhshkdsgh.github.io/rabies-reminder/privacy-policy".toUri()
                        }
                    context.startActivity(intent)
                }
            )

            MgButton(
                text = "Done",
                basicFontSize = base, onClick = {
                    navController.popBackStack()
                }
            )
        }
        item { Spacer(Modifier.height(64.dp)) }
    }
}