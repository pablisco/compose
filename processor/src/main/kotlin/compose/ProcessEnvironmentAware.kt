package compose

import com.sun.tools.javac.model.JavacElements
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

interface ProcessEnvironmentAware {

    val environment: ProcessingEnvironment

}

val ProcessEnvironmentAware.filer: Filer
    get() =  environment.filer

val ProcessEnvironmentAware.elements: Elements
    get() = environment.elementUtils

val ProcessEnvironmentAware.javaElements: JavacElements
    get() = elements as JavacElements

fun ProcessEnvironmentAware.printError(element: Element, message: String?) =
    environment.messager.printMessage(Diagnostic.Kind.ERROR, message ?: "Unknonwn error", element)

fun ProcessEnvironmentAware.print(message: String) =
    environment.messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, message)
