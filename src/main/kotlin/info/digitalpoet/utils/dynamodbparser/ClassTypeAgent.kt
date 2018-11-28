package info.digitalpoet.utils.dynamodbparser

import kotlin.reflect.KClass

/** <!-- Documentation for: info.digitalpoet.utils.dynamodbparser.ClassTypeAgent on 17/10/18 -->
 *
 * @author Aran Moncusí Ramírez
 */
interface ClassTypeAgent
{
    //~ Constants ======================================================================================================

    //~ Values =========================================================================================================

    //~ Properties =====================================================================================================

    //~ Methods ========================================================================================================

    /**
     * Get [KClass] instance from [String]
     *
     * @param parsed String Identifier from get [KClass]
     * @return KClass<T>
     */
    fun <T: Any> getKClass(parsed: String): KClass<out T>

    /**
     * Transform [KClass] to [String]
     *
     * @param type T
     * @return String
     */
    fun <T: Any> parse(type: T): String

    //~ Operators ======================================================================================================
}
