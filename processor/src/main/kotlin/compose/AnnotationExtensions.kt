package compose

import javax.lang.model.element.AnnotationMirror
import kotlin.reflect.KProperty1

@Suppress("UNCHECKED_CAST")
internal fun <T> AnnotationMirror.valueFor(key: String): T? =
    elementValues.filter { (k, _) -> k.simpleName.toString() == key }
        .map { (_, v) -> v }
        .firstOrNull()?.value as T?

internal fun <T> AnnotationMirror.valueFor(p: KProperty1<Compose, Any>): T? =
    valueFor(p.name)