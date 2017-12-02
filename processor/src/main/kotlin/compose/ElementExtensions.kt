package compose

import com.google.auto.common.MoreElements
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

internal fun Element.isClass() =
    kind == ElementKind.CLASS

internal fun <A : Annotation> Element.annotationFor(clazz: Class<A>): AnnotationMirror? =
    MoreElements.getAnnotationMirror(this, clazz).orNull()