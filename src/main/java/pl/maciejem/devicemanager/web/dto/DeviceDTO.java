package pl.maciejem.devicemanager.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeviceDTO {

    @JsonView(Views.Basic.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    UUID id;

    @JsonView(Views.Basic.class)
    @NotNull
    String name;

    @JsonView(Views.Details.class)
    @NotNull
    String secretKey;

    @JsonView(Views.Basic.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    DeviceStatusDTO deviceStatus;

    public DeviceDTO(String name, String secretKey) {
        this.name = name;
        this.secretKey = secretKey;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", deviceStatus=" + deviceStatus +
                '}';
    }
}
