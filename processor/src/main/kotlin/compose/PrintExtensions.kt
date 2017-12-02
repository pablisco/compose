package compose

import javax.lang.model.element.Element
import javax.tools.Diagnostic

fun ProcessEnvironmentAware.printError(element: Element, message: String?) =
    environment.messager.printMessage(Diagnostic.Kind.ERROR, message ?: "Unknonwn error", element)

fun ProcessEnvironmentAware.print(message: String) =
    environment.messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, message)