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
import protocolwhisperer.*;
import java.util.*;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public abstract class ProtocolDriver {
    protected ArrayList<ProtocolRecord> recordList = new ArrayList<ProtocolRecord>();
    boolean enabled = true;
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
    public abstract void getIncomingRecords();
    public abstract void sendOutgoingRecords();
    public abstract void mapIncomingValues();
    public void shutdown()
    {
        recordList.clear();
    }
    public abstract Class getProtocolHandlerClass();
    public abstract Class getProtocolRecordClass();
    public void storeProtocolRecord(ProtocolRecord currentRecord)
    {
        recordList.add(currentRecord);
    }
}
