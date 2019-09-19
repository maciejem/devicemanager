package pl.maciejem.devicemanager.web.exception;

import pl.maciejem.devicemanager.persitence.model.DeviceStatus;

public class NotAllowedStatusToUpdateDeviceStatus extends RuntimeException {

    public NotAllowedStatusToUpdateDeviceStatus(DeviceStatus deviceStatus) {
        super("Update device status to: "+ deviceStatus+ " is not allowed. Allowed is update to statuses: OK, UNHEALTHY");
    }

}
