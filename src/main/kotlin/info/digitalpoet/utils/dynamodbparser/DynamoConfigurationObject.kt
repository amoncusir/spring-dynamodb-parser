package info.digitalpoet.utils.dynamodbparser

import com.amazonaws.services.dynamodbv2.model.AttributeValue

/** <!-- Documentation for: info.digitalpoet.utils.dynamodbparser.DynamoConfigurationObject on 17/10/18 -->
 *
 * @author Aran Moncusí Ramírez
 */
interface DynamoConfigurationObject
{
    //~ Constants ======================================================================================================

    //~ Values =========================================================================================================

    val tableName: String

    val userAgent: String
        get() = "DigitalPoetParser"

    //~ Properties =====================================================================================================

    //~ Methods ========================================================================================================

    fun <T: Any> prepareObject(obj: T): RequestDynamoObject

    fun <T: Any> createObject(element: MutableMap<String, AttributeValue>): T

    //~ Operators ======================================================================================================
}
