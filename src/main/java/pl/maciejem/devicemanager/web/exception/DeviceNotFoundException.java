package pl.maciejem.devicemanager.web.exception;

import java.util.UUID;

public class DeviceNotFoundException extends RuntimeException {

    public DeviceNotFoundException(UUID id) {
        super("Device id not found : " + id);
    }
}
