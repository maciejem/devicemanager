package pl.maciejem.devicemanager.persistence.dao;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import pl.maciejem.devicemanager.persitence.dao.DeviceRepository;
import pl.maciejem.devicemanager.persitence.dao.DeviceRepositoryImpl;
import pl.maciejem.devicemanager.persitence.model.Device;
import pl.maciejem.devicemanager.persitence.model.DeviceStatus;
import pl.maciejem.devicemanager.web.dto.SearchParams;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@RunWith(MockitoJUnitRunner.class)
public class DeviceRepositoryTest {

    private DeviceRepository deviceRepository = new DeviceRepositoryImpl();

    @Test
    public void saveTest() {
        //given
        Device device1 = new Device("Mobile", "XXX");
        Device device2 = new Device("Laptop", "YYY");
        //when
        Device saveDevice1 = deviceRepository.save(device1);
        deviceRepository.save(device2);
        //then
        Assertions.assertThat(saveDevice1.getId()).isNotNull();
        Assertions.assertThat(device1.getName()).isEqualTo(saveDevice1.getName());
        Assertions.assertThat(device1.getDeviceStatus()).isEqualTo(DeviceStatus.NEW);

        List<Device> devices = deviceRepository.getDevices();
        Assertions.assertThat(devices).isNotNull();
        Assertions.assertThat(devices).hasSize(2);
    }

    @Test
    public void saveWithStatusTest() {
        //given
        Device device1 = new Device("Mobile", "XXX", DeviceStatus.OK);
        Device device2 = new Device("Laptop", "YYY", DeviceStatus.OK);
        //when
        Device saveDevice1 = deviceRepository.save(device1);
        deviceRepository.save(device2);
        //then
        Assertions.assertThat(saveDevice1.getId()).isNotNull();
        Assertions.assertThat(device1.getName()).isEqualTo(saveDevice1.getName());
        Assertions.assertThat(device1.getDeviceStatus()).isEqualTo(DeviceStatus.NEW);

        List<Device> devices = deviceRepository.getDevices();
        Assertions.assertThat(devices).isNotNull();
        Assertions.assertThat(devices).hasSize(2);
    }

    @Test
    public void getDeviceByIdTest() {
        //given
        Device device = new Device("Mobile", "XXX");
        deviceRepository.save(device);
        UUID deviceId = device.getId();
        //when
        Device deviceById = deviceRepository.getDeviceById(deviceId);
        //then
        Assertions.assertThat(deviceById).isNotNull();
        Assertions.assertThat(deviceById.getId()).isEqualTo(device.getId());
        Assertions.assertThat(deviceById.getName()).isEqualTo(device.getName());
        Assertions.assertThat(deviceById.getSecretKey()).isEqualTo(device.getSecretKey());
        Assertions.assertThat(deviceById.getDeviceStatus()).isEqualTo(device.getDeviceStatus());

    }

    @Test
    public void getDeviceByIdNotFoundTest() {
        //given
        Device device = new Device("Mobile", "XXX");
        deviceRepository.save(device);
        UUID notFoundDeviceId = UUID.randomUUID();
        //when
        Device deviceById = deviceRepository.getDeviceById(notFoundDeviceId);
        //then
        Assertions.assertThat(deviceById).isNull();
    }

    @Test
    public void getDevicesTest() {
        //given
        Device device1 = new Device("Mobile", "XXX");
        Device device2 = new Device("Laptop", "YYY");

        deviceRepository.save(device1);
        deviceRepository.save(device2);
        //when
        List<Device> devices = deviceRepository.getDevices();
        //then
        Assertions.assertThat(devices).isNotNull();
        Assertions.assertThat(devices).hasSize(2);

        Device firstDeviceFromGetDevices = devices.get(0);
        Assertions.assertThat(firstDeviceFromGetDevices).isNotNull();

        Device deviceThatWasAddedAndIsFirst = Arrays.asList(device1, device2).stream().filter(d -> d.getId().equals(firstDeviceFromGetDevices.getId())).findFirst().orElse(null);
        Assertions.assertThat(deviceThatWasAddedAndIsFirst).isNotNull();

        Assertions.assertThat(deviceThatWasAddedAndIsFirst.getId()).isEqualTo(firstDeviceFromGetDevices.getId());
        Assertions.assertThat(deviceThatWasAddedAndIsFirst.getName()).isEqualTo(firstDeviceFromGetDevices.getName());
        Assertions.assertThat(deviceThatWasAddedAndIsFirst.getSecretKey()).isEqualTo(firstDeviceFromGetDevices.getSecretKey());
        Assertions.assertThat(deviceThatWasAddedAndIsFirst.getDeviceStatus()).isEqualTo(firstDeviceFromGetDevices.getDeviceStatus());

    }

