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

import etherip.EtherNetIP;
import etherip.types.CIPData;
import java.util.ArrayList;
import java.util.Arrays;
import protocolwhisperer.*;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class CIPProtocolDriver extends ProtocolDriver{
    boolean enabled = true;

    public void driverInit()
    {
        recordList.clear();
    }
    public void setEnabled(boolean aEnabled)
    {
        enabled = aEnabled;
    }
    public boolean getEnabled()
    {
        return enabled;
    }
    
    public void CIPRead(CIPProtocolRecord currentRecord)
    {
        try
        {
            String[] tagArr = new String[currentRecord.tagRecords.size()];
            for (int i = 0; i < currentRecord.tagRecords.size(); i++)
            {
                tagArr[i] = ((CIPTagRecord)currentRecord.tagRecords.get(i)).cipTag;
            }
            EtherNetIP controller = new EtherNetIP(currentRecord.host, currentRecord.slot);
            controller.connectTcp();
            CIPData[] values = controller.readTags(tagArr);
            controller.close();
            for (int i = 0; i < values.length; i++)
            {
                currentRecord.tagRecords.get(i).setValue(values[i].getNumber(0).doubleValue());
            }
        }
        catch (Exception e)
        {
            if (ProtocolWhisperer.debug)
            {
                e.printStackTrace();
            }
        }
    }
    public void storeProtocolRecord(ProtocolRecord pr)
    {
        recordList.add(pr);
    }
    public void getIncomingRecords()
    {
        for (int i = 0; i < recordList.size(); i++)
        {
            CIPProtocolRecord currentRecord = (CIPProtocolRecord)recordList.get(i);
            if (currentRecord.getType() == ProtocolRecord.RECORD_TYPE_INCOMING)
            {
                CIPRead(currentRecord);
            }
            if (currentRecord.getType() == ProtocolRecord.RECORD_TYPE_OUTGOING)
            {
                CIPWriteTags(currentRecord);
            }
        }
    }
    public void mapIncomingValues()
    {   
    }
    public void shutdown()
    {
    }
    public void sendOutgoingRecords()
    {
      
    }
    public void CIPWriteTags(CIPProtocolRecord currentRecord)
    {
        try
        {
            EtherNetIP controller = new EtherNetIP(currentRecord.host, currentRecord.slot);
            controller.connectTcp();
            String[] tagArr = new String[currentRecord.tagRecords.size()];
            for (int i = 0; i < currentRecord.tagRecords.size(); i++)
            {
                tagArr[i] = ((CIPTagRecord)currentRecord.tagRecords.get(i)).cipTag;
            }
            CIPData[] values = controller.readTags(tagArr);
            for (int i = 0; i < values.length; i++)
            {
                values[i].set(0, currentRecord.tagRecords.get(i).getValue());
            }
            controller.writeTags(tagArr, values);
            controller.close();
        }
        catch (Exception e)
        {
            if (ProtocolWhisperer.debug)
            {
                e.printStackTrace();
            }
        }
    }
    public Class getProtocolHandlerClass()
    {
        return CIPProtocolHandler.class;
    }
    public Class getProtocolRecordClass()
    {
        return CIPProtocolRecord.class;
    }
}
