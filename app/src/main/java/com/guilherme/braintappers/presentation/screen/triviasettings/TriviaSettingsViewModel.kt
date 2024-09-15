package com.guilherme.braintappers.presentation.screen.triviasettings

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import com.guilherme.braintappers.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DropdownItem(
    val text: UiText.StringResource,
    val onClick: () -> Unit
)

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

sealed class UiText {
    data class DynamicString(val value: String) : UiText()

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(id = resId, formatArgs = args)
        }
    }

}