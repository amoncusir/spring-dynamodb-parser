package info.digitalpoet.utils.dynamodbparser

/** <!-- Documentation for: info.digitalpoet.utils.dynamodbparser.DuplicatedElementRepositoryException on 18/10/18 -->
 *
 * @author Aran Moncusí Ramírez
 */
class DuplicatedElementRepositoryException(val entity: String, val element: Any, cause: Throwable? = null):
    Throwable("The element $element is duplicated in repository", cause)
