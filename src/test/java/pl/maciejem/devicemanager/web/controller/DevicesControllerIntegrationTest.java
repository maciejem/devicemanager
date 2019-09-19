package pl.maciejem.devicemanager.web.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import pl.maciejem.devicemanager.DeviceApplication;
import pl.maciejem.devicemanager.persitence.dao.DeviceRepository;
import pl.maciejem.devicemanager.persitence.model.Device;
import pl.maciejem.devicemanager.persitence.model.DeviceStatus;
import pl.maciejem.devicemanager.web.dto.DeviceDTO;
import pl.maciejem.devicemanager.web.dto.DeviceStatusDTO;
import pl.maciejem.devicemanager.web.dto.DeviceStatusUpdateDTO;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = DeviceApplication.class)
public class DevicesControllerIntegrationTest {

    private static final String APP_HOST = "/devices";

    @Autowired
    private TestRestTemplate restTemplate;
    private RestTemplate patchRestTemplate;

    @MockBean
    private DeviceRepository deviceRepository;

    private UUID uuid1;
    private UUID uuid2;

    @Before
    public void setup() {
        this.patchRestTemplate = restTemplate.getRestTemplate();
        this.patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        uuid1 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        uuid2 = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");

    }

    @Test
    public void saveTest() throws JSONException {

        //given
        DeviceDTO device = new DeviceDTO("phone", "XXX");
        Device savedDevice = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        String expected = "{\"id\":\"" + uuid1 + "\",\"name\":\"phone\",\"deviceStatus\":\"NEW\"}";
        when(deviceRepository.save(any(Device.class))).thenReturn(savedDevice);

        //when
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(APP_HOST, device, String.class);

        //then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
    }

