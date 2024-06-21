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

### Before all:

* *If your OS is **Windows 11** follow this step for [command prompt configuration](FIleReadMe/WIN11.md)*
* *If your OS is **Mac OS** or **Linux** follow this step for [command prompt configuration](FIleReadMe/MacOS.md)*
* *if you have any other **OS** you can skip **command prompt configuration***

### First Step:

Download :

* [*Codex Naturalis Client*]()
* [*Codex Naturalis Server*]() --> *needed only in the host machine*

### Second step:

* open the command prompt on the host machine
* Navigate through the directories using the appropriate commands for your command prompt (these may vary based on the
  operating system)
  <br>and position yourself in the file path where the ```CodexNaturalisServer.jar``` is located
* Run with this command the jar file -> ```java -jar CodexNaturalisServer.jar```
* Complete the on-screen instructions to finalize the server configuration.

### Third step (Final step):

* open the command prompt on the clients machine

* Navigate through the directories using the appropriate commands for your command prompt (these may vary based on the
  operating system).
  <br>and position yourself in the file path where the ```CodexNaturalisClient.jar``` is located.


* Run with this command the jar file -> ```java -jar CodexNaturalisClient.jar```
* Complete the on-screen instructions to start a game -> Ensure to enter the IP address used by the server when you have
  to choose the IP to connect to.

**IMPORTANT -->** : Once these steps are completed, you can play without any issues. However,<br> if your goal is to
play as hosts not connected to the same network, you will need to use a VPN to create a virtual network (such
as [Hamachi](https://vpn.net/)).

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

Codex Naturalis is the intellectual property of Cranio Creations. All content, including but not limited to, game
mechanics, design, artwork, and assets, is protected under copyright law.
Unauthorized reproduction, distribution, or modification of this material is prohibited
without prior written consent from Cranio Creations. This project is released under the  **[MIT License](LICENSE.txt)**.
Please refer to the LICENSE file for detailed information regarding the terms and conditions of use, reproduction, and
distribution of the game.


[license-image]: https://img.shields.io/badge/License-MIT-blue.svg