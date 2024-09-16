package com.guilherme.braintappers.presentation.screen.triviasettings

import androidx.lifecycle.ViewModel
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.model.DropdownItem
import com.guilherme.braintappers.presentation.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TriviaSettingsState(
    val isNumberOfQuestionsMenuOpen: Boolean = false,
    val numberOfQuestionsValue: String? = null,

    val isDifficultyMenuOpen: Boolean = false,
    val difficultyValue: String? = null
)

sealed interface TriviaSettingsEvents {
    data object OpenNumberOfQuestionsDropdownMenu : TriviaSettingsEvents
    data class OnNumberOfQuestionsSelected(val value: String) : TriviaSettingsEvents

    data object OpenDifficultyMenu : TriviaSettingsEvents
    data class OnDifficultySelected(val value: String) : TriviaSettingsEvents

    data object DismissDropdownMenu: TriviaSettingsEvents
}


class TriviaSettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(TriviaSettingsState())
    val state = _state.asStateFlow()

    val numberOfQuestions = listOf(
        DropdownItem(
            text = UiText.StringResource(resId = R.string.question, "1"),
            onClick = { onEvent(TriviaSettingsEvents.OnNumberOfQuestionsSelected("1")) }
        ),
        DropdownItem(
            text = UiText.StringResource(resId = R.string.questions, "2"),
            onClick = { onEvent(TriviaSettingsEvents.OnNumberOfQuestionsSelected("2")) }
        ),
        DropdownItem(
            text = UiText.StringResource(resId = R.string.questions, "3"),
            onClick = { onEvent(TriviaSettingsEvents.OnNumberOfQuestionsSelected("3")) }
        ),
    )

    val difficulty = listOf(
        DropdownItem(
            text = UiText.StringResource(resId = R.string.any_difficulty),
            onClick = { onEvent(TriviaSettingsEvents.OnDifficultySelected("Any Difficulty")) }
        ),
        DropdownItem(
            text = UiText.StringResource(resId = R.string.easy),
            onClick = { onEvent(TriviaSettingsEvents.OnDifficultySelected("Easy")) }
        ),
        DropdownItem(
            text = UiText.StringResource(resId = R.string.medium),
            onClick = { onEvent(TriviaSettingsEvents.OnDifficultySelected("Medium")) }
        ),
        DropdownItem(
            text = UiText.StringResource(resId = R.string.hard),
            onClick = { onEvent(TriviaSettingsEvents.OnDifficultySelected("Hard")) }
        ),
    )

    fun onEvent(event: TriviaSettingsEvents) {
        when (event) {
            TriviaSettingsEvents.OpenNumberOfQuestionsDropdownMenu -> {
                _state.update {
                    it.copy(
                        isNumberOfQuestionsMenuOpen = true
                    )
                }
            }

            is TriviaSettingsEvents.OnNumberOfQuestionsSelected -> {
                _state.update {
                    it.copy(
                        numberOfQuestionsValue = event.value,
                        isNumberOfQuestionsMenuOpen = false
                    )
                }
            }

            TriviaSettingsEvents.OpenDifficultyMenu -> {
                _state.update {
                    it.copy(
                        isDifficultyMenuOpen = true
                    )
                }
            }

            is TriviaSettingsEvents.OnDifficultySelected -> {
                _state.update {
                    it.copy(
                        difficultyValue = event.value,
                        isDifficultyMenuOpen = false
                    )
                }
            }

            TriviaSettingsEvents.DismissDropdownMenu -> {
                _state.update { it.copy(
                    isNumberOfQuestionsMenuOpen = false,
                    isDifficultyMenuOpen = false
                ) }
            }
        }
    }

}