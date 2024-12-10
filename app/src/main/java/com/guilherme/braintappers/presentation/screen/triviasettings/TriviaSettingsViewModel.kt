package com.guilherme.braintappers.presentation.screen.triviasettings

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.model.DropdownItem
import com.guilherme.braintappers.navigation.TriviaScreen
import com.guilherme.braintappers.presentation.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TriviaSettingsState(

    val categoryId: String? = null,

    val isNumberOfQuestionsMenuOpen: Boolean = false,
    val numberOfQuestionsValue: DropdownItem? = null,

    val isDifficultyMenuOpen: Boolean = false,
    val difficultyValue: DropdownItem? = null,

    val isTypeMenuOpen: Boolean = false,
    val typeValue: DropdownItem? = null
)

sealed interface TriviaSettingsEvents {
    data object OpenNumberOfQuestionsDropdownMenu : TriviaSettingsEvents
    data class OnNumberOfQuestionsSelected(val value: DropdownItem) : TriviaSettingsEvents

    data object OpenDifficultyMenu : TriviaSettingsEvents
    data class OnDifficultySelected(val value: DropdownItem) : TriviaSettingsEvents

    data object OpenTypeMenu : TriviaSettingsEvents
    data class OnTypeSelected(val value: DropdownItem) : TriviaSettingsEvents

    data class OnStartButtonClicked(val value: NavController) : TriviaSettingsEvents

    data object DismissDropdownMenu : TriviaSettingsEvents
}


class TriviaSettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(TriviaSettingsState())
    val state = _state.asStateFlow()

    val numberOfQuestions = (1..10).map { number ->
        DropdownItem(
            apiParameter = "amount=$number",
            text = UiText.StringResource(
                resId = if (number == 1) R.string.question else R.string.questions,
                number.toString()
            ),
            onClick = { onEvent(TriviaSettingsEvents.OnNumberOfQuestionsSelected(it)) }
        )
    }

    val difficulty = listOf(
        DropdownItem(
            apiParameter = "", //Empty
            text = UiText.StringResource(resId = R.string.any_difficulty),
            onClick = { onEvent(TriviaSettingsEvents.OnDifficultySelected(it)) }
        ),
        DropdownItem(
            apiParameter = "&difficulty=easy",
            text = UiText.StringResource(resId = R.string.easy),
            onClick = { onEvent(TriviaSettingsEvents.OnDifficultySelected(it)) }
        ),
        DropdownItem(
            apiParameter = "&difficulty=medium",
            text = UiText.StringResource(resId = R.string.medium),
            onClick = { onEvent(TriviaSettingsEvents.OnDifficultySelected(it)) }
        ),
        DropdownItem(
            apiParameter = "&difficulty=hard",
            text = UiText.StringResource(resId = R.string.hard),
            onClick = { onEvent(TriviaSettingsEvents.OnDifficultySelected(it)) }
        ),
    )

    val type = listOf(
        DropdownItem(
            apiParameter = "",
            text = UiText.StringResource(resId = R.string.any_type),
            onClick = { onEvent(TriviaSettingsEvents.OnTypeSelected(it)) }
        ),
        DropdownItem(
            apiParameter = "&type=multiple",
            text = UiText.StringResource(resId = R.string.multiple_choice),
            onClick = { onEvent(TriviaSettingsEvents.OnTypeSelected(it)) }
        ),
        DropdownItem(
            apiParameter = "&type=boolean",
            text = UiText.StringResource(resId = R.string.true_false),
            onClick = { onEvent(TriviaSettingsEvents.OnTypeSelected(it)) }
        ),
    )

    fun setCategoryId(categoryId: String) {
        _state.update {
            it.copy(
                categoryId = categoryId
            )
        }
    }

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

            is TriviaSettingsEvents.OnStartButtonClicked -> {

                val categoryId = _state.value.categoryId ?: ""

                val numberOfQuestions: String =
                    _state.value.numberOfQuestionsValue?.apiParameter ?: "amount=10"

                val difficulty: String = _state.value.difficultyValue?.apiParameter ?: ""

                val type: String = _state.value.typeValue?.apiParameter ?: ""

                event.value.navigate(
                    TriviaScreen(
                        categoryId = categoryId,
                        numberOfQuestions = numberOfQuestions,
                        difficulty = difficulty,
                        type = type
                    )
                )
            }

            TriviaSettingsEvents.DismissDropdownMenu -> {
                _state.update {
                    it.copy(
                        isNumberOfQuestionsMenuOpen = false,
                        isDifficultyMenuOpen = false,
                        isTypeMenuOpen = false
                    )
                }
            }

        }
    }

}