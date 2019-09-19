package pl.maciejem.devicemanager.service.statusexpiration;

import lombok.Data;

import java.util.UUID;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * StatusExpirationDelayObject stores information about the time
 * when the status of the Device is expired and deviceId of this Device.
 * It is used by delay queue. When the expiration time has passed the object is taken from the queue.
 */
@Data
public class StatusExpirationDelayObject implements Delayed {
    private UUID deviceId;
    private long expirationTime;

    public StatusExpirationDelayObject(UUID deviceId, long delay) {
        this.deviceId = deviceId;
        this.expirationTime = System.currentTimeMillis() + delay;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = expirationTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.expirationTime, ((StatusExpirationDelayObject) o).expirationTime);
    }

    @Override
    public String toString() {
        return "{" +
                "deviceId='" + deviceId + '\'' +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
