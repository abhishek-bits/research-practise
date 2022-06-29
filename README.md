# Research Practice

## Topic

Identifying Hardware Anchors in an IOT-CPS device to prevent code tampering.

## Guide

[Prof. Chittaranjan Hota](https://universe.bits-pilani.ac.in/hyderabad/chittaranjanhota/Profile)

## Referenced Research Paper

[Detecting Stealthy Sensor Attacks with Micro-distortion](https://arxiv.org/abs/2203.12249)

## Goal

To develop a secure IOT system that works on *Filtered Delta Mean Difference Algorithm*, as proposed in the referenced research paper.

## Code Base

- [Server](https://github.com/abhishek-bits/research-practise/blob/master/src/iot/Server.java) The heart of the application that initiates the database and allows sensor-sockets to connect to.
- [SensorHandler](https://github.com/abhishek-bits/research-practise/blob/master/src/iot/SensorHandler.java) Multi-threaded approach to handle and process requests from Sensors simultaneously.
- [Sensor](https://github.com/abhishek-bits/research-practise/blob/master/src/iot/Sensor.java) Basic properties and functionalties of a sensor device.
- [Port](https://github.com/abhishek-bits/research-practise/blob/master/src/iot/Port.java) Utility class that contains port number info.
- [DB](https://github.com/abhishek-bits/research-practise/blob/master/src/iot/DB.java) Contains Database related connectivity and configuration info.
- [Attacker](https://github.com/abhishek-bits/research-practise/blob/master/src/iot/Attacker.java) Common attacks that an Attacker can possibly fire at any IOT system. e.g. Dictionary attack.
- [AttackerRepo](https://github.com/abhishek-bits/research-practise/blob/master/src/iot/AttackerRepo.java) Basically a well researched sensitive information like port numbers, commonly used usernames and passwords that are used by the Attacker.
- [Master](https://github.com/abhishek-bits/research-practise/blob/master/src/iot/Master.java) (or Defender) The key to our secure IOT system. Implements the algorithm and predicts whether a particular attacker was compromised by the attacker.

## Technologies

- Java (Multi-threading and Socket Programming)
- Eclipse
- MySQL