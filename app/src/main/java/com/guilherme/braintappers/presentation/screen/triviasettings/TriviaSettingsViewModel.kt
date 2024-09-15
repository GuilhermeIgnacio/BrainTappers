package com.guilherme.braintappers.presentation.screen.triviasettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DropdownItem(
    val text: String,
    val onClick: () -> Unit
)

data class TriviaSettingsState(
    val isNumberOfQuestionsMenuOpen: Boolean = false,
    val numberOfQuestionsValue: String? = null
)

sealed interface TriviaSettingsEvents {
    data object OpenNumberOfQuestionsDropdownMenu : TriviaSettingsEvents
    data class OnNumberOfQuestionsSelected(val value: String) : TriviaSettingsEvents
}


class TriviaSettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(TriviaSettingsState())
    val state = _state.asStateFlow()

    val numberOfQuestions = listOf(
        DropdownItem(
            text = "1",
            onClick = { onEvent(TriviaSettingsEvents.OnNumberOfQuestionsSelected("1")) })
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
        }
    }

}