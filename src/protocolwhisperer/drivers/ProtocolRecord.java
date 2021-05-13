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

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
import java.util.*;
public abstract class ProtocolRecord {
    public static int RECORD_TYPE_INCOMING = 1;
    public static int RECORD_TYPE_OUTGOING = 2;
    int type = 0;
    public String selectedItem = "";
    public boolean configured = false;
    public ArrayList<TagRecord> tagRecords = new ArrayList<TagRecord>();
    public int getType()
    {
        return type;
    }
    public void setType(int aType)
    {
        type = aType;
    }
    public abstract Class getProtocolHandlerClass();
}