    @Test
    public void updateDeviceStatusTest() {
        //given
        Device device = new Device("Mobile", "XXX");
        deviceRepository.save(device);
        UUID id = device.getId();
        //when
        deviceRepository.updateDeviceStatus(DeviceStatus.STALE, id);
        //then
        Device deviceUpdated = deviceRepository.getDeviceById(id);
        Assertions.assertThat(deviceUpdated).isNotNull();
        Assertions.assertThat(deviceUpdated.getDeviceStatus()).isEqualTo(DeviceStatus.STALE);
    }

    @Test
    public void updateDeviceStatusIdNotFoundTest() {
        //given
        Device device = new Device("Mobile", "XXX");
        deviceRepository.save(device);
        UUID idNotFound = UUID.randomUUID();
        //when
        Device deviceUpdated = deviceRepository.updateDeviceStatus(DeviceStatus.STALE, idNotFound);
        //then
        Assertions.assertThat(deviceUpdated).isNull();
    }

    @Test
    public void getDevicesFilteredByStatusTest() {
        //given
        Device device1 = new Device("Mobile", "XXX");
        Device device2 = new Device("Laptop", "YYY");
        Device device3 = new Device("Tablet", "ZZZ");

        //when
        deviceRepository.save(device1);
        deviceRepository.save(device2);
        deviceRepository.save(device3);

        deviceRepository.updateDeviceStatus(DeviceStatus.UNHEALTHY, device3.getId());
        SearchParams searchParams = new SearchParams();
        searchParams.setStatus("UNHEALTHY");
        List<Device> devices = deviceRepository.getDevicesFiltered(searchParams);
        //then
        Assertions.assertThat(devices).isNotNull();
        Assertions.assertThat(devices).hasSize(1);

        Device unhealthyDevice = devices.get(0);

        Assertions.assertThat(unhealthyDevice.getId()).isEqualTo(device3.getId());
        Assertions.assertThat(unhealthyDevice.getName()).isEqualTo(device3.getName());
        Assertions.assertThat(unhealthyDevice.getSecretKey()).isEqualTo(device3.getSecretKey());
        Assertions.assertThat(unhealthyDevice.getDeviceStatus()).isEqualTo(device3.getDeviceStatus());
    }

    @Test
    public void getDevicesFilteredByUnknownStatusTest() {
        //given
        Device device1 = new Device("Mobile", "XXX");
        Device device2 = new Device("Laptop", "YYY");
        Device device3 = new Device("Tablet", "ZZZ");

        //when
        deviceRepository.save(device1);
        deviceRepository.save(device2);
        deviceRepository.save(device3);

        deviceRepository.updateDeviceStatus(DeviceStatus.UNHEALTHY, device3.getId());
        SearchParams searchParams = new SearchParams();
        searchParams.setStatus("xxx");
        List<Device> devices = deviceRepository.getDevicesFiltered(searchParams);
        //then
        Assertions.assertThat(devices).isNotNull();
        Assertions.assertThat(devices).hasSize(0);
    }

    @Test
    public void getDevicesByNullSearchParamsTest() {
        //given
        Device device1 = new Device("Mobile", "XXX");
        Device device2 = new Device("Laptop", "YYY");
        Device device3 = new Device("Tablet", "ZZZ");

        //when
        deviceRepository.save(device1);
        deviceRepository.save(device2);
        deviceRepository.save(device3);

        List<Device> devices = deviceRepository.getDevicesFiltered(null);
        //then
        Assertions.assertThat(devices).isNotNull();
        Assertions.assertThat(devices).hasSize(3);
    }

    @Test
    public void getDevicesByEmptySearchParamsTest() {
        //given
        Device device1 = new Device("Mobile", "XXX");
        Device device2 = new Device("Laptop", "YYY");
        Device device3 = new Device("Tablet", "ZZZ");

        //when
        deviceRepository.save(device1);
        deviceRepository.save(device2);
        deviceRepository.save(device3);

        SearchParams searchParams = new SearchParams();
        List<Device> devices = deviceRepository.getDevicesFiltered(searchParams);
        //then
        Assertions.assertThat(devices).isNotNull();
        Assertions.assertThat(devices).hasSize(3);
    }
}
