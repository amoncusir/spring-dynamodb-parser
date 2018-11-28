package info.digitalpoet.utils.dynamodbparser

import com.amazonaws.services.dynamodbv2.model.AttributeValue

/** <!-- Documentation for: info.digitalpoet.utils.dynamodbparser.RequestDynamoObject on 17/10/18 -->
 *
 * @author Aran Moncusí Ramírez
 */
open class RequestDynamoObject(
    val attributes: Map<String, AttributeValue>
)
{
    //~ Constants ======================================================================================================

    //~ Values =========================================================================================================

    open var uniqueAttributes: List<String> = listOf()

    //~ Properties =====================================================================================================

    //~ Constructors ===================================================================================================

    //~ Open Methods ===================================================================================================

    //~ Methods ========================================================================================================

    //~ Operators ======================================================================================================
}
