package info.digitalpoet.utils.dynamodbparser

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.AmazonWebServiceResult
import com.amazonaws.ResponseMetadata
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.utils.NameMap
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.model.AttributeAction
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate
import com.amazonaws.services.dynamodbv2.model.Condition
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException
import com.amazonaws.services.dynamodbv2.model.GetItemResult
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.QueryRequest
import com.amazonaws.services.dynamodbv2.model.QueryResult
import com.amazonaws.services.dynamodbv2.model.ReturnValue
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest
import mu.KotlinLogging
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/** <!-- Documentation for: info.digitalpoet.utils.dynamodbparser.AbstractDynamoRepository on 16/10/18 -->
 *
 * @author Aran Moncusí Ramírez
 */
abstract class AbstractDynamoRepository<T: Any>(
    val client: AmazonDynamoDB,
    val configuration: DynamoConfigurationObject
):
    DynamoRepository<T>
{
    //~ Constants ======================================================================================================

    companion object
    {
        private val logger = KotlinLogging.logger {}

        private const val ENTITY = "Conversation"
    }

    //~ Values =========================================================================================================

    val dynamo = DynamoDB(client)

    val table = dynamo.getTable(configuration.tableName)

    //~ Properties =====================================================================================================

    //~ Constructors ===================================================================================================

    //~ Open Methods ===================================================================================================

    @Suppress("MagicNumber")
    open fun <T: ResponseMetadata> AmazonWebServiceResult<T>.isOK(): Boolean =
        sdkHttpMetadata.httpStatusCode in 200..299

    open fun <T> Mono<T>.repositoryParse(): Mono<T> = this

    open fun <T> Flux<T>.repositoryParse(): Flux<T> = this

    open fun Mono<GetItemResult>.convert(entity: String, element: String): Mono<T> = this
        .repositoryParse()
        .map { it.item ?: throw ElementNotFoundRepositoryException(entity, element) }
        .map { configuration.createObject<T>(it) }

    /**
     * Create new object in DynamoDB, fails if object exists.
     *
     * @param obj T The object to create
     * @return T The object saved with keys if it didn't have
     */
    @Throws(DuplicatedElementRepositoryException::class)
    override fun create(obj: T): Mono<T>
    {
        logger.trace { "Create new Object in DB $obj" }

        val dynamoObject = configuration.prepareObject(obj)
        val request = makePutItemRequest(dynamoObject)

        return Mono.fromCallable { client.putItem(request) }
            .onErrorMap(ConditionalCheckFailedException::class.java)
                { DuplicatedElementRepositoryException(ENTITY, dynamoObject, it) }
            .repositoryParse()
            .map { obj }
    }

    /**
     *
     * @param query QueryRequest
     * @return OperationResult<QueryResult>
     */
    override fun query(query: QueryRequest): Mono<QueryResult>
    {
        logger.trace { "Query operation: $query" }
        return Mono.fromCallable { client.query(query) }
            .repositoryParse()
    }

    /**
     * Update entity using only not null fields.
     *
     * Throw if the entity not exist in DB
     *
     * @param message T Object to update
     * @return OperationResult<T> Updated object with all fields
     */
    override fun updateOnlyNotNullFields(obj: T): Mono<Unit>
    {
        val dynamoObject = configuration.prepareObject(obj)
        val request = makeUpdateItemRequest(dynamoObject)

        logger.trace { "Update object using only not null fields: ${dynamoObject.attributes}" }

        return Mono.fromCallable { client.updateItem(request) }
            .onErrorMap(ConditionalCheckFailedException::class.java)
                { ElementNotFoundRepositoryException(ENTITY, dynamoObject, it) }
            .repositoryParse()
            .map {  }
    }

    //~ Util methods ===================================================================================================

    fun nameMap(vararg pair: Pair<String, String>): NameMap
    {
        val nameMap = NameMap()

        for (it in pair) nameMap.with(it.first, it.second)

        return nameMap
    }

    fun valueMap(): ValueMap = ValueMap()

    fun <T: Any> prepareObject(obj: T): Map<String, AttributeValue> = configuration.prepareObject(obj).attributes

    fun <T: Any> createObject(attr: MutableMap<String, AttributeValue>): T = configuration.createObject(attr)

    open fun createQuery(): QueryRequest = prepareRequest(QueryRequest(configuration.tableName))

    open fun createQuery(conditions: Map<String, Condition>): QueryRequest = createQuery().withKeyConditions(conditions)

    //:: PutItem Requests ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    open fun makePutItemRequest(value: RequestDynamoObject): PutItemRequest
    {
        val request = PutItemRequest(configuration.tableName, value.attributes)

        request.withConditionExpression(makeConditionExpressionAttributeNotExist(value))

        return prepareRequest(request)
    }

    //:: PutItem Requests ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    open fun makeUpdateItemRequest(value: RequestDynamoObject): UpdateItemRequest
    {
        val updateAttr = value.attributes
            .filterKeys { !(it in value.uniqueAttributes) }
            .mapValues { AttributeValueUpdate(it.value, AttributeAction.PUT) }

        val keys = value.attributes
            .filterKeys { it in value.uniqueAttributes }

        logger.trace { "Create UpdateItemRequest(Keys: $keys, Values: $updateAttr)" }

        return prepareRequest(UpdateItemRequest(configuration.tableName, keys, updateAttr, ReturnValue.NONE))
    }

    open fun <R: AmazonWebServiceRequest> prepareRequest(request: R): R
    {
        request.requestClientOptions.appendUserAgent(getUserAgent())

        logger.trace { "Prepare new request: $request" }

        return request
    }

    //:: Utils :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    open fun makeConditionExpressionAttributeNotExist(value: RequestDynamoObject) =
        value.uniqueAttributes.joinToString(separator = " AND ") { "attribute_not_exists($it)" }

    open fun getUserAgent(): String = configuration.userAgent

    //~ Methods ========================================================================================================

    //~ Operators ======================================================================================================
}
