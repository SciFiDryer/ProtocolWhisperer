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
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import protocolwhisperer.*;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */

public class DriverMenuHandler implements ActionListener, java.io.Serializable{
    JComboBox incomingDataSelector = null;
    public JComboBox outgoingDataSelector = null;
    MainFrame parentFrame = null;
    BridgeEntryContainer parentEntryContainer = null;
    ArrayList<ProtocolHandler> driverList = new ArrayList();
    JPanel outgoingPanel = null;
    JPanel incomingDataSettings = null;
    boolean firstRun = true;
    public DriverMenuHandler()
    {
        
    }
    public ArrayList<ProtocolHandler> getDriverList()
    {
        return driverList;
    }
    public DriverMenuHandler(JComboBox aIncomingDataSelector, JComboBox aOutgoingDataSelector, MainFrame aParentFrame, JPanel aIncomingDataSettings, JPanel aOutgoingPanel)
    {
        incomingDataSelector = aIncomingDataSelector;
        outgoingDataSelector = aOutgoingDataSelector;
        parentFrame = aParentFrame;
        outgoingPanel = aOutgoingPanel;
        incomingDataSettings = aIncomingDataSettings;
        loadDrivers();
    }
    public void loadDrivers()
    {
        driverList.add(new ModbusProtocolHandler(this, parentFrame, parentEntryContainer, incomingDataSettings, outgoingPanel));
        //driverList.add(new CIPProtocolHandler(this, parentFrame, parentEntryContainer, incomingDataSettings, outgoingPanel));
        ArrayList<String> menuItems = new ArrayList();
        ArrayList<String> outgoingMenuItems = new ArrayList();
        menuItems.add("Select");
        outgoingMenuItems.add("Select");
        for (int i = 0; i < driverList.size(); i++)
        {
            String[] menuNames = driverList.get(i).getIncomingMenuNames();
            for (int j = 0; j < menuNames.length; j++)
            {
                menuItems.add(menuNames[j]);
            }
            menuNames = driverList.get(i).getOutgoingMenuNames();
            for (int j = 0; j < menuNames.length; j++)
            {
                outgoingMenuItems.add(menuNames[j]);
            }
        }
        DefaultComboBoxModel model = new DefaultComboBoxModel(menuItems.toArray());
        incomingDataSelector.setModel(model);
        DefaultComboBoxModel outgoingModel = new DefaultComboBoxModel(outgoingMenuItems.toArray());
        outgoingDataSelector.setModel(outgoingModel);
    }
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == incomingDataSelector)
        {
            dispatchDriverEvent(ProtocolHandler.PANE_TYPE_INCOMING, incomingDataSelector.getSelectedItem().toString());
        }
        if (e.getSource() == outgoingDataSelector)
        {
            dispatchDriverEvent(ProtocolHandler.PANE_TYPE_OUTGOING, outgoingDataSelector.getSelectedItem().toString());
        }
    }
    public JTable dispatchDriverEvent(int paneType, String selectedItem)
    {
        boolean handlerFound = false;
        for (int i = 0; i < driverList.size() && !handlerFound; i++)
        {
            ProtocolHandler currentHandler = driverList.get(i);
            String[] menuNames = null;
            if (paneType == ProtocolHandler.PANE_TYPE_INCOMING)
            {
                menuNames = currentHandler.getIncomingMenuNames();
            }
            if (paneType == ProtocolHandler.PANE_TYPE_OUTGOING)
            {
                menuNames = currentHandler.getOutgoingMenuNames();
            }
            for (int j = 0; j < menuNames.length && !handlerFound; j++)
            {
                if (menuNames[j].equals(selectedItem))
                {
                    return currentHandler.buildProtocolPane(paneType, selectedItem);
                }
            }
        }
        return null;
    }
}
