package gs.calendar.appointments.frontend.redux

import finally
import gs.calendar.appointments.frontend.App
import gs.calendar.appointments.model.Agenda
import gs.calendar.appointments.model.Slot
import gs.calendar.appointments.model.User
import notistack.WithSnackbar
import redux.RAction
import snackbar
import kotlin.js.Promise

fun <T> Promise<T>.uiLinked(withSnackbar: WithSnackbar): Promise<T> {
    catch { it.snackbar(withSnackbar) }
    finally { StopLoading.dispatch() }
    store.dispatch(StartLoading)
    return this
}

fun Action.dispatch() = store.dispatch(this)

sealed class Action : RAction {

    abstract operator fun invoke(state: App.State): App.State

    override fun toString() = this::class.simpleName!!

}

object StartLoading : Action() {

    override fun invoke(state: App.State) = state.copy(
        loadingCount = state.loadingCount + 1
    )

}

object StopLoading : Action() {

    override fun invoke(state: App.State) = state.copy(
        loadingCount = (state.loadingCount - 1).takeUnless { it < 0 } ?: throw IllegalStateException("loadingCount < 0")
    )

}

data class ChangeUser(private val user: User?) : Action() {

    override fun invoke(state: App.State) = state.copy(
        currentUser = user
    )

}

data class SetAgendas(private val agendas: List<Agenda>?) : Action() {

    override fun invoke(state: App.State) = state.copy(
        agendas = agendas,
        currentAgenda = agendas?.first()
    )

}

data class SelectAgenda(private val agenda: Agenda?) : Action() {

    override fun invoke(state: App.State) = state.copy(
        currentAgenda = agenda,
        currentSlot = null
    )

}

data class SelectSlot(private val slot: Slot?) : Action() {

    override fun invoke(state: App.State) = state.copy(
        currentSlot = slot
    )

}

object RefreshSlots : Action() {

    override fun invoke(state: App.State) = state.copy(
        currentAgenda = state.currentAgenda?.copy(),
        currentSlot = null
    )

}
