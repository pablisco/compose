package compose

import com.squareup.javapoet.TypeName
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.type.TypeMirror

class ComposeAnnotationMirror(
    annotationMirror: AnnotationMirror
) {

    private val parentType: TypeMirror = annotationMirror.valueFor(Compose::value)!!
    val parentName = TypeName.get(parentType)!!

    private val prefix = annotationMirror.valueFor(Compose::prefix) ?: ""
    private val postfix = annotationMirror.valueFor(Compose::postfix) ?: ""

    fun composeName(original: CharSequence): String = when {
        prefix.isEmpty() && prefix.isEmpty() -> "Composed$original"
        else -> "$prefix$original$postfix"
    }

}