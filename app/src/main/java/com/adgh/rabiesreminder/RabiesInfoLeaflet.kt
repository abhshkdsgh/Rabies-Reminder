package com.adgh.rabiesreminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.adgh.rabiesreminder.ui.components.InfoCard
import com.adgh.rabiesreminder.ui.components.MgText

/**
 * Composable screen rendering the Rabies Awareness and Educational Leaflet.
 *
 * Displays medical facts on rabies viral infection, vectors, exposure categories (Cat I, II, III),
 * immediate first-aid wound care steps, post-exposure prophylaxis (PEP) treatment schedules, and prevention guidelines.
 *
 * @param basicFontSize Scalable typography font size.
 */
@Composable
fun RabiesInfoLeaflet(
    basicFontSize: TextUnit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(basicFontSize.value.dp),
        verticalArrangement = Arrangement.spacedBy(basicFontSize.value.dp)
    ) {
        MgText(text=stringResource(R.string.rabies_awareness), basicFontSize = basicFontSize)

        InfoCard( basicFontSize = basicFontSize, title = stringResource(R.string.what_is_rabies), icon = "🧠",
            content = stringResource(R.string.rabies_is_a_deadly_viral_disease_that_attacks_the_brain_and_nerves) +
                    " " +
                    stringResource(R.string.once_symptoms_appear_it_is_almost_always_fatal_but_timely_treatment_can_prevent_it)
        )

        InfoCard(basicFontSize = basicFontSize, title = stringResource(R.string.cause), icon = "🦠",
            content = stringResource(R.string.caused_by_the_rabies_virus_found_in_the_saliva_of_infected_animals) +
                    " " +
                    stringResource(R.string.in_india_dogs_are_the_most_common_source_but_cats_monkeys_and_wild_animals_can_also_spread_rabies)
        )

        InfoCard(basicFontSize = basicFontSize, title = stringResource(R.string.how_people_get_infected), icon = "⚠️",
            content = stringResource(R.string.bite_or_scratch_from_a_rabid_animal) +
                    stringResource(R.string.saliva_or_lick_on_broken_skin_or_eyes_mouth_or_nose) +
                    stringResource(R.string.handling_infected_animals_without_protection)
        )

        InfoCard(basicFontSize = basicFontSize, title = stringResource(R.string.signs_of_rabid_animals), icon = "🐕",
            content = stringResource(R.string.unusual_aggression_or_excessive_drooling) +
                    stringResource(R.string.difficulty_walking_biting_objects_or_sudden_behavior_changes) +
                    stringResource(R.string.even_healthy_looking_animals_can_carry_rabies_always_treat_bites_seriously)
        )

        InfoCard(basicFontSize = basicFontSize, title = stringResource(R.string.categories_of_exposure), icon = "📊",
            content = stringResource(R.string.category_i_touching_or_feeding_animals_licks_on_intact_skin_no_treatment_needed) +
                    stringResource(R.string.category_ii_minor_scratches_nibbles_without_bleeding_rabies_vaccine_required) +
                    stringResource(R.string.category_iii_deep_bites_scratches_saliva_on_broken_skin_bites_on_face_neck_hands_rabies_vaccine_rabies_immunoglobulin_required)
        )

        InfoCard(basicFontSize = basicFontSize, title = stringResource(R.string.first_aid_act_immediately), icon = "⏱️",
            content = stringResource(R.string._1_wash_the_wound_with_plenty_of_soap_and_running_water_for_at_least_15_minutes) +
                    stringResource(R.string._2_apply_antiseptic_povidone_iodine_or_alcohol_based) +
                    stringResource(R.string._3_do_not_apply_irritants_chili_turmeric_oils_or_cover_tightly) +
                    stringResource(R.string._4_visit_the_nearest_hospital_or_anti_rabies_clinic_immediately)
        )

        InfoCard(basicFontSize = basicFontSize, title = stringResource(R.string.treatment_schedule), icon = "💉",
            content = stringResource(R.string.rabies_vaccination_pep_day_0_3_7_28_day_0_first_injection_intradermal_or_day_0_3_7_14_28_day_0_first_injection_intramuscular) +
                    stringResource(R.string.rabies_immunoglobulin_rig_for_severe_category_iii_bites_injected_into_and_around_the_wound_as_soon_as_possible) +
                    stringResource(R.string.tetanus_injection_may_also_be_required) +
                    stringResource(R.string.complete_all_doses_even_if_the_wound_heals_or_the_animal_appears_healthy)
        )

        InfoCard(basicFontSize = basicFontSize, title = stringResource(R.string.prevention), icon = "🛡️",
            content = stringResource(R.string.vaccinate_pets_regularly) +
                    stringResource(R.string.avoid_contact_with_unknown_animals) +
                    stringResource(R.string.report_stray_dogs_to_local_authorities) +
                    stringResource(R.string.educate_children_about_animal_bite_safety)
        )

        InfoCard(basicFontSize = basicFontSize, title = stringResource(R.string.remember), icon = "📌",
            content = stringResource(R.string.rabies_is_100_preventable_if_treated_early) +
                    stringResource(R.string.wash_the_wound_immediately) +
                    stringResource(R.string.do_not_wait_for_symptoms_rabies_is_almost_always_fatal_after_onset) +
                    stringResource(R.string.keep_contact_numbers_of_anti_rabies_vaccine_clinics_handy)
        )
    }
}
