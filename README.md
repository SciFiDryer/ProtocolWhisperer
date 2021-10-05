# ProtocolWhisperer

Cross platform industrial automation protocol converter

## Overview

Previously the bridge functionality of [ModbusMechanic](https://github.com/SciFiDryer/ModbusMechanic).

This project is in beta - the GUI is still under development so if you notice bugs, please open an issue.

If you have an Allen-Bradley PLC and you want to pipe Modbus data into it over the network for testing this is possible with ProtocolWhisperer. You can also expose Allen-Bradley data over Modbus. Only network based protocols are supported so far, but when combined with the gateway functionality of ModbusMechanic, Modbus RTU can be exposed over a network to Modbus TCP.

The data is updated followed by a rest period specified in the bridge. If 1000ms is specified, the data update interval will be slightly longer than 1000ms due to the time it takes to retrieve the data.

After saving the bridge config to a file such as "bridge.cfg", if you want to start a headless instance of the bridge use the following command:
```
$ java -jar ProtocolWhisperer.jar -bridge bridge.cfg
```

## Quickstart

Download & extract the [latest release](#latest-release) and double click ProtocolWhisperer.jar.

## Dependencies

This project depends on the JLibModbus library and EtherIP for CIP functionality.

https://github.com/kochedykov/jlibmodbus  

https://github.com/EPICSTools/etherip

## Logging data with JDBC plugins

The currently supported datalog drivers are sqlite and MySQL. To use them, download the appropriate JDBC jar file and put it in the plugins folder. The next time ProtocolWhisperer is launched, you will see an option for it in datalogging.

## Building from source

This is a NetBeans project. It can be built by pulling it in the NetBeans IDE or manually compiling. The project requires the JLibModbus and EtherIP jars.

## Latest release

[ProtocolWhisperer.v0.4.zip](https://github.com/SciFiDryer/ProtocolWhisperer/releases/download/v0.4/ProtocolWhisperer.v0.4.zip)

## Completed features and planned features

- [x] Protocol conversion
- [x] Plugins for other protocols
- [x] Data logger
- [ ] Visual trending of data
- [x] Scripting of collected data
