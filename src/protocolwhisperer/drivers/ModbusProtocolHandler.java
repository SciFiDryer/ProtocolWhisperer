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
import protocolwhisperer.BridgeEntryContainer;
import protocolwhisperer.BridgeMappingRecord;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */

public class ModbusProtocolHandler implements ProtocolHandler{
    protocolwhisperer.MainFrame parentFrame = null;
    JComboBox incomingDataSelector = null;
    JComboBox outgoingDataSelector = null;
    boolean incomingPanelReady = false;
    JComboBox readTypeSelector = null;
    JPanel incomingDataSettings = null;
    JPanel outgoingPanel = null;
    DriverMenuHandler dmh = null;
    String[] dataTypeMenuNames = new String[] {"Select data type", "Float", "Unsigned Int16", "Unsigned Int32"};
    BridgeEntryContainer parentEntryContainer = null;
    String[] incomingMenuNames = new String[] {"From Modbus Slave (act as master)", "From Modbus Master (act as slave)"};
    String[] outgoingMenuNames = new String[] {"To Modbus Slave (act as master)", "To Modbus Master (act as slave)"};
    public ModbusProtocolHandler()
    {
        
    }
    public String[] getIncomingMenuNames()
    {
        return incomingMenuNames;
    }
    public String[] getOutgoingMenuNames()
    {
        return outgoingMenuNames;
    }
    public ModbusProtocolHandler(DriverMenuHandler aDmh, protocolwhisperer.MainFrame aParentFrame, BridgeEntryContainer aParentEntryContainer, JPanel aIncomingDataSettings, JPanel aOutgoingPanel)
    {
        parentFrame = aParentFrame;
        parentEntryContainer = aParentEntryContainer;
        outgoingPanel = aOutgoingPanel;
        incomingDataSettings = aIncomingDataSettings;
        dmh = aDmh;
        outgoingDataSelector = dmh.outgoingDataSelector;
        
    }
    public void setIncomingSettings(JTable table, ProtocolRecord protocolRecord)
    {
        ModbusProtocolRecord modbusRecord = (ModbusProtocolRecord)protocolRecord;
        if (modbusRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_MASTER)
        {
             table.getModel().setValueAt(modbusRecord.slaveHost, 0, 1);
             table.getModel().setValueAt(modbusRecord.slavePort, 0, 2);
             table.getModel().setValueAt(modbusRecord.node, 0, 3);
             String registerType = "";
             if (modbusRecord.functionCode == 3)
             {
                 registerType = "Holding registers";
             }
             if (modbusRecord.functionCode == 4)
             {
                 registerType = "Input registers";
             }
             table.getModel().setValueAt(registerType, 0, 4);
             table.getModel().setValueAt(modbusRecord.startingRegister, 0, 5);
             String dataType = "";
             if (modbusRecord.formatType == ModbusProtocolRecord.FORMAT_TYPE_FLOAT)
             {
                 dataType = "Read Float";
             }
             if (modbusRecord.formatType == ModbusProtocolRecord.FORMAT_TYPE_UINT_16)
             {
                 dataType = "Read Unsigned Int16";
             }
             if (modbusRecord.formatType == ModbusProtocolRecord.FORMAT_TYPE_UINT_32)
             {
                 dataType = "Read Unsigned Int32";
             }
             table.getModel().setValueAt(dataType, 0, 6);
         }
    }
    public void setOutgoingSettings(ProtocolRecord protocolRecord)
    {
        
    }
    public JTable buildProtocolPane(int paneType, String selectedItem)
    {
        if (paneType == PANE_TYPE_INCOMING)
        {
            return constructDataSettings(incomingDataSettings, selectedItem);
        }
        if (paneType == PANE_TYPE_OUTGOING)
        {
            return constructOutgoingDataSettings(outgoingPanel, selectedItem);
        }
        return null;
    }
    public JTable constructDataSettings(JPanel mainPanel, String selectedItem)
    {
        JTable table = new JTable();
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel();
        if (selectedItem.equals(incomingMenuNames[0]))
        {
            tableModel.addColumn("Tag name");
            tableModel.addColumn("Slave IP");
            tableModel.addColumn("Slave port");
            tableModel.addColumn("Slave ID");
            tableModel.addColumn("Register group");
            tableModel.addColumn("Register");
            tableModel.addColumn("Data type");
            tableModel.addColumn("Byte swap");
            tableModel.addColumn("word swap");

            tableModel.addRow(new Object[] {null,null,502,1,null,null,null,Boolean.FALSE,Boolean.FALSE } );

            table.setModel(tableModel);
            JComboBox registerTypeSelector = new JComboBox();
            DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select register type", "Input registers", "Holding registers"});
            registerTypeSelector.setModel(model);
            DefaultComboBoxModel readModel = new DefaultComboBoxModel(dataTypeMenuNames);
            readTypeSelector = new JComboBox();
            readTypeSelector.setModel(readModel);
            table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(registerTypeSelector));
            table.getColumnModel().getColumn(4).setCellRenderer(table.getDefaultRenderer(JComboBox.class));
            table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(readTypeSelector));
            table.getColumnModel().getColumn(6).setCellRenderer(table.getDefaultRenderer(JComboBox.class));
            table.getColumnModel().getColumn(6).setPreferredWidth(100);
            table.getColumnModel().getColumn(7).setCellEditor(table.getDefaultEditor(Boolean.class));
            table.getColumnModel().getColumn(7).setCellRenderer(table.getDefaultRenderer(Boolean.class));
            table.getColumnModel().getColumn(8).setCellEditor(table.getDefaultEditor(Boolean.class));
            table.getColumnModel().getColumn(8).setCellRenderer(table.getDefaultRenderer(Boolean.class));
            table.getColumnModel().getColumn(0).setPreferredWidth(150);
            table.getColumnModel().getColumn(4).setPreferredWidth(100);
        }
        if (selectedItem.equals(incomingMenuNames[1]))
        {
            tableModel.addColumn("Tag name");
            tableModel.addColumn("Master port");
            tableModel.addColumn("Master ID");
            tableModel.addColumn("Register group");
            tableModel.addColumn("Register");
            tableModel.addColumn("Data type");
            tableModel.addColumn("Byte swap");
            tableModel.addColumn("word swap");

            tableModel.addRow(new Object[] {null,502,1,null,null,null,Boolean.FALSE,Boolean.FALSE } );

            table.setModel(tableModel);
            JComboBox registerTypeSelector = new JComboBox();
            DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select register type", "Input registers", "Holding registers"});
            registerTypeSelector.setModel(model);
            DefaultComboBoxModel readModel = new DefaultComboBoxModel(dataTypeMenuNames);
            readTypeSelector = new JComboBox();
            readTypeSelector.setModel(readModel);
            table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(registerTypeSelector));
            table.getColumnModel().getColumn(3).setCellRenderer(table.getDefaultRenderer(JComboBox.class));
            table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(readTypeSelector));
            table.getColumnModel().getColumn(5).setCellRenderer(table.getDefaultRenderer(JComboBox.class));
            table.getColumnModel().getColumn(5).setPreferredWidth(100);
            table.getColumnModel().getColumn(6).setCellEditor(table.getDefaultEditor(Boolean.class));
            table.getColumnModel().getColumn(6).setCellRenderer(table.getDefaultRenderer(Boolean.class));
            table.getColumnModel().getColumn(7).setCellEditor(table.getDefaultEditor(Boolean.class));
            table.getColumnModel().getColumn(7).setCellRenderer(table.getDefaultRenderer(Boolean.class));
            table.getColumnModel().getColumn(0).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(100);
        }
        JPanel tablePane = new JPanel(new java.awt.BorderLayout());
        
        tablePane.add(table.getTableHeader(), java.awt.BorderLayout.PAGE_START);
        tablePane.add(table, java.awt.BorderLayout.CENTER);
        JPanel tableParentPane = new JPanel();
        tableParentPane.setLayout(new java.awt.FlowLayout());
        tableParentPane.add(tablePane);
        JButton deleteButton = new JButton("Delete");
        tableParentPane.add(deleteButton);
        JPanel driverPane = new JPanel();
        driverPane.setLayout(new BoxLayout(driverPane, BoxLayout.Y_AXIS));
        driverPane.add(new JSeparator());
        driverPane.add(new JLabel("Modbus Master driver"));
        
        driverPane.add(tableParentPane);
        JPanel driverParentPane = new JPanel();
        driverParentPane.setLayout(new java.awt.FlowLayout());
        driverParentPane.add(driverPane);
        mainPanel.add(driverParentPane);
        BridgeEntryContainer entryContainer = new BridgeEntryContainer(selectedItem, table);
        parentFrame.getManager().dataSourceList.add(entryContainer);
        deleteButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                mainPanel.remove(driverParentPane);
                parentFrame.getManager().dataSourceList.remove(entryContainer);
            }
        });
        
        parentFrame.repaint();
        return table;
    }
    public JTable constructOutgoingDataSettings(JPanel mainPanel, String selectedItem)
    {
        JTable table = new JTable();
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel();
        if (selectedItem.equals(outgoingMenuNames[0]))
        {
            tableModel.addColumn("Tag name");
            tableModel.addColumn("Slave IP");
            tableModel.addColumn("Slave port");
            tableModel.addColumn("Slave ID");
            tableModel.addColumn("Register group");
            tableModel.addColumn("Register");
            tableModel.addColumn("Data type");
            tableModel.addColumn("Byte swap");
            tableModel.addColumn("word swap");

            tableModel.addRow(new Object[] {null,null,502,1,null,null,null,Boolean.FALSE,Boolean.FALSE } );

            table.setModel(tableModel);
            JComboBox registerTypeSelector = new JComboBox();
            DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select register type", "Input registers", "Holding registers"});
            registerTypeSelector.setModel(model);
            DefaultComboBoxModel readModel = new DefaultComboBoxModel(dataTypeMenuNames);
            readTypeSelector = new JComboBox();
            readTypeSelector.setModel(readModel);
            table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(registerTypeSelector));
            table.getColumnModel().getColumn(4).setCellRenderer(table.getDefaultRenderer(JComboBox.class));
            table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(readTypeSelector));
            table.getColumnModel().getColumn(6).setCellRenderer(table.getDefaultRenderer(JComboBox.class));
            table.getColumnModel().getColumn(6).setPreferredWidth(100);
            table.getColumnModel().getColumn(7).setCellEditor(table.getDefaultEditor(Boolean.class));
            table.getColumnModel().getColumn(7).setCellRenderer(table.getDefaultRenderer(Boolean.class));
            table.getColumnModel().getColumn(8).setCellEditor(table.getDefaultEditor(Boolean.class));
            table.getColumnModel().getColumn(8).setCellRenderer(table.getDefaultRenderer(Boolean.class));
            table.getColumnModel().getColumn(0).setPreferredWidth(150);
            table.getColumnModel().getColumn(4).setPreferredWidth(100);
        }
        if (selectedItem.equals(outgoingMenuNames[1]))
        {
            tableModel.addColumn("Tag name");
            tableModel.addColumn("Master port");
            tableModel.addColumn("Master ID");
            tableModel.addColumn("Register group");
            tableModel.addColumn("Register");
            tableModel.addColumn("Data type");
            tableModel.addColumn("Byte swap");
            tableModel.addColumn("word swap");

            tableModel.addRow(new Object[] {null,502,1,null,null,null,Boolean.FALSE,Boolean.FALSE } );

            table.setModel(tableModel);
            JComboBox registerTypeSelector = new JComboBox();
            DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select register type", "Input registers", "Holding registers"});
            registerTypeSelector.setModel(model);
            DefaultComboBoxModel readModel = new DefaultComboBoxModel(new String[] {"Select read type", "Block read", "Read Float", "Read Unsigned Int16", "Read Unsigned Int32"});
            readTypeSelector = new JComboBox();
            readTypeSelector.setModel(readModel);
            table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(registerTypeSelector));
            table.getColumnModel().getColumn(3).setCellRenderer(table.getDefaultRenderer(JComboBox.class));
            table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(readTypeSelector));
            table.getColumnModel().getColumn(5).setCellRenderer(table.getDefaultRenderer(JComboBox.class));
            table.getColumnModel().getColumn(5).setPreferredWidth(100);
            table.getColumnModel().getColumn(6).setCellEditor(table.getDefaultEditor(Boolean.class));
            table.getColumnModel().getColumn(6).setCellRenderer(table.getDefaultRenderer(Boolean.class));
            table.getColumnModel().getColumn(7).setCellEditor(table.getDefaultEditor(Boolean.class));
            table.getColumnModel().getColumn(7).setCellRenderer(table.getDefaultRenderer(Boolean.class));
            table.getColumnModel().getColumn(0).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(100);
        }
        JPanel tablePane = new JPanel(new java.awt.BorderLayout());
        
        tablePane.add(table.getTableHeader(), java.awt.BorderLayout.PAGE_START);
        tablePane.add(table, java.awt.BorderLayout.CENTER);
        JPanel tableParentPane = new JPanel();
        tableParentPane.setLayout(new java.awt.FlowLayout());
        tableParentPane.add(tablePane);
        JButton deleteButton = new JButton("Delete");
        tableParentPane.add(deleteButton);
        JPanel driverPane = new JPanel();
        driverPane.setLayout(new BoxLayout(driverPane, BoxLayout.Y_AXIS));
        driverPane.add(new JSeparator());
        driverPane.add(new JLabel("Modbus Slave driver"));
        
        driverPane.add(tableParentPane);
        JPanel driverParentPane = new JPanel();
        driverParentPane.setLayout(new java.awt.FlowLayout());
        driverParentPane.add(driverPane);
        mainPanel.add(driverParentPane);
        BridgeEntryContainer entryContainer = new BridgeEntryContainer(selectedItem, table);
        parentFrame.getManager().dataDestinationList.add(entryContainer);
        deleteButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                mainPanel.remove(driverParentPane);
                parentFrame.getManager().dataDestinationList.remove(entryContainer);
            }
        });
        
        parentFrame.repaint();
        return table;
    }
    public ProtocolRecord getIncomingProtocolRecord(BridgeEntryContainer container)
    {
        ProtocolRecord incomingProtocolRecord = null;
        String tag = "";
        int type = 0;
        int format = 0;
        String slaveHost = null;
        int port = 0;
        int functionCode = 0;
        int formatPos = 7;
        int node = 0;
        int register = 0;
        boolean wordSwap = false;
        boolean byteSwap = false;
        tag = container.table.getValueAt(0, 0).toString();
        if (container.driverSelection.equals(incomingMenuNames[0]))
        {
            type = ModbusProtocolRecord.PROTOCOL_TYPE_MASTER;
            slaveHost = container.table.getValueAt(0, 1).toString();
            port = Integer.parseInt(container.table.getValueAt(0, 2).toString());
            if (container.table.getValueAt(0, 4).toString().equals("Input registers"))
            {
                functionCode = 4;
            }
            if (container.table.getValueAt(0, 4).toString().equals("Holding registers"))
            {
                functionCode = 3;
            }
            node = Integer.parseInt(container.table.getValueAt(0, 3).toString());
            register = Integer.parseInt(container.table.getValueAt(0, 5).toString());
            wordSwap = ((Boolean)container.table.getValueAt(0, 8)).booleanValue();
            byteSwap = ((Boolean)container.table.getValueAt(0, 7)).booleanValue();
        
        }
        if (container.driverSelection.equals(incomingMenuNames[1]))
        {
            formatPos = 5;
            type = ModbusProtocolRecord.PROTOCOL_TYPE_SLAVE;
            port = Integer.parseInt(container.table.getValueAt(0, 1).toString());
            if (container.table.getValueAt(0, 3).toString().equals("Input registers"))
            {
                functionCode = 4;
            }
            if (container.table.getValueAt(0, 3).toString().equals("Holding registers"))
            {
                functionCode = 3;
            }
            node = Integer.parseInt(container.table.getValueAt(0, 2).toString());
            register = Integer.parseInt(container.table.getValueAt(0, 4).toString());
            wordSwap = ((Boolean)container.table.getValueAt(0, 7)).booleanValue();
            byteSwap = ((Boolean)container.table.getValueAt(0, 6)).booleanValue();
        }
        
        System.out.println(container.table.getValueAt(0, formatPos).toString());
        if (container.table.getValueAt(0, formatPos).toString().equals(dataTypeMenuNames[1]))
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_FLOAT;
        }
        else if (container.table.getValueAt(0, formatPos).toString().equals(dataTypeMenuNames[2]))
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_UINT_16;
        }
        else if (container.table.getValueAt(0, formatPos).toString().equals(dataTypeMenuNames[3]))
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_UINT_32;
        }
        incomingProtocolRecord = new ModbusProtocolRecord(tag, type, slaveHost, port, node, format, functionCode, register, 2, wordSwap, byteSwap);
        
        return incomingProtocolRecord;
    }
    public ProtocolRecord getOutgoingProtocolRecord(BridgeEntryContainer container)
    {
        ProtocolRecord outgoingProtocolRecord = null;
        String tag = "";
        int type = 0;
        int format = 0;
        String slaveHost = null;
        int port = 0;
        int functionCode = 0;
        int formatPos = 6;
        int node = 0;
        int register = 0;
        boolean wordSwap = false;
        boolean byteSwap = false;
        tag = container.table.getValueAt(0, 0).toString();
        if (container.driverSelection.equals(outgoingMenuNames[0]))
        {
            type = ModbusProtocolRecord.PROTOCOL_TYPE_MASTER;
            slaveHost = container.table.getValueAt(0, 1).toString();
            port = Integer.parseInt(container.table.getValueAt(0, 2).toString());
            if (container.table.getValueAt(0, 4).toString().equals("Input registers"))
            {
                functionCode = 4;
            }
            if (container.table.getValueAt(0, 4).toString().equals("Holding registers"))
            {
                functionCode = 3;
            }
            node = Integer.parseInt(container.table.getValueAt(0, 3).toString());
            register = Integer.parseInt(container.table.getValueAt(0, 5).toString());
            wordSwap = ((Boolean)container.table.getValueAt(0, 8)).booleanValue();
            byteSwap = ((Boolean)container.table.getValueAt(0, 7)).booleanValue();
        
        }
        if (container.driverSelection.equals(outgoingMenuNames[1]))
        {
            formatPos = 5;
            type = ModbusProtocolRecord.PROTOCOL_TYPE_SLAVE;
            port = Integer.parseInt(container.table.getValueAt(0, 1).toString());
            if (container.table.getValueAt(0, 3).toString().equals("Input registers"))
            {
                functionCode = 4;
            }
            if (container.table.getValueAt(0, 3).toString().equals("Holding registers"))
            {
                functionCode = 3;
            }
            node = Integer.parseInt(container.table.getValueAt(0, 2).toString());
            register = Integer.parseInt(container.table.getValueAt(0, 4).toString());
            wordSwap = ((Boolean)container.table.getValueAt(0, 7)).booleanValue();
            byteSwap = ((Boolean)container.table.getValueAt(0, 6)).booleanValue();
        }
        
        System.out.println(container.table.getValueAt(0, formatPos).toString());
        if (container.table.getValueAt(0, formatPos).toString().equals(dataTypeMenuNames[1]))
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_FLOAT;
        }
        else if (container.table.getValueAt(0, formatPos).toString().equals(dataTypeMenuNames[2]))
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_UINT_16;
        }
        else if (container.table.getValueAt(0, formatPos).toString().equals(dataTypeMenuNames[3]))
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_UINT_32;
        }
        outgoingProtocolRecord = new ModbusProtocolRecord(tag, type, slaveHost, port, node, format, functionCode, register, 2, wordSwap, byteSwap);
        
        return outgoingProtocolRecord;
    }
}
