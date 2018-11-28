package info.digitalpoet.utils.dynamodbparser.reflection

import info.digitalpoet.utils.kotlonic.allProperties
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.safeCast

/** <!-- Documentation for: info.digitalpoet.utils.dynamodbparser.reflection.AnnotationMetadataExtractor on 17/10/18 -->
 *
 * @author Aran Moncusí Ramírez
 */
//~ Constants ==========================================================================================================

//~ Functions ==========================================================================================================

inline fun <reified E: Annotation> extract(klass: KClass<*>): List<Pair<Annotation, Any>> =
    extractAllProperties(klass, E::class)

fun <T : Annotation> KAnnotatedElement.findAnnotation(klass: KClass<T>): T? =
    klass.safeCast(annotations.firstOrNull { klass.isInstance(it) })

fun <T: Annotation> extractAllProperties(klass: KClass<*>, annotationClass: KClass<T>): List<Pair<T, Any>>
{
    return klass
        .allProperties()
        .asSequence()
        .filterNotNull()
        .filter { it.findAnnotation(annotationClass) != null }
        .map { it.findAnnotation(annotationClass)!! to it }
        .toList()
}

//~ Extensions =========================================================================================================

//~ Annotations ========================================================================================================

//~ Interfaces =========================================================================================================

//~ Enums ==============================================================================================================

//~ Data Classes =======================================================================================================

//~ Classes ============================================================================================================

//~ Sealed Classes =====================================================================================================

//~ Objects ============================================================================================================
