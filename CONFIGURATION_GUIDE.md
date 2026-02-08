# Blackline to S/4HANA - Optimized Interface Setup

## 1. Professional Error Handling (Groovy)
Based on the analyzed iFlow structure, the error handling is now modularized. 
**Action:** Create a new file in your iFlow's `/script` folder named `errorHandler.groovy` and paste the following code:

```groovy
import com.sap.gateway.ip.core.customdev.util.Message
def Message processData(Message message) {
    def map = message.getProperties()
    def log = messageLogFactory.getMessageLog(message)
    def ex = map.get("CamelExceptionCaught")
    
    if (ex != null && log != null) {
        log.addAttachmentAsString("Technical_Error", ex.toString(), "text/plain")
        log.addAttachmentAsString("Payload_At_Failure", message.getBody(String.class), "application/json")
        log.setStringProperty("CustomStatus", "Error_In_Blackline_Inbound")
    }
    return message
}
```

## 2. Optimized iFlow Logic (Key Properties)
When configuring your iFlow elements in the UI, use these exact settings to ensure stability:

### Sender Adapter (HTTPS)
- **Address:** `/blackline/v1/inbound`
- **Sender Auth:** `RoleBased` (ESBMessaging.send)

### Receiver Adapter (HTTP)
- **Address:** `http://s4hana-virtual-host:8000/sap/bc/srt/rfc/sap/blackline_service`
- **Proxy Type:** `On-Premise`
- **Authentication:** `Basic`
- **Credential Name:** `S4_TECHNICAL_USER` (Must exist in Security Material)
- **Timeout:** `60000` (1 Minute for heavy financial postings)

## 3. Stability Improvements
- **Request-Reply Pattern:** Use a Request-Reply step instead of a simple Message Flow to S/4HANA to ensure Blackline receives the technical response from the SAP Framework.
- **Payload Validation:** Add an XML/JSON Validator step before the mapping to catch malformed Blackline requests early.
