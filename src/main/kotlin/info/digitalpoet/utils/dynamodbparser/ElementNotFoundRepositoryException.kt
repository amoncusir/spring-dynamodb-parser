package info.digitalpoet.utils.dynamodbparser

/** <!-- Documentation for: info.digitalpoet.utils.dynamodbparser.ElementNotFoundRepositoryException on 18/10/18 -->
 *
 * @author Aran Moncusí Ramírez
 */
class ElementNotFoundRepositoryException(val entity: String, val element: Any, cause: Throwable? = null):
    Throwable("The element $element not found in repository", cause)
