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

import com.intelligt.modbus.jlibmodbus.master.*;
import com.intelligt.modbus.jlibmodbus.msg.base.ModbusResponse;
import com.intelligt.modbus.jlibmodbus.data.*;
import com.intelligt.modbus.jlibmodbus.msg.base.AbstractMultipleRequest;
import com.intelligt.modbus.jlibmodbus.msg.base.AbstractWriteMultipleRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadCoilsRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadDiscreteInputsRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadHoldingRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadInputRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.WriteMultipleCoilsRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.WriteMultipleRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.response.ReadHoldingRegistersResponse;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import com.intelligt.modbus.jlibmodbus.slave.*;
import com.intelligt.modbus.jlibmodbus.exception.*;
import java.net.InetAddress;
import java.util.*;
import protocolwhisperer.*;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class ModbusProtocolDriver extends ProtocolDriver{
    boolean enabled = false;
    ArrayList<ModbusHostRecord> incomingSlaveList = new ArrayList();
    ArrayList<ModbusHostRecord> outgoingSlaveList = new ArrayList();
    ArrayList<LocalModbusSlave> localRunningSlaves = new ArrayList();
    boolean outgoingSlavesStarted = false;
    boolean incomingSlavesStarted = false;
    public boolean getEnabled()
    {
        return enabled;
    }
    public void setEnabled(boolean aEnabled)
    {
        enabled = aEnabled;
    }
    public void driverInit()
    {

    }
    public Class getProtocolRecordClass()
    {
        return ModbusProtocolRecord.class;
    }
    public void getIncomingRecords()
    {
        startLocalIncomingSlaves();
        fetchIncomingData();
    }
    public void sendOutgoingRecords()
    {
        sendOutgoingData();
    }
    public void startLocalIncomingSlaves()
    {
        if (!incomingSlavesStarted)
        {
            for (int i = 0; i < incomingSlaveList.size(); i++)
            {
                if (incomingSlaveList.get(i).hostType == ModbusHostRecord.HOST_TYPE_LOCAL_SLAVE)
                {
                    startLocalSlave(incomingSlaveList.get(i));
                }
            }
        }
        incomingSlavesStarted = true;
    }
    public void startLocalOutgoingSlaves()
    {
        if (!outgoingSlavesStarted)
        {
            for (int i = 0; i < outgoingSlaveList.size(); i++)
            {
                if (outgoingSlaveList.get(i).hostType == ModbusHostRecord.HOST_TYPE_LOCAL_SLAVE)
                {
                    startLocalSlave(outgoingSlaveList.get(i));
                }
            }
        }
        outgoingSlavesStarted = true;
    }
    public void startLocalSlave(ModbusHostRecord slaveRecord)
    {
        try
        {
            TcpParameters parameters = new TcpParameters();
            parameters.setHost(InetAddress.getLocalHost());
            parameters.setPort(slaveRecord.port);
            ModbusSlave slave = ModbusSlaveFactory.createModbusSlaveTCP(parameters);
            slave.setServerAddress(1);
            slave.setReadTimeout(30000);
            //slave.setDataHolder(new SimulatorDataHolder(registerList));
            slave.listen();
            byte[] buf = new byte[131070];
            for (int j = 0; j < buf.length; j++)
            {
                byte b = 0;
                buf[j] = b;
            }
            ModbusHoldingRegisters hr = new ModbusHoldingRegisters();
            ModbusHoldingRegisters ir = new ModbusHoldingRegisters();
            hr.setBytesBe(buf);
            ir.setBytesBe(buf);
            DataHolder dh = new DataHolder(){
            @Override
            public void writeHoldingRegister(int offset, int value) throws IllegalDataAddressException, IllegalDataValueException {
                super.writeHoldingRegister(offset, value);

            }
            @Override
            public int readHoldingRegister(int offset) throws IllegalDataAddressException
            {
                return super.readHoldingRegister(offset);
            }
            @Override
            public int[] readHoldingRegisterRange(int offset, int quantity) throws IllegalDataAddressException
            {
                return super.readHoldingRegisterRange(offset, quantity);
            }
            @Override
            public int[] readInputRegisterRange(int offset, int quantity) throws IllegalDataAddressException
            {
                return super.readInputRegisterRange(offset, quantity);
            }
            @Override
            public boolean[] readDiscreteInputRange(int offset, int quantity) throws IllegalDataAddressException, IllegalDataValueException
            {
                return super.readDiscreteInputRange(offset, quantity);
            }
            @Override
            public boolean[] readCoilRange(int offset, int quantity) throws IllegalDataAddressException, IllegalDataValueException
            {
                return super.readCoilRange(offset, quantity);
            }
            @Override
            public void writeHoldingRegisterRange(int offset, int[] range) throws IllegalDataAddressException, IllegalDataValueException {
                
                super.writeHoldingRegisterRange(offset, range);
                refreshRegisters(slaveRecord, hr.getBytes());
            }

            @Override
            public void writeCoil(int offset, boolean value) throws IllegalDataAddressException, IllegalDataValueException {
                super.writeCoil(offset, value);
            }

            @Override
            public void writeCoilRange(int offset, boolean[] range) throws IllegalDataAddressException, IllegalDataValueException {
                super.writeCoilRange(offset, range);
            }};

            dh.setHoldingRegisters(hr);
            dh.setInputRegisters(ir);
            slave.setDataHolder(dh);
            localRunningSlaves.add(new LocalModbusSlave(slave, slaveRecord.port));
            slaveRecord.localSlave = slave;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void refreshRegisters(ModbusHostRecord record, byte[] target)
    {
        for (int i = 0; i < record.registerRecords.size(); i++)
        {
            RegisterRecord rr = record.registerRecords.get(i);
            rr.value = Arrays.copyOfRange(target, rr.register*2, (rr.register*2)+2);
        }
    }
    
    public void storeProtocolRecord(ProtocolRecord pr)
    {
        ModbusProtocolRecord currentRecord = (ModbusProtocolRecord)pr;
        recordList.add(pr);
        //we maintain concurrent lists here so also build the modbus maps
        if (currentRecord.getType() == ProtocolRecord.RECORD_TYPE_INCOMING)
        {
            addToModbusList(incomingSlaveList, currentRecord);
        }
    }
    public void updateOutgoingSlaves()
    {
        outgoingSlaveList.clear();
        for (int i = 0; i < recordList.size(); i++)
        {
            ModbusProtocolRecord currentRecord = (ModbusProtocolRecord)recordList.get(i);
            if (currentRecord.getType() == ProtocolRecord.RECORD_TYPE_OUTGOING && currentRecord.recordChanged)
            {
                addToModbusList(outgoingSlaveList, currentRecord);
            }
        }
    }
    public static ModbusResponse generateModbusMessage(ModbusMaster master, int protocolId, int transactionId, int slaveNode, int functionCode, int register, int quantity, byte[] values) throws Exception
    {
        
        ModbusResponse response = null;
        if (!master.isConnected())
        {
            master.connect();
        }
        AbstractMultipleRequest request = null;
        if (functionCode == 1)
        {
            request = new ReadCoilsRequest();
        }
        if (functionCode == 2)
        {
            request = new ReadDiscreteInputsRequest();
        }
        if (functionCode == 3)
        {
            request = new ReadHoldingRegistersRequest();
        }
        else if (functionCode == 4)
        {
            request = new ReadInputRegistersRequest();
        }
        else if (functionCode == 15)
        {
            request = new WriteMultipleCoilsRequest();
        }
        else if (functionCode == 16)
        {
            request = new WriteMultipleRegistersRequest();
        }
        request.setServerAddress(slaveNode);
        request.setStartAddress(register);
        request.setQuantity(quantity);
        if (request instanceof AbstractWriteMultipleRequest)
        {
            ((AbstractWriteMultipleRequest)(request)).setBytes(values);
        }
        if (master instanceof ModbusMasterTCP)
        {
            master.setTransactionId(transactionId);
            request.setProtocolId(protocolId);
        }
        response = master.processRequest(request);
        return response;
    }
    public void addToModbusList(ArrayList<ModbusHostRecord> currentList, ModbusProtocolRecord currentRecord)
    {
        if (!currentRecord.individualCalls && currentRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_MASTER)
        {
            ModbusHostRecord newHostRecord = new ModbusHostRecord(ModbusHostRecord.HOST_TYPE_REMOTE_SLAVE, currentRecord.slaveHost, currentRecord.slavePort, currentRecord.node);
            for (int j = 0; j < currentRecord.tagRecords.size(); j++)
            {
                ModbusTagRecord currentRegister = (ModbusTagRecord)currentRecord.tagRecords.get(j);
                for (int k = 0; k < currentRegister.quantity; k++)
                {
                    RegisterRecord rr = new RegisterRecord(currentRegister.functionCode, k+currentRegister.startingRegister);
                    if (currentRegister.rawValue != null)
                    {
                        rr.value = Arrays.copyOfRange(currentRegister.rawValue, k*2, (k*2)+2);
                    }
                    newHostRecord.registerRecords.add(rr);
                }
            }
            currentList.add(newHostRecord);
        }
        else if (currentRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_SLAVE)
        {
            ModbusHostRecord newHostRecord = new ModbusHostRecord(ModbusHostRecord.HOST_TYPE_LOCAL_SLAVE, "", currentRecord.slavePort, currentRecord.node);
            for (int j = 0; j < currentRecord.tagRecords.size(); j++)
            {
                ModbusTagRecord currentRegister = (ModbusTagRecord)currentRecord.tagRecords.get(j);
                for (int k = 0; k < currentRegister.quantity; k++)
                {
                    RegisterRecord rr = new RegisterRecord(currentRegister.functionCode, k+currentRegister.startingRegister);
                    if (currentRegister.rawValue != null)
                    {
                        rr.value = Arrays.copyOfRange(currentRegister.rawValue, k*2, (k*2)+2);
                    }
                    newHostRecord.registerRecords.add(rr);
                    associateSlave(newHostRecord);
                }
            }
            currentList.add(newHostRecord);
        }
    }
    public void associateSlave(ModbusHostRecord currentRecord)
    {
        for (int i = 0; i < localRunningSlaves.size(); i++)
        {
            if (localRunningSlaves.get(i).port == currentRecord.port)
            {
                currentRecord.localSlave = localRunningSlaves.get(i).localSlave;
            }
        }
    }
    public void fetchIncomingData()
    {
        for (int i = 0; i < recordList.size(); i++)
        {
            ModbusProtocolRecord currentRecord = (ModbusProtocolRecord)recordList.get(i);
            if (currentRecord.individualCalls)
            {
                ModbusMaster master = null;
                try
                {
                    TcpParameters tcpParameters = new TcpParameters();
                    tcpParameters.setHost(InetAddress.getByName(currentRecord.slaveHost));
                    tcpParameters.setKeepAlive(true);
                    tcpParameters.setPort(currentRecord.slavePort);
                    //this should work but somehow doesn't
                    //tcpParameters.setConnectionTimeout(3000);
                    if (ProtocolWhisperer.debug)
                    {
                        System.out.println("Connecting to slave " + currentRecord.slaveHost + " on port " + currentRecord.slavePort);
                    }
                    master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
                    master.setResponseTimeout(5000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                for (int j = 0; j < currentRecord.tagRecords.size(); j++)
                {
                    ModbusTagRecord currentTag = (ModbusTagRecord)currentRecord.tagRecords.get(j);
                    try
                    {
                        ModbusResponse response = generateModbusMessage(master, 0, 1, currentRecord.node, currentTag.functionCode, currentTag.startingRegister, currentTag.quantity, null);
                        if (response != null)
                        {
                            if (currentTag.functionCode == 3 || currentTag.functionCode == 4)
                            {
                                double oldValue = currentTag.getValue();
                                byte[] buf = ((ReadHoldingRegistersResponse)(response)).getBytes();
                                currentTag.rawValue = buf;
                                if (currentTag.getValue() != oldValue)
                                {
                                    currentTag.lastChangedTime = System.currentTimeMillis();
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        if (ProtocolWhisperer.debug)
                        {
                            e.printStackTrace();
                        }
                    }
                    if (master != null)
                    {
                        try
                        {
                            master.disconnect();
                        }
                        catch (Exception e)
                        {
                            if (ProtocolWhisperer.debug)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < incomingSlaveList.size(); i++)
        {
            ModbusHostRecord currentSlave = incomingSlaveList.get(i);
            if (currentSlave.hostType == ModbusHostRecord.HOST_TYPE_REMOTE_SLAVE)
            {
                remoteSlaveFetch(currentSlave);
            }
            if (currentSlave.hostType == ModbusHostRecord.HOST_TYPE_LOCAL_SLAVE)
            {
                localSlaveFetch(currentSlave);
            }
        }
    }
    public void remoteSlaveFetch(ModbusHostRecord currentSlave)
    {
        byte[] buf = null;
        ModbusResponse response = null;
        Exception raisedException = null;
        ModbusMaster master = null;
        try
        {
            TcpParameters tcpParameters = new TcpParameters();
            tcpParameters.setHost(InetAddress.getByName(currentSlave.hostname));
            tcpParameters.setKeepAlive(true);
            tcpParameters.setPort(currentSlave.port);
            //this should work but somehow doesn't
            //tcpParameters.setConnectionTimeout(3000);
            if (ProtocolWhisperer.debug)
            {
                System.out.println("Connecting to slave " + currentSlave.hostname + " on port " + currentSlave.port);
            }
            master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
            master.setResponseTimeout(5000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //holding registers
        modbusBlockRead(3, master, currentSlave);
        //input registers
        modbusBlockRead(4, master, currentSlave);
        try
        {
            master.disconnect();
        }
        catch (Exception e)
        {
            if (ProtocolWhisperer.debug)
            {
                e.printStackTrace();
            }
        }
    }
    public void modbusBlockRead(int functionCode, ModbusMaster master, ModbusHostRecord currentSlave)
    {
        int[] registers = getSortedRegisters(functionCode, currentSlave);
        ModbusResponse response = null;
        int startingRegister = 0;
        int quantity = 0;
        for (int j = 0; j < registers.length; j++)
        {
            startingRegister = registers[j];
            quantity = 1;
            int lastIndex = j;
            for (int k = j; k < registers.length; k++)
            {
                int calcDistance = registers[k]-registers[j];
                if (calcDistance < 128)
                {
                    quantity = calcDistance + 1;
                    lastIndex = k;
                }
            }
            j = lastIndex;
            try 
            {
                if (ProtocolWhisperer.debug)
                {
                    System.out.println("Requesting function code " + functionCode + " register " + startingRegister +  " length " + quantity + " from slave " + currentSlave.hostname);
                }
                response = generateModbusMessage(master, 0, 1, currentSlave.node, functionCode, startingRegister, quantity, null);
            }
            catch(Exception e)
            {
                if (ProtocolWhisperer.debug)
                {
                    e.printStackTrace();
                }
            }
            if (response != null)
            {
                if (ProtocolWhisperer.debug)
                {
                    System.out.println("Got response from slave " + currentSlave.hostname);
                }
                byte[] buf = ((ReadHoldingRegistersResponse)(response)).getBytes();
                for (int k = 0; k < quantity; k++)
                {
                    byte[] value = Arrays.copyOfRange(buf, k*2, k*2+2);
                    currentSlave.insertRegisterValue(functionCode, k+startingRegister, value);
                }
            }
        }
    }
    public void localSlaveFetch(ModbusHostRecord currentSlave)
    {
        byte[] buf = null;
        ModbusResponse response = null;
        Exception raisedException = null;
        int[] registers = getSortedRegisters(3, currentSlave);
        
        int quantity = 0;
        for (int j = 0; j < registers.length; j++)
        {
            byte[] registerBuf = currentSlave.localSlave.getDataHolder().getHoldingRegisters().getBytes();
            buf = Arrays.copyOfRange(registerBuf, registers[j]*2, (registers[j]*2)+2);
            currentSlave.insertRegisterValue(3, registers[j], buf);
        }
    }
    public int[] getSortedRegisters(int functionCode, ModbusHostRecord mhr)
    {
        int[] returnArray = new int[] {};
        for (int i = 0; i < mhr.registerRecords.size(); i++)
        {
            if (mhr.registerRecords.get(i).functionCode == functionCode)
            {
                returnArray = Arrays.copyOf(returnArray, returnArray.length+1);
                returnArray[i] = mhr.registerRecords.get(i).register;
            }
        }
        Arrays.sort(returnArray);
        return returnArray;
    }
    public void mapIncomingValues()
    {
        for (int i = 0; i < recordList.size(); i++)
        {
            ModbusProtocolRecord currentRecord = (ModbusProtocolRecord)recordList.get(i);
            if (!currentRecord.individualCalls && currentRecord.getType() == ProtocolRecord.RECORD_TYPE_INCOMING)
            {
                for (int j = 0; j < currentRecord.tagRecords.size(); j++)
                {
                    ModbusTagRecord currentTag = (ModbusTagRecord)currentRecord.tagRecords.get(j);
                    double oldValue = currentTag.getValue();
                    currentTag.rawValue = getModbusValue(currentRecord.slaveHost, currentRecord.slavePort, currentTag.functionCode, currentTag.startingRegister, currentTag.quantity);
                    if (currentTag.getValue() != oldValue)
                    {
                        currentTag.lastChangedTime = System.currentTimeMillis();
                    }
                }
            }
        }
    }
    public byte[] getModbusValue(String host, int port, int functionCode, int startingRegister, int quantity)
    {
        for (int i = 0; i < incomingSlaveList.size(); i++)
        {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            ModbusHostRecord currentSlave = incomingSlaveList.get(i);
            if (currentSlave.port == port && (currentSlave.hostType == ModbusHostRecord.HOST_TYPE_LOCAL_SLAVE || (currentSlave.hostname.equals(host) && currentSlave.hostType == ModbusHostRecord.HOST_TYPE_REMOTE_SLAVE)) )
            {
                for (int j = 0; j < currentSlave.registerRecords.size(); j++)
                {
                    RegisterRecord rr = currentSlave.registerRecords.get(j);
                    if (rr.value == null)
                    {
                        return null;
                    }
                    if (rr.register >= startingRegister && rr.register < startingRegister + quantity)
                    {
                        try
                        {
                            baos.write(rr.value);
                            baos.flush();
                        }
                        catch (Exception e)
                        {
                            if (ProtocolWhisperer.debug)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return baos.toByteArray();
            }
        }
        return null;
    }
    
    public void sendOutgoingData()
    {
        updateOutgoingSlaves();
        startLocalOutgoingSlaves();
        for (int i = 0; i < outgoingSlaveList.size(); i++)
        {
            ModbusHostRecord currentRecord = outgoingSlaveList.get(i);
            if (currentRecord.hostType == ModbusHostRecord.HOST_TYPE_REMOTE_SLAVE)
            {
                sendToRemoteSlave(currentRecord);
            }
            if (currentRecord.hostType == ModbusHostRecord.HOST_TYPE_LOCAL_SLAVE)
            {
                sendToLocalSlave(currentRecord);
            }
        }
    }
    public void sendToLocalSlave(ModbusHostRecord currentSlave)
    {
        
        for (int i = 0; i < currentSlave.registerRecords.size(); i++)
        {
            byte[] registerBuf = null;
            if (currentSlave.registerRecords.get(i).functionCode == 3)
            {
                registerBuf = currentSlave.localSlave.getDataHolder().getHoldingRegisters().getBytes();
            }
            if (currentSlave.registerRecords.get(i).functionCode == 4)
            {
                registerBuf = currentSlave.localSlave.getDataHolder().getInputRegisters().getBytes();
            }
            setLocalSlaveRegister(currentSlave.registerRecords.get(i).register, currentSlave.registerRecords.get(i).value, registerBuf);
            if (currentSlave.registerRecords.get(i).functionCode == 3)
            {
                currentSlave.localSlave.getDataHolder().getHoldingRegisters().setBytesBe(registerBuf);
            }
            if (currentSlave.registerRecords.get(i).functionCode == 4)
            {
                currentSlave.localSlave.getDataHolder().getInputRegisters().setBytesBe(registerBuf);
            }
            
        }
    }
    public void setLocalSlaveRegister(int register, byte[] value, byte[] buffer)
    {
        for (int i = 0; i < value.length; i++)
        {
            buffer[register*2+i] = value[i];
        }
    }
    public void sendToRemoteSlave(ModbusHostRecord currentSlave)
    {
        byte[] buf = null;
        ModbusResponse response = null;
        Exception raisedException = null;
        ModbusMaster master = null;
        try
        {
            TcpParameters tcpParameters = new TcpParameters();
            tcpParameters.setHost(InetAddress.getByName(currentSlave.hostname));
            tcpParameters.setKeepAlive(true);
            tcpParameters.setPort(currentSlave.port);
            //this should work but somehow doesn't
            //tcpParameters.setConnectionTimeout(3000);
            master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
            master.setResponseTimeout(5000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        int[] registers = getSortedRegisters(3, currentSlave);
        int startingRegister = 0;
        int quantity = 0;
        for (int j = 0; j < registers.length; j++)
        {
            startingRegister = registers[j];
            quantity = 1;
            for (int k = 0; k < 250 && j+k < registers.length; k++)
            {
                int calcDistance = registers[j+k]-registers[j] + 1;
                if (calcDistance < 1024)
                {
                    quantity = calcDistance;
                    j = j+k;
                }
            }
            byte[] values = getModbusValues(currentSlave, 3, startingRegister, quantity);

            try
            {
                if (ProtocolWhisperer.debug)
                {
                    System.out.println("Modbus master request to " + currentSlave.hostname + " function 3 register " + startingRegister + " length " + quantity + " byte length " + values.length);
                }
                response = generateModbusMessage(master, 0, 1, currentSlave.node, 16, startingRegister, quantity, values);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            try
            {
                master.disconnect();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public byte[] getModbusValues(ModbusHostRecord currentRecord, int functionCode, int register, int quantity)
    {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        for (int i = 0; i < quantity; i++)
        {
            for (int j = 0; j < currentRecord.registerRecords.size(); j++)
            {
                RegisterRecord rr = currentRecord.registerRecords.get(j);
                if (rr.functionCode == functionCode && rr.register == register+i)
                {
                    
                    if (rr.value != null)
                    {
                        try
                        {
                            baos.write(rr.value);
                            baos.flush();
                        }
                        catch(java.io.IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return baos.toByteArray();
    }
    public void shutdown()
    {
        for (int i = 0; i < localRunningSlaves.size(); i++)
        {
            try
            {
                localRunningSlaves.get(i).localSlave.shutdown();
                outgoingSlavesStarted = false;
                incomingSlavesStarted = false;
            }
            catch(Exception e)
            {
                if (ProtocolWhisperer.debug)
                {
                    e.printStackTrace();
                }
            }
        }
        incomingSlaveList.clear();
        outgoingSlaveList.clear();
        super.shutdown();
    }
    public Class getProtocolHandlerClass()
    {
        return ModbusProtocolHandler.class;
    }
}
