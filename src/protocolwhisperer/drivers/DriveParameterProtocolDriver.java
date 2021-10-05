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

import minimalcipj.DPIOnlineReadFullResponse;
import minimalcipj.CIPResponse;
import minimalcipj.CIPDataFormatter;
import minimalcipj.CIPClient;
import etherip.EtherNetIP;
import etherip.types.CIPData;
import java.util.ArrayList;
import java.util.Arrays;
import protocolwhisperer.*;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class DriveParameterProtocolDriver extends ProtocolDriver{
    boolean enabled = true;

    public void setEnabled(boolean aEnabled)
    {
        enabled = aEnabled;
    }
    public boolean getEnabled()
    {
        return enabled;
    }
    
    public void ParameterRead(DriveParameterProtocolRecord currentRecord)
    {
        CIPClient client = null;
        try
        {
            client = new CIPClient(currentRecord.host);
            client.connect();
            for (int i = 0; i < currentRecord.tagRecords.size(); i++)
            {
                DriveParameterTagRecord currentTagRecord = ((DriveParameterTagRecord)currentRecord.tagRecords.get(i));
                double oldValue = currentTagRecord.getValue();
                CIPResponse response = client.getAttribute(0x93, currentTagRecord.parameter, 0x07);
                if (response instanceof DPIOnlineReadFullResponse)
                {
                    CIPDataFormatter formatter = ((DPIOnlineReadFullResponse)(response)).getCIPDataFormatter();
                    currentTagRecord.setValue(formatter.getDisplayVal(((DPIOnlineReadFullResponse)(response)).getParamValue()));
                }
                if (oldValue != currentTagRecord.getValue())
                {
                    currentTagRecord.lastChangedTime = System.currentTimeMillis();
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
        try
        {
            client.disconnect();
        }
        catch (java.io.IOException e)
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
            DriveParameterProtocolRecord currentRecord = (DriveParameterProtocolRecord)recordList.get(i);
            if (currentRecord.getType() == ProtocolRecord.RECORD_TYPE_INCOMING)
            {
                ParameterRead(currentRecord);
            }
        }
    }
    public void mapIncomingValues()
    {   
    }

    public void sendOutgoingRecords()
    {

    }
    public Class getProtocolHandlerClass()
    {
        return DriveParameterProtocolHandler.class;
    }
    public Class getProtocolRecordClass()
    {
        return DriveParameterProtocolRecord.class;
    }
}
