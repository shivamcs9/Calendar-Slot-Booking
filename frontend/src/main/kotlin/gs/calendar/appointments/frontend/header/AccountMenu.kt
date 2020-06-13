package gs.calendar.appointments.frontend.header

import allOf
import css
import gs.calendar.appointments.frontend.redux.ChangeUser
import gs.calendar.appointments.frontend.redux.dispatch
import gs.calendar.appointments.frontend.redux.uiLinked
import gs.calendar.appointments.frontend.userAvatar
import gs.calendar.appointments.model.User
import kotlinext.js.jsObject
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.LinearDimension
import kotlinx.css.padding
import kotlinx.css.px
import material_ui.core.TooltipPlacement
import material_ui.core.TypographyVariant
import material_ui.core.iconButton
import material_ui.core.listItemAvatar
import material_ui.core.menu
import material_ui.core.menuItem
import material_ui.core.styles.WithTheme
import material_ui.core.styles.withTheme
import material_ui.core.tooltip
import material_ui.core.typography
import notistack.WithSnackbar
import notistack.withSnackbar
import onClick
import org.w3c.dom.Element
import rClass
import react.RBuilder
import react.RComponent
import react.RHandler
import react.RState
import react.dom.strong
import react.setState
import kotlin.browser.window
import kotlin.js.Promise

class AccountMenu : RComponent<AccountMenu.Props, AccountMenu.State>() {

    override fun RBuilder.render() {
        tooltip(
            title = props.user.run { "$name <$email>" },
            placement = TooltipPlacement.BOTTOM_END
        ) {
            iconButton {
                css { marginLeft = props.theme.spacing.unit.px }

                userAvatar(props.user)
                onClick { ev ->
                    val target = ev.target as Element

                    setState { menuAnchor = target }
                }
            }
        }
        menu(open = state.menuAnchor != null,
            anchorEl = state.menuAnchor,
            onClose = { setState { menuAnchor = null } }) {
            attrs.menuListProps = jsObject { css { padding(vertical = 0.px) } }

            menuItem(divider = true, disabled = true) {
                css {
                    height = LinearDimension.auto
                    opacity = 1
                    backgroundColor = Color.lightGray
                }

                listItemAvatar { userAvatar(props.user) }
                typography(variant = TypographyVariant.CAPTION) {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        marginLeft = props.theme.spacing.unit.px
                    }

                    props.user.name?.let { strong { +it } }
                    +props.user.email
                }
            }
            menuItem {
                onClick {
                    setState { menuAnchor = null }

                    window.asDynamic().gapi.auth2.getAuthInstance()
                        .signOut().unsafeCast<Promise<*>>()
                        .uiLinked(props)
                        .then { ChangeUser(null).dispatch() }
                }
                +"Logout"
            }
        }
    }

    interface Props : WithTheme, WithSnackbar {
        var user: User
    }

    interface State : RState {
        var menuAnchor: Element?
    }

}

private val wrapped = allOf<AccountMenu.Props>(withTheme(), withSnackbar())(AccountMenu::class.rClass)

fun RBuilder.accountMenu(
    user: User,
    handler: (RHandler<AccountMenu.Props>) = {}
) = wrapped {
    attrs.user = user

    handler(this)
}
