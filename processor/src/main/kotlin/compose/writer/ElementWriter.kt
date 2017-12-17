package compose.writer

import compose.ProcessEnvironmentAware
import compose.filer
import compose.javaElements
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.JavaFileObject



class ElementWriter(override val environment: ProcessingEnvironment): ProcessEnvironmentAware {

    fun write(element: Element) {
        val tree = javaElements.getTree(element)

        when(element) {
            is TypeElement -> write(element)
        }
    }

    fun write(element: TypeElement) {
        element.enclosingElement
        val fileObject = filer.createSourceFile(element.qualifiedName)
        element.enclosedElements.forEach {
            write(element)
        }
    }

}