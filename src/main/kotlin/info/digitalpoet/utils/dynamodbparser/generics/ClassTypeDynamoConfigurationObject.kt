package info.digitalpoet.utils.dynamodbparser.generics

import com.amazonaws.services.dynamodbv2.datamodeling.ItemConverter
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import info.digitalpoet.utils.dynamodbparser.ClassTypeAgent
import info.digitalpoet.utils.dynamodbparser.DynamoConfigurationObject
import info.digitalpoet.utils.dynamodbparser.DynamoDBUnique
import info.digitalpoet.utils.dynamodbparser.RequestDynamoObject
import info.digitalpoet.utils.dynamodbparser.reflection.extract
import mu.KotlinLogging
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/** <!-- Documentation for: info.digitalpoet.utils.dynamodbparser.generics.ClassTypeDynamoConfigurationObject on 17/10/18 -->
 *
 * @author Aran Moncusí Ramírez
 */
open class ClassTypeDynamoConfigurationObject(
    val converter: ItemConverter,
    val classType: ClassTypeAgent,
    override val tableName: String
):
    DynamoConfigurationObject
{
    //~ Constants ======================================================================================================

    companion object
    {
        private val logger = KotlinLogging.logger {}

        const val CLASS_NAME_ATTRIBUTE = "ClassId"
    }

    //~ Values =========================================================================================================

    //~ Properties =====================================================================================================

    //~ Constructors ===================================================================================================

    //~ Open Methods ===================================================================================================

    override fun <T: Any> prepareObject(obj: T): RequestDynamoObject
    {
        logger.trace { "Prepare new Object: $obj" }

        val convert = converter.convert(obj)
        embedClassIdInDynamoMap(convert, classType.parse(obj))

        logger.trace { "Objected Converted[$obj]: $convert" }

        val request = RequestDynamoObject(convert)
            .apply {
                uniqueAttributes = getUniqueAttributes(obj::class)
            }

        logger.trace { "New DynamoRequest: $request" }

        return request
    }

    override fun <T: Any> createObject(element: MutableMap<String, AttributeValue>): T
    {
        val klass = this.extractClassIdFromDynamoMap<T>(element)

        logger.trace { "Create new Object[$klass]: $element" }

        val objectConverted = converter.unconvert(klass.java, element)

        logger.trace { "Parsed Object[$klass]: $objectConverted" }

        return objectConverted
    }

    //~ Methods ========================================================================================================

    @Suppress("UnsafeCast")
    open fun <T: Any> getUniqueAttributes(klass: KClass<out T>): List<String>
    {
        return extract<DynamoDBUnique>(klass)
            .asSequence()
            .filter { it.second is KProperty<*> }
            .map { it.second as KProperty<*> }
            .map { it.name }
            .toList()
            .apply { logger.trace { "Extract UniqueAttributes from $klass -> $this" } }
    }

    //:: Class Embedded ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    open fun <T: Any> extractClassIdFromDynamoMap(element: MutableMap<String, AttributeValue>): KClass<out T>
    {
        val classId = element[CLASS_NAME_ATTRIBUTE] ?:
                throw IllegalArgumentException("Element not contains $CLASS_NAME_ATTRIBUTE attribute")

        logger.trace { "Extract ClassID from $element -> $classId" }

        element.remove(CLASS_NAME_ATTRIBUTE)

        return classType.getKClass(classId.s!!)
    }

    open fun embedClassIdInDynamoMap(element: MutableMap<String, AttributeValue>, classId: String)
    {
        if (CLASS_NAME_ATTRIBUTE in element)
            throw IllegalArgumentException("The attribute $CLASS_NAME_ATTRIBUTE is reserved for mapper logic")

        element[CLASS_NAME_ATTRIBUTE] = AttributeValue().withS(classId)

        logger.trace { "Embed ClassId in $element <- $classId" }
    }

    //~ Operators ======================================================================================================
}
