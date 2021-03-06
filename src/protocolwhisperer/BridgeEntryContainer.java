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
package protocolwhisperer;
import protocolwhisperer.drivers.ProtocolHandler;
import java.awt.event.*;
import java.util.*;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class BridgeEntryContainer implements java.io.Serializable{
    public String driverSelection = null;
    public String uuid = null;
    public javax.swing.JTable table = null;
    public BridgeEntryContainer(String aDriverSelection, javax.swing.JTable aTable)
    {
        driverSelection = aDriverSelection;
        table = aTable;
        uuid = uuid = java.util.UUID.randomUUID().toString();
    }
}
