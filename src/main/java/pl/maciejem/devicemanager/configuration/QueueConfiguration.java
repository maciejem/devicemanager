package pl.maciejem.devicemanager.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.maciejem.devicemanager.service.statusexpiration.StatusExpirationDelayObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

@Configuration
public class QueueConfiguration {

    /**
     * Queue holds information when are expiration time of devices status
     */
    @Bean
    public BlockingQueue<StatusExpirationDelayObject> statusExpirationDelayQueue() {
        return new DelayQueue<>();
    }
}
