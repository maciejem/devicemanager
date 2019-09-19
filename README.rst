Device Manager service - recruitment task
==============================================

Create a simple REST (or GraphQL) service (no user interface required) for 
tracking status of devices.

Use cases
---------

The service should support following use cases:

Device registration
    Each device has an associated human-friendly name, secret key and status.
    Client provides name and secret key, while the status is initially set to
    NEW. Neither name nor secret key are assumed to be unique.

Retrieval of device details
    Returns name, secret key and current status of a single device.

Listing devices
    Includes names and status of devices, but not secret keys

Device status update
    Client can set a single device status to OK or UNHEALTHY, but he must provide 
    secret key associated with the device as part of the request. Otherwise 
    update attempt is rejected.

Automatic device status expiration
    If some device status is OK, but it has not been changed by API call during
    last 5 minutes, than the device status should be considered STALE.

Listing devices by status
    Same as 'Listing devices' but only devices with required status are returned.

Note:

- Please follow REST/GraphQL API design best practices.

- No authentication/authorization is required for making API calls.

- Plain HTTP is enough.

- Restarting application should cause loss of data, do not use external
  persistence layer (database, in-memory database, Hibernate etc).
  
Technical requirements
----------------------

- Service should be implemented in Java, and it should run on Linux.

- It should correctly handle concurrent access by multiple clients.

- Code quality and good application architecture matters. Including 
  automated tests at appropriate layers is expected.

- Either Maven or Gradle should be used as build system.

- Scripts for compiling and running the service should be included, as well as
  short instructions for running the service.

- Any free libraries/frameworks that Maven/Gradle can download from public
  repositories is allowed.

- Building and running the system should not require external system
  dependencies apart from basic ones like Java, Maven/Gradle or Docker.
  
- Please keep the solution simple, don't include dependencies that are not
  related to solving requested use-cases.

Delivery
--------

The code should be commited to a new branch in this repository, and merge request
to 'master' branch should be created when solution is ready.

Quality is more important than speed, so sending good solution after 2 weeks is 
better than sending half-baked solution without tests after 2 days. However, 
please include the information of the total amount of time spent on the task.

If you have questions about use-cases, technical requirements or delivery,
please just ask.

Confidentiality
---------------

Please don't share the details of the task with other potential candidates ;)
