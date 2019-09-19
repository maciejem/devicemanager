package pl.maciejem.devicemanager.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.maciejem.devicemanager.persitence.dao.DeviceRepository;
import pl.maciejem.devicemanager.persitence.model.Device;
import pl.maciejem.devicemanager.persitence.model.DeviceStatus;
import pl.maciejem.devicemanager.service.statusexpiration.StatusExpirationService;
import pl.maciejem.devicemanager.web.dto.SearchParams;
import pl.maciejem.devicemanager.web.exception.DeviceNotFoundException;
import pl.maciejem.devicemanager.web.exception.NotAllowedStatusToUpdateDeviceStatus;
import pl.maciejem.devicemanager.web.exception.WrongSecretKeyException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class DeviceService {

    private DeviceRepository deviceRepository;
    private StatusExpirationService statusExpirationService;

    public Device save(Device device) {
        log.info("Saving device (device: {}", device);
        return deviceRepository.save(device);
    }

    public List<Device> getDevices(SearchParams searchParams) {
        log.info("Getting devices");
        return deviceRepository.getDevicesFiltered(searchParams);
    }

    public Device getDeviceById(UUID id, String secretKey) throws DeviceNotFoundException {
        log.info("Getting device by id (deviceId: {}", id);
        Device device = deviceRepository.getDeviceById(id);
        if (device == null) {
            throw new DeviceNotFoundException(id);
        }
        validateSecretKey(secretKey, device.getSecretKey());
        return device;
    }

    public Device updateDeviceStatus(DeviceStatus deviceStatus, String secretKey, UUID id) {
        log.info("Updating device status (deviceId: {}, deviceStatus: {}", id, deviceStatus);
        Device device = deviceRepository.getDeviceById(id);
        if (device == null) {
            throw new DeviceNotFoundException(id);
        }
        validateSecretKey(secretKey, device.getSecretKey());
        validateStatusToUpdateDeviceStatus(deviceStatus);
        device = deviceRepository.updateDeviceStatus(deviceStatus, id);
        statusExpirationService.updateStatusExpirationDelayQueue(id, deviceStatus);
        return device;
    }

    private void validateSecretKey(String userSecretKey, String deviceSecretKey) {
        if (!userSecretKey.equals(deviceSecretKey)) {
            throw new WrongSecretKeyException();
        }
    }

    private void validateStatusToUpdateDeviceStatus(DeviceStatus deviceStatus) {
        if (!Arrays.asList(DeviceStatus.OK, DeviceStatus.UNHEALTHY).contains(deviceStatus) ) {
            throw new NotAllowedStatusToUpdateDeviceStatus(deviceStatus);
        }
    }
}