    @Test
    public void saveWithoutDeviceTest() {

        //given
        String expected = "Required request body is missing";

        //when
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(APP_HOST, null, String.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertThat(responseEntity.getBody()).contains(expected);
    }

    @Test
    public void saveTestWithoutSecretKey() throws JSONException {

        //given
        DeviceDTO device = new DeviceDTO("phone", null);
        Device savedDevice = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        String expected = "{\"secretKey\":\"must not be null\"}";
        when(deviceRepository.save(any(Device.class))).thenReturn(savedDevice);

        //when
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(APP_HOST, device, String.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
    }

    @Test
    public void findOneDeviceNotFoundTest() {

        //given
        String expected = "Device id not found : " + uuid1;
        when(deviceRepository.getDeviceById(any(UUID.class))).thenReturn(null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("secretKey", "XXX");
        HttpEntity entity = new HttpEntity(headers);
        //when
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(APP_HOST + "/" + uuid1, HttpMethod.GET, entity, String.class);

        //then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void findOneDeviceIdNotUUIDTest() {

        //given
        String expected = "id should be of type java.util.UUID";
        when(deviceRepository.getDeviceById(any(UUID.class))).thenReturn(null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("secretKey", "XXX");
        HttpEntity entity = new HttpEntity(headers);
        //when
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(APP_HOST + "/" + "23ds", HttpMethod.GET, entity, String.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(expected, responseEntity.getBody());
    }



    @Test
    public void saveTestWithoutName() throws JSONException {

        //given
        DeviceDTO device = new DeviceDTO(null, "XXX");
        Device savedDevice = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        String expected = "{\"name\":\"must not be null\"}";
        when(deviceRepository.save(any(Device.class))).thenReturn(savedDevice);

        //when
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(APP_HOST, device, String.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
    }

    @Test
    public void findOneTest() throws JSONException {

        //given
        Device device = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        String expected = "{\"id\":\"" + uuid1 + "\",\"name\":\"phone\",\"secretKey\":\"XXX\",\"deviceStatus\":\"NEW\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.set("secretKey", "XXX");
        HttpEntity entity = new HttpEntity(headers);
        when(deviceRepository.getDeviceById(any(UUID.class))).thenReturn(device);

        //when
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(APP_HOST + "/" + uuid1, HttpMethod.GET, entity, String.class);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
    }

    @Test
    public void findOneWrongSecretKeyTest() {

        //given
        Device device = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        String expected = "Wrong secret key";
        HttpHeaders headers = new HttpHeaders();
        headers.set("secretKey", "YYY");
        HttpEntity entity = new HttpEntity(headers);
        when(deviceRepository.getDeviceById(any(UUID.class))).thenReturn(device);

        //when
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(APP_HOST + "/" + uuid1, HttpMethod.GET, entity, String.class);

        //then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void findOneWithoutSecretKeyTest() {

        //given
        Device device = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        String expected = "Missing secret key";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        when(deviceRepository.getDeviceById(any(UUID.class))).thenReturn(device);

        //when
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(APP_HOST + "/" + uuid1, HttpMethod.GET, entity, String.class);

        //then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(expected, responseEntity.getBody());
    }


    @Test
    public void updateDeviceStatusTest() throws JSONException {

        //given
        Device device = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        Device updatedDevice = new Device(uuid1, "phone", "XXX", DeviceStatus.OK);

        DeviceStatusUpdateDTO deviceStatusUpdateDTO = new DeviceStatusUpdateDTO("XXX", DeviceStatusDTO.OK);

        String expected = "{\"id\":\"" + uuid1 + "\",\"name\":\"phone\",\"deviceStatus\":\"OK\"}";

        when(deviceRepository.updateDeviceStatus(any(DeviceStatus.class), any(UUID.class))).thenReturn(updatedDevice);
        when(deviceRepository.getDeviceById(any(UUID.class))).thenReturn(device);

        //when
        ResponseEntity<String> responseEntity = patchRestTemplate.exchange(APP_HOST + "/" + uuid1, HttpMethod.PATCH, new HttpEntity<>(deviceStatusUpdateDTO),
                String.class);
        //then

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
    }

    @Test
    public void updateDeviceStatusIdNotFoundTest() {

        //given
        Device updatedDevice = new Device(uuid1, "phone", "XXX", DeviceStatus.OK);
        DeviceStatusUpdateDTO deviceStatusUpdateDTO = new DeviceStatusUpdateDTO("XXX", DeviceStatusDTO.OK);

        String expected = "Device id not found : " + uuid1;

        when(deviceRepository.updateDeviceStatus(any(DeviceStatus.class), any(UUID.class))).thenReturn(updatedDevice);
        when(deviceRepository.getDeviceById(any(UUID.class))).thenReturn(null);

        //when
        ResponseEntity<String> responseEntity = patchRestTemplate.exchange(APP_HOST + "/" + uuid1, HttpMethod.PATCH, new HttpEntity<>(deviceStatusUpdateDTO),
                String.class);
        //then

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void updateDeviceStatusWithWrongSecretKeyTest() {

        //given
        Device device = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        Device updatedDevice = new Device(uuid1, "phone", "XXX", DeviceStatus.OK);

        DeviceStatusUpdateDTO deviceStatusUpdateDTO = new DeviceStatusUpdateDTO("YYY", DeviceStatusDTO.OK);

        String expected = "Wrong secret key";

        when(deviceRepository.updateDeviceStatus(any(DeviceStatus.class), any(UUID.class))).thenReturn(updatedDevice);
        when(deviceRepository.getDeviceById(any(UUID.class))).thenReturn(device);

        //when
        ResponseEntity<String> responseEntity = patchRestTemplate.exchange(APP_HOST + "/" + uuid1, HttpMethod.PATCH, new HttpEntity<>(deviceStatusUpdateDTO),
                String.class);
        //then

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void updateDeviceStatusWithoutSecretKeyTest() throws JSONException {

        //given
        Device device = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        Device updatedDevice = new Device(uuid1, "phone", "XXX", DeviceStatus.OK);

        DeviceStatusUpdateDTO deviceStatusUpdateDTO = new DeviceStatusUpdateDTO(null, DeviceStatusDTO.OK);

        String expected = "{\"secretKey\":\"must not be null\"}";

        when(deviceRepository.updateDeviceStatus(any(DeviceStatus.class), any(UUID.class))).thenReturn(updatedDevice);
        when(deviceRepository.getDeviceById(any(UUID.class))).thenReturn(device);

        //when
        ResponseEntity<String> responseEntity = patchRestTemplate.exchange(APP_HOST + "/" + uuid1, HttpMethod.PATCH, new HttpEntity<>(deviceStatusUpdateDTO),
                String.class);
        //then

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
    }

    @Test
    public void updateDeviceStatusWithoutDeviceStatusTest() throws JSONException {

        //given
        Device device = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        Device updatedDevice = new Device(uuid1, "phone", "XXX", DeviceStatus.OK);

        DeviceStatusUpdateDTO deviceStatusUpdateDTO = new DeviceStatusUpdateDTO("XXX", null);

        String expected = "{\"deviceStatus\":\"must not be null\"}";

        when(deviceRepository.updateDeviceStatus(any(DeviceStatus.class), any(UUID.class))).thenReturn(updatedDevice);
        when(deviceRepository.getDeviceById(any(UUID.class))).thenReturn(device);

        //when
        ResponseEntity<String> responseEntity = patchRestTemplate.exchange(APP_HOST + "/" + uuid1, HttpMethod.PATCH, new HttpEntity<>(deviceStatusUpdateDTO),
                String.class);
        //then

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
    }

    @Test
    public void updateDeviceStatusToStaleStatusTest() {

        //given
        Device device = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        Device updatedDevice = new Device(uuid1, "phone", "XXX", DeviceStatus.OK);

        DeviceStatusUpdateDTO deviceStatusUpdateDTO = new DeviceStatusUpdateDTO("XXX", DeviceStatusDTO.STALE);

        String expected = "Update device status to: STALE is not allowed. Allowed is update to statuses: OK, UNHEALTHY";

        when(deviceRepository.updateDeviceStatus(any(DeviceStatus.class), any(UUID.class))).thenReturn(updatedDevice);
        when(deviceRepository.getDeviceById(any(UUID.class))).thenReturn(device);

        //when
        ResponseEntity<String> responseEntity = patchRestTemplate.exchange(APP_HOST + "/" + uuid1, HttpMethod.PATCH, new HttpEntity<>(deviceStatusUpdateDTO),
                String.class);
        //then

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void updateDeviceStatusToNewStatusTest() {

        //given
        Device device = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        Device updatedDevice = new Device(uuid1, "phone", "XXX", DeviceStatus.OK);

        DeviceStatusUpdateDTO deviceStatusUpdateDTO = new DeviceStatusUpdateDTO("XXX", DeviceStatusDTO.NEW);

        String expected = "Update device status to: NEW is not allowed. Allowed is update to statuses: OK, UNHEALTHY";

        when(deviceRepository.updateDeviceStatus(any(DeviceStatus.class), any(UUID.class))).thenReturn(updatedDevice);
        when(deviceRepository.getDeviceById(any(UUID.class))).thenReturn(device);

        //when
        ResponseEntity<String> responseEntity = patchRestTemplate.exchange(APP_HOST + "/" + uuid1, HttpMethod.PATCH, new HttpEntity<>(deviceStatusUpdateDTO),
                String.class);
        //then

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void updateDeviceStatusWithNoSupportedStatusTest() throws JSONException {

        //given
        Device device = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        Device updatedDevice = new Device(uuid1, "phone", "XXX", DeviceStatus.OK);

        JSONObject deviceStatusUpdate = new JSONObject();
        deviceStatusUpdate.put("secretKey", "XXX");
        deviceStatusUpdate.put("deviceStatus", "xxx");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String expected = "Cannot deserialize value of type";
        HttpEntity<String> request = new HttpEntity<>(deviceStatusUpdate.toString(), headers);

        when(deviceRepository.updateDeviceStatus(any(DeviceStatus.class), any(UUID.class))).thenReturn(updatedDevice);
        when(deviceRepository.getDeviceById(any(UUID.class))).thenReturn(device);

        //when
        ResponseEntity<String> responseEntity = patchRestTemplate.exchange(APP_HOST + "/" + uuid1, HttpMethod.PATCH, request,
                String.class);
        //then

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertThat(responseEntity.getBody()).contains(expected);
    }

    @Test
    public void findAllTest() throws JSONException {

        //given
        Device device = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        Device device2 = new Device(uuid2, "tablet", "YYY", DeviceStatus.OK);

        String expected = "[{\"id\":\"" + uuid1 + "\",\"name\":\"phone\",\"deviceStatus\":\"NEW\"}," +
                "{\"id\":\"" + uuid2 + "\",\"name\":\"tablet\",\"deviceStatus\":\"OK\"}]";
        String notExpectedWithPasswords = "[{\"id\":\"" + uuid1 + "\",\"name\":\"phone\",\"secretKey\":\"XXX\",\"deviceStatus\":\"NEW\"}," +
                "{\"id\":\"" + uuid2 + "\",\"name\":\"tablet\",\"secretKey\":\"YYY\",\"deviceStatus\":\"OK\"}]";
        when(deviceRepository.getDevicesFiltered(any())).thenReturn(Arrays.asList(device, device2));

        //when
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(APP_HOST, String.class);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
        JSONAssert.assertNotEquals(notExpectedWithPasswords, responseEntity.getBody(), false);

    }

    @Test
    public void findFilteredByStatusTest() throws JSONException {

        //given
        Device device = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        Device device2 = new Device(uuid2, "tablet", "YYY", DeviceStatus.NEW);

        String expected = "[{\"id\":\"" + uuid1 + "\",\"name\":\"phone\",\"deviceStatus\":\"NEW\"}," +
                "{\"id\":\"" + uuid2 + "\",\"name\":\"tablet\",\"deviceStatus\":\"NEW\"}]";

        when(deviceRepository.getDevicesFiltered(any())).thenReturn(Arrays.asList(device, device2));

        //when
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(APP_HOST + "?status=NEW", String.class);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
    }


    @Test
    public void findFilteredByUnknownParameterTest() throws JSONException {

        //given
        Device device = new Device(uuid1, "phone", "XXX", DeviceStatus.NEW);
        Device device2 = new Device(uuid2, "tablet", "YYY", DeviceStatus.NEW);

        String expected = "[{\"id\":\"" + uuid1 + "\",\"name\":\"phone\",\"deviceStatus\":\"NEW\"}," +
                "{\"id\":\"" + uuid2 + "\",\"name\":\"tablet\",\"deviceStatus\":\"NEW\"}]";

        when(deviceRepository.getDevicesFiltered(any())).thenReturn(Arrays.asList(device, device2));

        //when
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(APP_HOST + "?xxx=xxx", String.class);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
    }
}