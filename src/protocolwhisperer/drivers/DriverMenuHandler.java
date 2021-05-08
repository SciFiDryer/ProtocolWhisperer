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
import javax.swing.table.*;
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
    public DriverMenuHandler(JComboBox aIncomingDataSelector, JComboBox aOutgoingDataSelector, MainFrame aParentFrame)
    {
        incomingDataSelector = aIncomingDataSelector;
        outgoingDataSelector = aOutgoingDataSelector;
        parentFrame = aParentFrame;
        
        
        loadDrivers();
    }
    public void loadDrivers()
    {
        driverList.add(new ModbusProtocolHandler());
        driverList.add(new CIPProtocolHandler());
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
    public void constructGuiRecord(ProtocolRecord currentRecord)
    {
        final JPanel subjectPane;
        final ArrayList subjectRecords;
        if (currentRecord.type == ProtocolRecord.RECORD_TYPE_INCOMING)
        {
            subjectPane = parentFrame.incomingDataPane;
            subjectRecords = parentFrame.manager.dataSourceRecords;
        }
        else
        {
            subjectPane = parentFrame.outgoingDataPane;
            subjectRecords = parentFrame.manager.dataDestinationRecords;
        }
        JPanel currentPanel = new JPanel();
        currentPanel.add(new JLabel(currentRecord.selectedItem));
        
        JButton configButton = new JButton("Configure");
        ProtocolHandler currentHandler = getProtocolHandler(currentRecord.protocolHandler);
        configButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                currentHandler.configure(currentRecord);
            }
        });
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                subjectPane.remove(currentPanel);
                subjectRecords.remove(currentRecord);
            }
        });
        currentPanel.add(configButton);
        currentPanel.add(deleteButton);
        subjectPane.add(currentPanel);
        parentFrame.repaint();
    }
    public ProtocolHandler getProtocolHandler(String selectedItem)
    {
        for (int i = 0; i < driverList.size(); i++)
        {
            ProtocolHandler currentHandler = driverList.get(i);
            String[] menuNames = currentHandler.getIncomingMenuNames();
            
            for (int j = 0; j < menuNames.length; j++)
            {
                if (menuNames[j].equals(selectedItem))
                {
                    return currentHandler;
                }
            }
            menuNames = currentHandler.getOutgoingMenuNames();
            for (int j = 0; j < menuNames.length; j++)
            {
                if (menuNames[j].equals(selectedItem))
                {
                    return currentHandler;
                }
            }
        }
        return null;
    }
    public ProtocolHandler getProtocolHandler(Class handlerClass)
    {
        for (int i = 0; i < driverList.size(); i++)
        {
            ProtocolHandler currentHandler = driverList.get(i);
            if (handlerClass.isInstance(currentHandler))
            {
                return currentHandler;
            }  
        }
        return null;
    }
    public void dispatchDriverEvent(int paneType, String selectedItem)
    {
        ProtocolHandler currentHandler = getProtocolHandler(selectedItem);
        ProtocolRecord currentRecord = currentHandler.getNewProtocolRecord(paneType, selectedItem);
        if (currentRecord.type == ProtocolRecord.RECORD_TYPE_INCOMING)
        {
            parentFrame.manager.dataSourceRecords.add(currentRecord);
        }
        if (currentRecord.type == ProtocolRecord.RECORD_TYPE_OUTGOING)
        {
            parentFrame.manager.dataDestinationRecords.add(currentRecord);
        }
        constructGuiRecord(currentRecord);
    }
}

