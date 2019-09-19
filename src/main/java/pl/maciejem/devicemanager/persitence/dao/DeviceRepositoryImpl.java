package pl.maciejem.devicemanager.persitence.dao;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import pl.maciejem.devicemanager.persitence.model.Device;
import pl.maciejem.devicemanager.persitence.model.DeviceStatus;
import pl.maciejem.devicemanager.web.dto.SearchParams;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRepositoryImpl implements DeviceRepository {

    private ConcurrentHashMap<UUID, Device> devices = new ConcurrentHashMap<>();

    @Override
    public Device save(Device device) {
        UUID id = UUID.randomUUID();
        device.setId(id);
        device.setDeviceStatus(DeviceStatus.NEW);
        devices.put(id, device);
        return device;
    }

    @Override
    public Device updateDeviceStatus(DeviceStatus deviceStatus, UUID id) {
        Device device = null;
        if (devices.containsKey(id)) {
            device = getDeviceById(id);
            device.setDeviceStatus(deviceStatus);
        }
        return device;
    }

    @Override
    public List<Device> getDevicesFiltered(SearchParams searchParams) {
        List<Predicate<Device>> predicates = new ArrayList<>();
        if (searchParams != null && searchParams.getStatus() != null) {
            Predicate<Device> statusPredicate = (Device device) -> device.getDeviceStatus().name().equalsIgnoreCase(searchParams.getStatus());
            predicates.add(statusPredicate);
        }
        if (predicates.size() > 0) {
            return devices.values().stream().filter(p -> predicates.stream().allMatch(f -> f.test(p)))
                    .collect(Collectors.toList());
        } else {
            return getDevices();
        }
    }

    @Override
    public List<Device> getDevices() {
        return new ArrayList<>(devices.values());
    }

    @Override
    public Device getDeviceById(UUID id) {
        return devices.get(id);
    }
}
