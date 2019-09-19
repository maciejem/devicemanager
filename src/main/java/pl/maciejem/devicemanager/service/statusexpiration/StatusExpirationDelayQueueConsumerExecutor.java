package pl.maciejem.devicemanager.service.statusexpiration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.Lifecycle;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * StatusExpirationDelayQueueConsumerExecutor runs and manages lifecycle of thread with
 * {@link StatusExpirationDelayQueueConsumer}
 */
@Component
@Slf4j
public class StatusExpirationDelayQueueConsumerExecutor implements Lifecycle {
    private StatusExpirationDelayQueueConsumer statusExpirationDelayQueueConsumer;
    private ExecutorService executorService;

    public StatusExpirationDelayQueueConsumerExecutor(StatusExpirationDelayQueueConsumer statusExpirationDelayQueueConsumer) {
        this.statusExpirationDelayQueueConsumer = statusExpirationDelayQueueConsumer;
    }

    @PostConstruct
    public void runStatusExpirationDelayQueueConsumer() {
        log.info("Starting StatusExpirationDelayQueueConsumer");
        this.executorService = Executors.newSingleThreadExecutor();
        executorService.submit(statusExpirationDelayQueueConsumer);
    }

    public boolean isRunning() {
        return executorService.isTerminated();
    }

    public void start() {
    }

    public void stop() {
        log.info("Stopping StatusExpirationDelayQueueConsumer");
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            System.out.println("StatusExpirationDelayQueueConsumer thread never terminated");
        }
    }
}
