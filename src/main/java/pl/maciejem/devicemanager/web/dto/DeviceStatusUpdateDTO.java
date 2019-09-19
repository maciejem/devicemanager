package pl.maciejem.devicemanager.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceStatusUpdateDTO {

    @NotNull
    String secretKey;

    @NotNull
    DeviceStatusDTO deviceStatus;
}
