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
import com.intelligt.modbus.jlibmodbus.utils.DataUtils;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class ModbusProtocolRecord extends ProtocolRecord {
    
    int protocolType = 0;
    public static int PROTOCOL_TYPE_SLAVE = 1;
    public static int PROTOCOL_TYPE_MASTER = 2;
    public String slaveHost = null;
    public int slavePort = 502;
    public int node = 0;
    public Class protocolHandler = ModbusProtocolHandler.class;
    public ModbusProtocolRecord()
    {  
    }
    public Class getProtocolHandlerClass()
    {
        return protocolHandler;
    }
    public ModbusProtocolRecord(int aType, int aProtocolType, String calledMenuItem)
    {
        type = aType;
        protocolType = aProtocolType;
        selectedItem = calledMenuItem;
    }
    
    public void setProtocolType(int aProtocolType)
    {
        protocolType = aProtocolType;
    }
    public int getProtocolType()
    {
        return protocolType;
    }
    
}
