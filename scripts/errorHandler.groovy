import com.sap.gateway.ip.core.customdev.util.Message
import java.util.HashMap

/**
 * Professional Error Logging for Blackline Inbound Interface
 * Extracted from SAP Best Practices study
 */
def Message processData(Message message) {
    def map = message.getProperties()
    def log = messageLogFactory.getMessageLog(message)
    
    // 1. Capture the Exception
    def ex = map.get("CamelExceptionCaught")
    
    if (ex != null && log != null) {
        // Log the technical stacktrace for developers
        log.addAttachmentAsString("Technical_Error_StackTrace", ex.toString(), "text/plain")
        
        // Log the business payload that caused the crash
        def body = message.getBody(java.lang.String.class)
        log.addAttachmentAsString("Business_Payload_At_Failure", body, "application/json")
        
        // 2. Set Custom Status for Monitoring (MPL)
        log.setStringProperty("Error_Category", "Integration_Suite_Exception")
        
        // 3. Extract specific error message for the Return Payload
        def errorMessage = ex.getMessage() ?: "Unknown error occurred during processing"
        message.setProperty("FormattedErrorMessage", errorMessage)
    }
    
    return message
}
