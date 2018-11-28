package info.digitalpoet.utils.dynamodbparser

import com.amazonaws.services.dynamodbv2.model.AttributeValue

/** <!-- Documentation for: info.digitalpoet.utils.dynamodbparser.AttributeValueUtils on 18/10/18 -->
 *
 * @author Aran Moncusí Ramírez
 */
//~ Constants ==========================================================================================================

//~ Functions ==========================================================================================================

//~ Extensions =========================================================================================================

fun String.toAttributeValue(): AttributeValue = AttributeValue(this)

fun Long.toAttributeValue(): AttributeValue = AttributeValue().withN(this.toString())

fun Int.toAttributeValue(): AttributeValue = AttributeValue().withN(this.toString())

//~ Annotations ========================================================================================================

//~ Interfaces =========================================================================================================

//~ Enums ==============================================================================================================

//~ Data Classes =======================================================================================================

//~ Classes ============================================================================================================

//~ Sealed Classes =====================================================================================================

//~ Objects ============================================================================================================