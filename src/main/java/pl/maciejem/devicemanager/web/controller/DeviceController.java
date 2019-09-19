package pl.maciejem.devicemanager.web.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.maciejem.devicemanager.service.DeviceService;
import pl.maciejem.devicemanager.web.dto.*;
import pl.maciejem.devicemanager.web.exception.WrongSecretKeyException;
import pl.maciejem.devicemanager.web.mapper.DeviceMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/devices")
@AllArgsConstructor
public class DeviceController {

    private final DeviceMapper deviceMapper;
    private DeviceService deviceService;

    @JsonView(Views.Basic.class)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceDTO save(
            @Valid @RequestBody DeviceDTO device) {
        return deviceMapper.toDeviceDTO(deviceService.save(deviceMapper.toDevice(device)));
    }

    @PatchMapping("/{id}")
    public DeviceDTO updateDeviceStatus(@PathVariable("id") UUID id,
                                        @Valid @RequestBody DeviceStatusUpdateDTO deviceStatusUpdateDTO) throws WrongSecretKeyException {
        return deviceMapper.toDeviceDTO(deviceService.updateDeviceStatus(deviceMapper.toDeviceStatus(deviceStatusUpdateDTO.getDeviceStatus()), deviceStatusUpdateDTO.getSecretKey(), id));
    }

    @JsonView(Views.Basic.class)
    @GetMapping("")
    public List<DeviceDTO> find(SearchParams searchParams) {
        return deviceMapper.toDeviceDTOs(deviceService.getDevices(searchParams));
    }

    @JsonView(Views.Details.class)
    @GetMapping("/{id}")
    public DeviceDTO findOne(@PathVariable("id") UUID id,@RequestHeader(value = "secretKey") String secretKey) {
        return deviceMapper.toDeviceDTO(deviceService.getDeviceById(id, secretKey));
    }
}