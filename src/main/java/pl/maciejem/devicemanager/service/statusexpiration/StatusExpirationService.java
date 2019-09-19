package pl.maciejem.devicemanager.service.statusexpiration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.maciejem.devicemanager.persitence.dao.DeviceRepository;
import pl.maciejem.devicemanager.persitence.model.DeviceStatus;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * StatusExpirationService manages status expiration actions, like status expire action,
 * adding to waiting for expiration queue, removing from waiting for expiration queue.
 */
@Service
@Slf4j
public class StatusExpirationService {

    /**
     * Time after device's status gets expired and StatusExpirationDelayObject can be consumed from the queue.
     */
    @Value("${app.statusExpirationTime}")
    private long statusExpirationTime;

    private BlockingQueue<StatusExpirationDelayObject> statusExpirationDelayQueue;

    /**
     * The connection between ids of the devices and {@link StatusExpirationDelayObject} objects
     * which are in waiting queue for expiration. It helps to find object in the queue to remove it when
     * status is changed from OK to UNHEALTHY
     */
    private ConcurrentHashMap<UUID, StatusExpirationDelayObject> deviceIdToStatusExpirationDelayObject = new ConcurrentHashMap<>();
    private DeviceRepository deviceRepository;

    public StatusExpirationService(BlockingQueue<StatusExpirationDelayObject> statusExpirationDelayQueue, DeviceRepository deviceRepository) {
        this.statusExpirationDelayQueue = statusExpirationDelayQueue;
        this.deviceRepository = deviceRepository;
    }

    public void updateStatusExpirationDelayQueue(UUID deviceId, DeviceStatus deviceStatus) {
        if (deviceStatus.equals(DeviceStatus.OK)) {
            addToStatusExpirationDelayQueue(deviceId);
        } else if (deviceStatus.equals(DeviceStatus.UNHEALTHY)) {
            removeFromStatusExpirationDelayQueue(deviceId);
        }
    }

    void updateDeviceStatusAfterExpiration(UUID deviceId) {
        log.info("Updating device with deviceId={} after expiration time", deviceId);
        deviceRepository.updateDeviceStatus(DeviceStatus.STALE, deviceId);
        deviceIdToStatusExpirationDelayObject.remove(deviceId);
    }

    private void removeFromStatusExpirationDelayQueue(UUID deviceId) {
        log.info("Removing deviceId={} from statusExpirationDelayQueue", deviceId);
        StatusExpirationDelayObject statusExpirationDelayObject = deviceIdToStatusExpirationDelayObject.get(deviceId);
        statusExpirationDelayQueue.remove(statusExpirationDelayObject);
        deviceIdToStatusExpirationDelayObject.remove(deviceId);
    }

    private void addToStatusExpirationDelayQueue(UUID deviceId) {
        try {
            log.info("Adding deviceId={} to statusExpirationDelayQueue", deviceId);
            StatusExpirationDelayObject statusExpirationDelayObject = new StatusExpirationDelayObject(deviceId, statusExpirationTime);
            deviceIdToStatusExpirationDelayObject.put(deviceId, statusExpirationDelayObject);
            statusExpirationDelayQueue.put(statusExpirationDelayObject);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}