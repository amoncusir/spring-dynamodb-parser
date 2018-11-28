package info.digitalpoet.utils.dynamodbparser

import com.amazonaws.services.dynamodbv2.model.QueryRequest
import com.amazonaws.services.dynamodbv2.model.QueryResult
import reactor.core.publisher.Mono

/** <!-- Documentation for: info.digitalpoet.utils.dynamodbparser.DynamoRepository on 17/10/18 -->
 *
 * @author Aran Moncusí Ramírez
 */
interface DynamoRepository<T>
{
    //~ Constants ======================================================================================================

    //~ Values =========================================================================================================

    //~ Properties =====================================================================================================

    //~ Methods ========================================================================================================

    /**
     * Create new object in DynamoDB, fails if object exists.
     *
     * @param obj T The object to create
     * @return OperationResult<T> The object saved with keys if it didn't have
     */
    fun create(obj: T): Mono<T>

    fun query(query: QueryRequest): Mono<QueryResult>

    /**
     * Update entity using only not null fields.
     *
     * @param message T Object to update
     * @return OperationResult<T> Updated object with all fields
     */
    fun updateOnlyNotNullFields(obj: T): Mono<Unit>

    //~ Operators ======================================================================================================
}
