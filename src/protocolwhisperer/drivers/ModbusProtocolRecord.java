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
    int formatType = 0;
    int protocolType = 0;
    public byte[] rawValue = null;
    double numericValue = 0;
    public int startingRegister = 0;
    public int quantity = 0;
    public int functionCode = 0;
    public String slaveHost = null;
    public int slavePort = 502;
    public static int PROTOCOL_TYPE_SLAVE = 1;
    public static int PROTOCOL_TYPE_MASTER = 2;
    public static int FORMAT_TYPE_RAW = 1;
    public static int FORMAT_TYPE_FLOAT = 2;
    public static int FORMAT_TYPE_UINT_16 = 3;
    public static int FORMAT_TYPE_UINT_32 = 4;
    public static int HOLDING_REGISTER_FUNCTION = 3;
    public boolean wordSwap = false;
    public boolean byteSwap = false;
    public int node = 0;
    public ModbusProtocolRecord(String aTag, int protocol, String host, int port, int aNode, int format, int aFunctionCode, int aStartingRegister, int aQuantity, boolean aWordSwap, boolean aByteSwap)
    {
        tag = aTag;
        protocolType = protocol;
        slaveHost = host;
        slavePort = port;
        formatType = format;
        startingRegister = aStartingRegister;
        quantity = aQuantity;
        functionCode = aFunctionCode;
        byteSwap = aByteSwap;
        wordSwap = aWordSwap;
        node = aNode;
    }
    public ModbusProtocolRecord()
    {
        
    }
    public byte[] wordSwap(byte[] buf)
    {
        byte[] returnBuf = new byte[buf.length];
        for (int i = 0; i < buf.length; i = i + 4)
        {
            returnBuf[i] = buf[i+2];
            returnBuf[i+1] = buf[i+3];
            returnBuf[i+2] = buf[i];
            returnBuf[i+3] = buf[i+1];
        }
        return returnBuf;
    }
    public byte[] byteSwap(byte[] buf)
    {
        byte[] returnBuf = new byte[buf.length];
        for (int i = 0; i < buf.length; i = i + 2)
        {
            returnBuf[i] = buf[i+1];
            returnBuf[i+1] = buf[i];
        }
        return returnBuf;
    }
    public long bytesToInt32(byte[] buf)
    {
        int[] regs = DataUtils.BeToIntArray(buf);
        return ((long)regs[0]*65536) + (long)regs[1];
    }
    public void setProtocolType(int aProtocolType)
    {
        protocolType = aProtocolType;
    }
    public int getProtocolType()
    {
        return protocolType;
    }
    public void setFormatType(int aFormatType)
    {
        formatType = aFormatType;
    }
    public int getFormatType()
    {
        return formatType;
    }
    public double getValue()
    {
        byte[] workingValue = rawValue;
        if (workingValue == null)
        {
            return 0;
        }
        if (wordSwap)
        {
            workingValue = wordSwap(rawValue);
        }
        if (byteSwap)
        {
            workingValue = byteSwap(rawValue);
        }
        if (formatType == FORMAT_TYPE_FLOAT)
        {
            return DataUtils.toFloat(workingValue);
        }
        if (formatType == FORMAT_TYPE_UINT_16)
        {
            return DataUtils.BeToIntArray(workingValue)[0];
        }
        if (formatType == FORMAT_TYPE_UINT_32)
        {
            return bytesToInt32(workingValue);
        }
        return 0;
    }
    public void setValue(double value)
    {
        if (formatType != FORMAT_TYPE_RAW)
        {
            if (formatType == FORMAT_TYPE_FLOAT)
            {
                float floatValue = (float)value;
                rawValue = java.nio.ByteBuffer.allocate(4).putFloat(floatValue).array();
                rawValue = wordSwap(rawValue);
            }
            if (formatType == FORMAT_TYPE_UINT_16)
            {
                rawValue = java.nio.ByteBuffer.allocate(4).putInt((int)value).array();
                rawValue = java.util.Arrays.copyOfRange(rawValue, 2, 4);  
            }
            if (formatType == FORMAT_TYPE_UINT_32)
            {
                byte[] buf = java.nio.ByteBuffer.allocate(8).putLong((long)value).array();
                rawValue = java.util.Arrays.copyOfRange(buf, 4, 8);
            }
            if (wordSwap)
            {
                rawValue = wordSwap(rawValue);
            }
            if (byteSwap)
            {
                rawValue = byteSwap(rawValue);
            }
        }
    }
}
