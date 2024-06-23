# Software Engineering Project 2023/2024

## Codex Naturalis     [![License: MIT][license-image]](LICENSE.txt)

<img alt="Codex Naturalis game logo" src="https://m.media-amazon.com/images/I/814qEh0JKdS._AC_SX679_.jpg" width=192px height=192 px align="right" />

Official game site: [**CranioCreations**](https://www.craniocreations.it/prodotto/codex-naturalis).

[**Game rules** (EN)](FIleReadMe/CODEX_Rulebook_EN.pdf)
[**Game rules** (IT)](FIleReadMe/CODEX_Rulebook_IT.pdf)

Software Engineering group project following the conclusion of the **Software Engineering** course in the Computer
Science and
Engineering Bachelor's Degree at Politecnico di Milano (2023/2024).

Teacher: Cugola Gianpaolo

## Project specification

The project consists of a Java version of the board game **Codex Naturalis**, made by Cranio Creations.

[**Project requirements**](FIleReadMe/requirements.pdf)

**The final version of the game includes:**

* high-level, general UML diagram;
* detailed UML diagram, generated from the code by automated tools;
* java documentations
* working game implementation;
* source code of the implementation;
* source code of tests.

### List of functionalities:

| Functionality                    | Implemented |
|:---------------------------------|:-----------:|
| **Basic rules**                  |      ✅      |
| **Complete rules**               |      ✅      |
| **Socket**                       |      ✅      |
| **RMI**                          |      ✅      |
| **TUI**                          |      ✅      |
| **GUI**                          |      ✅      |
| **Multiple games**               |      ✅      |
| **Resilience to disconnections** |      ✅      |
| **Chat**                         |      ✅      |
| **Persistence**                  |      ⛔      |

## Download to play guide

### TUI/CLI Setup:

* *If your OS is **Windows 11** follow this step for [command prompt configuration](FIleReadMe/WIN11.md)*
* *If your OS is **Mac OS** or **Linux** follow this step for [command prompt configuration](FIleReadMe/MacOS.md)*
* *We currently do not support other OSs*

### Download links:

Click on the links below to be redirected to the download page, where you can download the self-contained all-in-one
.jar files.
Please note that you will still need to download the correct version of the JRE to run this program (which is written
with JDK 21,
and thus needs a JRE of XXXXXX or higher to be run);

* [*Codex Naturalis Client*](deliverables/CodexNaturalisClient.jar)
* [*Codex Naturalis Server*](deliverables/CodexNaturalisServer.jar) --> *needed only in the machine which is going to
  host it*

### Server configuration:

* Open the command prompt on the host machine;
* Navigate through the directories using the appropriate commands for your command prompt (these may vary based on the
  operating system) and position yourself in the file path where the ```CodexNaturalisServer.jar``` is located;
* Run the .jar file for the server with the following command -> ```java -jar CodexNaturalisServer.jar```;
* Follow the instructions that appear on the screen to finalize the server configuration.

### Clients' configuration:

* Open the command prompt on the clients' machines;
* Navigate through the directories using the appropriate commands for your command prompt (these may vary based on the
  operating system) and position yourself in the file path where the file ```CodexNaturalisClient.jar``` is located;
* Run the .jar file for the server with the following command -> ```java -jar CodexNaturalisClient.jar```
* Follow the instructions that appear on the screen to start a game (make sure to enter the correct IP address used by
  the server when you have to choose the IP to connect to).

**IMPORTANT -->** : If you aim to play with different devices over the net, you can achieve this by using a third-party
software such as [Hamachi](https://vpn.net/), ZeroTier, Wireguard or properly set forward rules on the router where the
server is located. In case you want to play with RMI over the Internet, remember to launch the jar with the command
```java -Djava.rmi.server.hostname=[YOUR_PUBLIC_IP_ADDRESS] -jar CodexNaturalisClient.jar```

## Software used:

* [**Intellij IDEA Ultimate** (Educational license)](https://www.jetbrains.com/idea/) - main IDE for development
* [**LucidChart** (EDU license)](https://www.lucidchart.com/pages/it) - online tool for composing UML diagrams

### External libraries:

* [**Gson**](https://github.com/google/gson) - for (de)serialization of JSON resources
* [**Jansi**](https://github.com/fusesource/jansi) - for TUI rendering

**Authors:**

- Piantoni Riccardo
- Rossi Matteo
- Pinto Federico
- Sacramone Jacopo

### Copyright and License:

Codex Naturalis is an intellectual property of Cranio Creations. All content, including but not limited to, game
mechanics, design, artwork, and assets, is protected under copyright law.
Unauthorized reproduction, distribution, or modification of this material is prohibited
without prior written consent from Cranio Creations. This project is released under the  **[MIT License](LICENSE.txt)**.
Please refer to the LICENSE file for detailed information regarding the terms and conditions of use, reproduction, and
distribution of the game.


[license-image]: https://img.shields.io/badge/License-MIT-blue.svg