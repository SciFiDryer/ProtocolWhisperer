/*
 * Copyright 2020 Matt Jamesson <scifidryer@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package protocolwhisperer.drivers;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import protocolwhisperer.BridgeEntryContainer;
import protocolwhisperer.BridgeMappingRecord;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */

public class ModbusProtocolHandler implements ProtocolHandler{
    static String[] dataTypeMenuNames = new String[] {"Select data type", "Float", "Unsigned Int16", "Unsigned Int32"};
    String[] incomingMenuNames = new String[] {"From Modbus Slave (act as master)", "From Modbus Master (act as slave)"};
    String[] outgoingMenuNames = new String[] {"To Modbus Slave (act as master)", "To Modbus Master (act as slave)"};
    public Class protocolHandler = ModbusProtocolHandler.class;
    public ModbusProtocolHandler()
    {
        
    }
    public ProtocolRecord getNewProtocolRecord(int type, String calledMenuItem)
    {
        int protocolType = 0;
        
        if (calledMenuItem.equals(incomingMenuNames[0]) || calledMenuItem.equals(outgoingMenuNames[0]))
        {
            protocolType = ModbusProtocolRecord.PROTOCOL_TYPE_MASTER;
        }
        if (calledMenuItem.equals(incomingMenuNames[1]) || calledMenuItem.equals(outgoingMenuNames[1]))
        {
            protocolType = ModbusProtocolRecord.PROTOCOL_TYPE_SLAVE;
        }
        return new ModbusProtocolRecord(type, protocolType, calledMenuItem);
        
    }
    public String[] getIncomingMenuNames()
    {
        return incomingMenuNames;
    }
    public String[] getOutgoingMenuNames()
    {
        return outgoingMenuNames;
    }
    
    public static String getMenuItemFromFormat(int format)
    {
        if (format == ModbusProtocolRecord.FORMAT_TYPE_FLOAT)
        {
            return dataTypeMenuNames[1];
        }
        else if (format == ModbusProtocolRecord.FORMAT_TYPE_UINT_16)
        {
            return dataTypeMenuNames[2];
        }
        else if (format == ModbusProtocolRecord.FORMAT_TYPE_UINT_32)
        {
            return dataTypeMenuNames[3];
        }
        return "";
    }
    public static int getFormatFromMenuItem(String menuItem)
    {
        int format = 0;
        if (menuItem.equals(dataTypeMenuNames[1]))
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_FLOAT;
        }
        else if (menuItem.equals(dataTypeMenuNames[2]))
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_UINT_16;
        }
        else if (menuItem.equals(dataTypeMenuNames[3]))
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_UINT_32;
        }
        return format;
    }
    public void configure(ProtocolRecord currentRecord)
    {
        new ModbusConfigFrame((ModbusProtocolRecord)currentRecord).setVisible(true);
    }
}
