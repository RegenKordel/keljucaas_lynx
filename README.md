# KeljuCaaS

KeljuCaaS is a service in the dependency engine of OpenReq infrastructure that primarily focuses on the contexts, which already contain a large number of existing and dependent requirements, such as large distributed open source projects or large systems engineering projects. For example, the Qt Company has about one hundred-thousand (100,000) issues in its Jira. The dependency engine focuses on the entire body of requirements as an interdepedent "requirements model".

This service was created as a result of the OpenReq project funded by the European Union Horizon 2020 Research and Innovation programme under grant agreement No 732463.

# Technical Description

KeljuCaaS is a generic inference engine for product models. For requirements models, KeljuCaaS is used through the Mulperi service, which  composes a requirements model from  requirements in the OpenReq JSON format and transforms the requirements model to more generic Murmeli product model. For further details, see the [Mulperi]((https://github.com/OpenReqEU/Mulperi) service. That is, in the case of requirements and OpenReq, KeljuCaaS should not be accessed directly.

## The following technologies are used:
- Java
- Spring Boot
- Maven
- GSON
- Choco
	
## Public APIs

The API is documented by using Swagger2: http://217.172.12.199:9205/swagger-ui.html

## How to Install

Run the compiled jar file, e.g., `java -jar KeljuCaaS-1.5.jar`.

KeljuCaaS uses port 9205 that needs to be open to in order that the endpoints and Swagger page can be accessed. 


## How to Use This Microservice

See [Mulperi]((https://github.com/OpenReqEU/Mulperi).  The swagger page of KeljuCaaS describes all endpoints that can be used for testing purposes. 


# How to Contribute
See the OpenReq Contribution Guidelines [here](https://github.com/OpenReqEU/OpenReq/blob/master/CONTRIBUTING.md).

# License

Free use of this software is granted under the terms of the [EPL version 2 (EPL2.0)](https://www.eclipse.org/legal/epl-2.0/).

