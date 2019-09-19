package pl.maciejem.devicemanager.web.mapper;

import org.mapstruct.Mapper;
import pl.maciejem.devicemanager.persitence.model.Device;
import pl.maciejem.devicemanager.persitence.model.DeviceStatus;
import pl.maciejem.devicemanager.web.dto.DeviceDTO;
import pl.maciejem.devicemanager.web.dto.DeviceStatusDTO;

import java.util.List;

@Mapper
public interface DeviceMapper {
    DeviceDTO toDeviceDTO(Device device);

    Device toDevice(DeviceDTO deviceDTO);

    List<DeviceDTO> toDeviceDTOs(List<Device> products);

    DeviceStatus toDeviceStatus(DeviceStatusDTO deviceStatusDTO);
}