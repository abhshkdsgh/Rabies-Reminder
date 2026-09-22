package com.adgh.rabiesreminder.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adgh.rabiesreminder.MgRecord
import com.adgh.rabiesreminder.PEPSchedule
import com.adgh.rabiesreminder.R
import com.adgh.rabiesreminder.toOrdinal
import com.adgh.rabiesreminder.ui.theme.AppColors
import com.adgh.rabiesreminder.ui.theme.AppColors.Destructive
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Custom scalable text component supporting font size scaling multiplier, bold weighting, and centering.
 *
 * @param modifier Layout modifier.
 * @param text Content string to display.
 * @param basicFontSize Base scalable typography font size.
 * @param times Multiplier applied to [basicFontSize].
 * @param isBold `true` for heavy/bold weight, `false` for normal weight.
 * @param color Text color tint.
 */
@Composable
fun MgText(
    modifier: Modifier = Modifier,
    text: String,
    basicFontSize: TextUnit,
    times: Float = 1f,
    isBold: Boolean = false,
    color: Color = Color.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        fontWeight = if (isBold) FontWeight.Black else FontWeight.Normal,
        fontSize = (basicFontSize.value * times).sp,
        lineHeight = ((basicFontSize.value * times) + 8).sp,
        textAlign = TextAlign.Center,
        color = color
    )
}

/**
 * Reusable outlined text field component styled for the app design language.
 *
 * @param value Text input value.
 * @param onValueChange Callback invoked when text input changes.
 * @param label Text field label string.
 * @param basicFontSize0 Base typography font size.
 * @param modifier Layout modifier.
 * @param visualTransformation Visual transformation (e.g. password masking).
 * @param keyboardOptions Keyboard options configuration.
 * @param trailingIcon Optional trailing icon composable.
 * @param readOnly `true` if field is read-only (e.g. dropdown triggers).
 */
@Composable
fun MgTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    basicFontSize0: TextUnit,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = { onValueChange(it) },
        label = {
            MgText(
                text = label,
                basicFontSize = basicFontSize0
            )
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        readOnly = readOnly
    )
}

/**
 * Custom outlined button component taking a simple string label.
 *
 * @param onClick Click handler callback.
 * @param modifier Layout modifier.
 * @param enabled Enabled state boolean.
 * @param basicFontSize Scalable base font size.
 * @param isPaddingForced `true` to enforce standard container padding.
 * @param text String label displayed on the button.
 */
@Composable
fun MgButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    basicFontSize: TextUnit,
    isPaddingForced: Boolean = true,
    text: String
) {
    Box(
        modifier = if (isPaddingForced) Modifier.padding((basicFontSize.value / 2).dp) else Modifier
    ) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
        ) {
            MgText(
                text = text,
                basicFontSize = basicFontSize
            )
        }
    }
}

/**
 * Custom outlined button component taking a composable slot for content.
 *
 * @param onClick Click handler callback.
 * @param modifier Layout modifier.
 * @param enabled Enabled state boolean.
 * @param basicFontSize Scalable base font size.
 * @param isPaddingForced `true` to enforce vertical container padding.
 * @param text Composable content slot rendered inside the button.
 */
@Composable
fun MgButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    basicFontSize: TextUnit,
    isPaddingForced: Boolean = true,
    text: @Composable () -> Unit
) {
    Box(
        modifier = if (isPaddingForced) Modifier.padding(vertical = (basicFontSize.value / 2).dp) else Modifier
    ) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
        ) {
            text()
        }
    }
}

/**
 * Reusable alert dialog supporting title, message, confirmation, and optional rejection buttons.
 *
 * @param showDialog `true` if the dialog is visible.
 * @param onDismissRequest Callback invoked when clicking outside or pressing back.
 * @param onConfirmation Callback invoked when tapping the positive action button.
 * @param onReject Callback invoked when tapping the dismiss button.
 * @param title Dialog title string.
 * @param message Dialog body message text.
 * @param confirmString Confirmation button label string.
 * @param dismissString Optional rejection button label string.
 * @param basicFontSize Scalable typography font size.
 */
