package murkeev.polling;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PollingRequestRegistry {

    private final Map<UUID, DeferredResult<String>> requestMap = new ConcurrentHashMap<>();

    public void register(UUID orderId, DeferredResult<String> result) {
        requestMap.put(orderId, result);
    }

    public void complete(UUID orderId, String newStatus) {
        DeferredResult<String> result = requestMap.remove(orderId);
        if (result != null) {
            result.setResult(newStatus);
        }
    }

    public boolean hasPendingRequest(UUID orderId) {
        return requestMap.containsKey(orderId);
    }
}
