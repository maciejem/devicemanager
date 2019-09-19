package pl.maciejem.devicemanager.persitence.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Device {

    UUID id;

    String name;

    String secretKey;

    DeviceStatus deviceStatus;

    public Device(String name, String secretKey) {
        this.name = name;
        this.secretKey = secretKey;
    }

    public Device(String name, String secretKey, DeviceStatus status) {
        this.name = name;
        this.secretKey = secretKey;
        this.deviceStatus = status;
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
