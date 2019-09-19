package pl.maciejem.devicemanager.persitence.dao;

import pl.maciejem.devicemanager.persitence.model.Device;
import pl.maciejem.devicemanager.persitence.model.DeviceStatus;
import pl.maciejem.devicemanager.web.dto.SearchParams;

import java.util.List;
import java.util.UUID;

public interface DeviceRepository {

    Device save(Device device);

    Device updateDeviceStatus(DeviceStatus deviceStatus, UUID id);

    List<Device> getDevicesFiltered(SearchParams searchParams);

    List<Device> getDevices();

    Device getDeviceById(UUID id);
}
