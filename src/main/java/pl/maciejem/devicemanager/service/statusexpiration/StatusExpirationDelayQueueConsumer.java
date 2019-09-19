package pl.maciejem.devicemanager.service.statusexpiration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

/**
 * StatusExpirationDelayQueueConsumer consumes objects from statusExpirationDelayQueue.
 * When the expiration time of {@link StatusExpirationDelayObject} has passed it
 * is consumed and the status gets expired.
 */
@Slf4j
@AllArgsConstructor
@Component
public class StatusExpirationDelayQueueConsumer implements Runnable {

    private BlockingQueue<StatusExpirationDelayObject> statusExpirationDelayQueue;
    private StatusExpirationService statusExpirationService;

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {
            try {
                StatusExpirationDelayObject statusExpirationDelayObject = statusExpirationDelayQueue.take();
                log.info("Take statusExpirationDelayObject from the statusExpirationDelayQueue (thread: {}, delayObject {}",
                        Thread.currentThread().getName(), statusExpirationDelayObject);
                statusExpirationService.updateDeviceStatusAfterExpiration(statusExpirationDelayObject.getDeviceId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}