@Composable
fun MgAlertDialogue(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onReject: () -> Unit = onDismissRequest,
    title: String,
    message: String,
    confirmString: String,
    dismissString: String = "",
    basicFontSize: TextUnit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                MgText(text = title, basicFontSize = basicFontSize, isBold = true)
            },
            text = {
                MgText(
                    text = message, basicFontSize = basicFontSize
                )
            },
            confirmButton = {
                TextButton(onClick = { onConfirmation() }) {
                    MgText(text = confirmString, basicFontSize = basicFontSize)
                }
            },
            dismissButton = {
                Column {
                    if (dismissString != "") {
                        TextButton(onClick = onReject) {
                            MgText(text = dismissString, basicFontSize = basicFontSize)
                        }
                    }
                }
            }
        )
    }
}

/**
 * Welcome information banner card displayed at the top of the reminders overview screen.
 */
@Composable
fun InfoCard() {
    Surface {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.welcome_to_rabies_reminder),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        stringResource(R.string.create_and_manage_post_exposure_schedules_quickly_and_reliably),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.width(12.dp))
                Image(
                    painter = painterResource(R.mipmap.ic_launcher_foreground),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}

/**
 * Educational information card card displaying an icon, title, and detailed explanation text.
 *
 * @param title Section title string.
 * @param icon Emoji or text icon symbol.
 * @param content Informational text body.
 * @param basicFontSize Scalable typography font size.
 */
@Composable
fun InfoCard(title: String, icon: String, content: String, basicFontSize: TextUnit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(basicFontSize.value.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(basicFontSize.value.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MgText(text = "$icon $title", basicFontSize = basicFontSize)
            MgText(text = content, basicFontSize = basicFontSize)
        }
    }
}

/**
 * Composable placeholder shown when no vaccination reminders exist in the database or active filter.
 *
 * @param basicFontSize Scalable typography font size.
 * @param onCreate Callback to navigate to schedule creation screen.
 * @param showAllEntries Callback to switch filter to show all entries.
 * @param isAllEntriesSelected `true` if "All" filter is currently active.
 */
@Composable
fun EmptyState(
    basicFontSize: TextUnit,
    onCreate: () -> Unit,
    showAllEntries: () -> Unit,
    isAllEntriesSelected: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(basicFontSize.value.dp)
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.no_reminders_yet), style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.create_a_schedule_to_start_receiving_reminders),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = onCreate) { Text(stringResource(R.string.create_schedule)) }
            if (!isAllEntriesSelected) {
                Spacer(Modifier.height((basicFontSize.value * 0.75f).dp))
                MgButton(
                    text = stringResource(R.string.all_entries),
                    onClick = showAllEntries,
                    basicFontSize = basicFontSize
                )
            }
        }
    }
}

/**
 * Row component providing checkbox controls for configuring same-day and pre-reminder (2 days before) alarms.
 *
 * @param basicFontSize Scalable typography font size.
 * @param xOn Injection day alarm toggle state.
 * @param pXOn Pre-reminder alarm toggle state.
 * @param xOnChanged Callback when injection day alarm state changes.
 * @param pXOnChanged Callback when pre-reminder alarm state changes.
 * @param isTodayOrFuture `true` if the vaccination date is today or in the future.
 */
