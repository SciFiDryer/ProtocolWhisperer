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

import protocolwhisperer.BridgeMappingRecord;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public interface ProtocolHandler {
    static int PANE_TYPE_INCOMING = 1;
    static int PANE_TYPE_OUTGOING = 2;
    public void buildProtocolPane(int paneType, String selectedItem);
    public ProtocolRecord getIncomingProtocolRecord();
    public ProtocolRecord getOutgoingProtocolRecord(ProtocolRecord incomingRecord);
    public String[] getIncomingMenuNames();
    public String[] getOutgoingMenuNames();
    public boolean getIncomingPanelReady();
    public void setIncomingSettings(ProtocolRecord protocolRecord);
    public void setOutgoingSettings(ProtocolRecord protocolRecord);
}
