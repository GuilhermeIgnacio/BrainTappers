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
    val difficultyValue: String? = null,

    val isTypeMenuOpen: Boolean = false,
    val typeValue: String? = null
)

sealed interface TriviaSettingsEvents {
    data object OpenNumberOfQuestionsDropdownMenu : TriviaSettingsEvents
    data class OnNumberOfQuestionsSelected(val value: String) : TriviaSettingsEvents

    data object OpenDifficultyMenu : TriviaSettingsEvents
    data class OnDifficultySelected(val value: String) : TriviaSettingsEvents

    data object OpenTypeMenu : TriviaSettingsEvents
    data class OnTypeSelected(val value: String) : TriviaSettingsEvents

    data object DismissDropdownMenu : TriviaSettingsEvents
}


class TriviaSettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(TriviaSettingsState())
    val state = _state.asStateFlow()

    val numberOfQuestions = (1..10).map { number ->
        DropdownItem(
            apiParameter = number.toString(),
            text = UiText.StringResource(
                resId = if (number == 1) R.string.question else R.string.questions,
                number.toString()
            ),
            onClick = { onEvent(TriviaSettingsEvents.OnNumberOfQuestionsSelected(number.toString())) }
        )
    }

    val difficulty = listOf(
        DropdownItem(
            apiParameter = "", //Empty
            text = UiText.StringResource(resId = R.string.any_difficulty),
            onClick = { onEvent(TriviaSettingsEvents.OnDifficultySelected("Any Difficulty")) }
        ),
        DropdownItem(
            apiParameter = "easy",
            text = UiText.StringResource(resId = R.string.easy),
            onClick = { onEvent(TriviaSettingsEvents.OnDifficultySelected("Easy")) }
        ),
        DropdownItem(
            apiParameter = "medium",
            text = UiText.StringResource(resId = R.string.medium),
            onClick = { onEvent(TriviaSettingsEvents.OnDifficultySelected("Medium")) }
        ),
        DropdownItem(
            apiParameter = "hard",
            text = UiText.StringResource(resId = R.string.hard),
            onClick = { onEvent(TriviaSettingsEvents.OnDifficultySelected("Hard")) }
        ),
    )

    val type = listOf(
        DropdownItem(
            apiParameter = "",
            text = UiText.StringResource(resId = R.string.any_type),
            onClick = { onEvent(TriviaSettingsEvents.OnTypeSelected("Any Type")) }
        ),
        DropdownItem(
            apiParameter = "multiple",
            text = UiText.StringResource(resId = R.string.multiple_choice),
            onClick = { onEvent(TriviaSettingsEvents.OnTypeSelected("Multiple Choice")) }
        ),
        DropdownItem(
            apiParameter = "boolean",
            text = UiText.StringResource(resId = R.string.true_false),
            onClick = { onEvent(TriviaSettingsEvents.OnTypeSelected("True/False")) }
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
                _state.update {
                    it.copy(
                        isNumberOfQuestionsMenuOpen = false,
                        isDifficultyMenuOpen = false
                    )
                }
            }

            TriviaSettingsEvents.OpenTypeMenu -> {
                _state.update {
                    it.copy(
                        isTypeMenuOpen = true
                    )
                }
            }

            is TriviaSettingsEvents.OnTypeSelected -> {
                _state.update {
                    it.copy(
                        isTypeMenuOpen = false,
                        typeValue = event.value
                    )
                }
            }
        }
    }

}