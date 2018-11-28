package info.digitalpoet.utils.dynamodbparser.generics

import info.digitalpoet.utils.dynamodbparser.ClassTypeAgent
import mu.KotlinLogging
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/** <!-- Documentation for: info.digitalpoet.utils.dynamodbparser.generics.QualifiedNameClassTypeAgent on 17/10/18 -->
 *
 * @author Aran Moncusí Ramírez
 */
@Component
class QualifiedNameClassTypeAgent: ClassTypeAgent
{
    //~ Constants ======================================================================================================

    companion object
    {
        private val logger = KotlinLogging.logger {}
    }

    //~ Values =========================================================================================================

    //~ Properties =====================================================================================================

    //~ Constructors ===================================================================================================

    //~ Open Methods ===================================================================================================

    //~ Methods ========================================================================================================

    /**
     * Get [KClass] instance from [String]
     *
     * @param parsed String Identifier from get [KClass]
     * @return KClass<T>
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(ClassNotFoundException::class)
    override fun <T: Any> getKClass(parsed: String): KClass<out T>
    {
        logger.trace { "Create KClass from $parsed" }

        return Class.forName(parsed).kotlin as KClass<out T>
    }

    /**
     * Transform [KClass] to [String]
     *
     * @param type KClass<T>
     * @return String
     */
    override fun <T: Any> parse(type: T): String
    {
        val classId = type::class.qualifiedName

        logger.trace { "Parse object $type -> $classId" }

        return classId ?: throw IllegalArgumentException("Can't parse a anonymous class")
    }

    //~ Operators ======================================================================================================
}