@Composable
fun AlarmEntry(
    basicFontSize: TextUnit,
    xOn: Boolean,
    pXOn: Boolean,
    xOnChanged: (Boolean) -> Unit,
    pXOnChanged: (Boolean) -> Unit,
    isTodayOrFuture: Boolean = true
) {
    Row(Modifier.fillMaxWidth()) {
        Spacer(Modifier.fillMaxWidth(0.25f))
        Column(
            modifier = Modifier.fillMaxWidth(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MgText(
                    text = stringResource(if (isTodayOrFuture) R.string.on_injection_day_at_7_am else R.string.on_the_previous_day_at_9_am),
                    basicFontSize = basicFontSize,
                    modifier = Modifier.fillMaxWidth(0.81f)
                )
                Checkbox(
                    checked = xOn,
                    onCheckedChange = {
                        xOnChanged(it)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MgText(
                    text = stringResource(R.string.two_days_before_at_9_am),
                    basicFontSize = basicFontSize,
                    modifier = Modifier.fillMaxWidth(0.81f)
                )
                Checkbox(
                    checked = pXOn,
                    onCheckedChange = {
                        pXOnChanged(it)
                    },
                    enabled = xOn,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Collapsible UI section component that expands or hides detailed content on click.
 *
 * @param title Section header title text.
 * @param base Base typography font size.
 * @param content Composable slot containing inner expandable content.
 */
@Composable
fun ExpandableSection(
    title: String,
    base: TextUnit,
    content: @Composable () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {

        MgText(
            text = if (expanded) "▼ $title" else "▶ $title",
            basicFontSize = base,
            times = 1.08f,
            isBold = true
        )

        AnimatedVisibility(expanded) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 6.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * Card component displaying a single vaccination reminder record with active toggle switch and delete button.
 *
 * @param reminder The [MgRecord] instance to display.
 * @param onToggle Callback when active status toggle state changes.
 * @param onDelete Callback when delete action is triggered.
 * @param basicFontSize Scalable typography font size.
 */
@Composable
fun ReminderCard(reminder: MgRecord, onToggle: (Boolean) -> Unit, onDelete: () -> Unit, basicFontSize: TextUnit) {
    Surface{
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = (if (reminder.isPreviousDay)
                    MaterialTheme.colorScheme.onTertiary
                else
                    MaterialTheme.colorScheme.tertiary)
            )
        ) {
            Row(
                modifier = Modifier
                    .background(
                        if (
                            reminder.isPreviousDay
                        )
                            Color.Gray
                        else MaterialTheme.colorScheme.tertiary
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (reminder.isActive) AppColors.Success else Color.Gray)
                )
                Spacer(Modifier.width(12.dp))

                // Optimized recognition logic using enum
                val isIntraMuscular = reminder.schedule0 == PEPSchedule.IntraMuscular

                Column(modifier = Modifier.weight(1f)) {
                    MgText(
                        text = stringResource(
                            R.string.reminder_sentence,
                            if (isIntraMuscular)
                                stringResource(R.string.anti_rabies_post_exposure_prophylaxis_intra_muscular)
                            else
                                stringResource(R.string.anti_rabies_post_exposure_prophylaxis_intra_dermal),
                            reminder.doseNumber.toOrdinal(LocalContext.current),
                            if (reminder.isPreviousDay)
                                stringResource(R.string.in_2_days)
                            else
                                stringResource(R.string.today)
                        ),
                        basicFontSize = basicFontSize,
                        color = if(reminder.isPreviousDay) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary,
                        isBold = true
                    )
                    Spacer(Modifier.height(4.dp))
                    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
                    MgText(
                        text = reminder.long0.let {
                            dateFormatter.format(Date(it))
                        }, basicFontSize = basicFontSize,
                        color = if(reminder.isPreviousDay) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiary,
                        isBold = true
                    )
                }
                Row(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.scrim,
                        CircleShape
                    )
                ){
                    Checkbox(checked = reminder.isActive, onCheckedChange = { onToggle(it) })
                    IconButton(
                        onClick = onDelete,
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = Destructive
                        )
                    }
                }
            }
        }
    }
}

/**
 * Calculates a timestamp in milliseconds since epoch representing today's date at 7:00 AM (07:00:00.000).
 *
 * @return Epoch time in milliseconds for today at 7 AM.
 */
fun today7amLong(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 7)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

