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
public class StaticTagProtocolDriver extends ProtocolDriver{
    boolean enabled = true;
    public void setEnabled(boolean aEnabled)
    {
        enabled = aEnabled;
    }
    public boolean getEnabled()
    {
        return enabled;
    }
    public void getIncomingRecords()
    {
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
    public Class getProtocolHandlerClass()
    {
        return StaticTagHandler.class;
    }
    public Class getProtocolRecordClass()
    {
        return StaticTagProtocolRecord.class;
    }
}
