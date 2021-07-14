/*
 * Copyright 2021 Matt Jamesson <scifidryer@gmail.com>.
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
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class StaticTagHandler implements ProtocolHandler{
    public ProtocolRecord getNewProtocolRecord(int type, String calledMenuItem)
    {
        return new StaticTagProtocolRecord(type, calledMenuItem);
    }
    public String[] getIncomingMenuNames()
    {
        return new String[] {"Static tags"};
    }
    public String[] getOutgoingMenuNames()
    {
        return new String[] {};
    }
    public void configure(ProtocolRecord currentRecord, BridgeManager manager)
    {
        new StaticTagConfigFrame((StaticTagProtocolRecord)currentRecord, manager).setVisible(true);
    }
}
