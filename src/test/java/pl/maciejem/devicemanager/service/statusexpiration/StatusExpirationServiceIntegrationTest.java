package pl.maciejem.devicemanager.service.statusexpiration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import pl.maciejem.devicemanager.DeviceApplication;
import pl.maciejem.devicemanager.persitence.dao.DeviceRepository;
import pl.maciejem.devicemanager.persitence.model.DeviceStatus;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DeviceApplication.class)
public class StatusExpirationServiceIntegrationTest {

    @Autowired
    StatusExpirationService statusExpirationService;
    @MockBean
    private DeviceRepository deviceRepository;

    @Test
    public void beforeStatusExpirationTest() throws InterruptedException {
        //given
        UUID uuid1 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        UUID uuid2 = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");

        statusExpirationService.updateStatusExpirationDelayQueue(uuid1, DeviceStatus.OK);
        statusExpirationService.updateStatusExpirationDelayQueue(uuid2, DeviceStatus.OK);
        //when
        Thread.sleep(1000);
        //then
        verify(deviceRepository, times(0)).updateDeviceStatus(DeviceStatus.STALE, uuid1);
        verify(deviceRepository, times(0)).updateDeviceStatus(DeviceStatus.STALE, uuid2);

    }

    @Test
    public void afterStatusExpirationTest() throws InterruptedException {
        //given
        UUID uuid1 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00e");
        UUID uuid2 = UUID.fromString("123e4567-e89b-12d3-a456-556642440001");

        statusExpirationService.updateStatusExpirationDelayQueue(uuid1, DeviceStatus.OK);
        statusExpirationService.updateStatusExpirationDelayQueue(uuid2, DeviceStatus.OK);
        //when
        Thread.sleep(3000);
        //then
        verify(deviceRepository, times(1)).updateDeviceStatus(DeviceStatus.STALE, uuid1);
        verify(deviceRepository, times(1)).updateDeviceStatus(DeviceStatus.STALE, uuid2);
    }

    @Test
    public void removeFromStatusExpirationDelayQueueTest() throws InterruptedException {
        //given
        UUID uuid1 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00f");
        UUID uuid2 = UUID.fromString("123e4567-e89b-12d3-a456-556642440002");
        statusExpirationService.updateStatusExpirationDelayQueue(uuid1, DeviceStatus.OK);
        statusExpirationService.updateStatusExpirationDelayQueue(uuid2, DeviceStatus.OK);
        //when
        Thread.sleep(1000);
        statusExpirationService.updateStatusExpirationDelayQueue(uuid1, DeviceStatus.UNHEALTHY);
        statusExpirationService.updateStatusExpirationDelayQueue(uuid2, DeviceStatus.UNHEALTHY);
        Thread.sleep(2000);
        //then
        verify(deviceRepository, times(0)).updateDeviceStatus(DeviceStatus.STALE, uuid1);
        verify(deviceRepository, times(0)).updateDeviceStatus(DeviceStatus.STALE, uuid2);
    }
}
