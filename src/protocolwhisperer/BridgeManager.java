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
import protocolwhisperer.drivers.*;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.master.*;
import com.intelligt.modbus.jlibmodbus.msg.base.*;
import com.intelligt.modbus.jlibmodbus.msg.response.*;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class BridgeManager{
    public ArrayList<BridgeEntryContainer> dataSourceList = new ArrayList();
    public ArrayList<ProtocolRecord> dataSourceRecords = new ArrayList();
    public ArrayList<BridgeEntryContainer> dataDestinationList = new ArrayList();
    public ArrayList<ProtocolRecord> dataDestinationRecords = new ArrayList();
    ArrayList<ProtocolDriver> driverList = new ArrayList();
    boolean firstRun = true;
    int restTime = 1000;
    boolean isRunning = false;
    BridgeThread bridgeThread = null;
    public DriverMenuHandler dmh = null;
    MainFrame bridgeFrame = null;
    boolean headless = false;
    public BridgeManager(boolean aHeadless, String fileName)
    {
        headless = aHeadless;
        if (!headless)
        {
            try
            {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
                {
                    if ("Windows".equals(info.getName()))
                    {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            }
            catch (ClassNotFoundException ex)
            {
                java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            catch (InstantiationException ex)
            {
                java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            catch (IllegalAccessException ex)
            {
                java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            catch (javax.swing.UnsupportedLookAndFeelException ex)
            {
                java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            bridgeFrame = new MainFrame(this);
            bridgeFrame.setVisible(true);
        }
        driverList.add(new ModbusProtocolDriver(this));
        driverList.add(new CIPProtocolDriver(this));
        if (headless)
        {
            loadConfig(fileName);
            startBridge();
        }
    }
    public void loadConfig(String fileName)
    {
        try
        {
            XMLDecoder xmld = new XMLDecoder(new FileInputStream(fileName));
            dataSourceRecords = (ArrayList<ProtocolRecord>)xmld.readObject();
            xmld.close();
        }
        catch (Exception e)
        {
            if (ProtocolWhisperer.debug)
            {
                e.printStackTrace();
            }
        }
    }
    public void setBridgeMapList(ArrayList aBridgeMapList)
    {
        dataSourceList = aBridgeMapList;
    }
    public ArrayList<ProtocolRecord> getDataSourceRecords()
    {
        return dataSourceRecords;
    }
    public void setMappingRecords(ArrayList aMappingRecords)
    {
        dataSourceRecords = aMappingRecords;
    }
    public ArrayList<BridgeEntryContainer> getBridgeMapList()
    {
        return dataSourceList;
    }
    public void setDriverList(ArrayList aDriverList)
    {
        driverList = aDriverList;
    }
    public ArrayList<ProtocolDriver> getDriverList()
    {
        return driverList;
    }
    public void runBridge()
    {
        if (firstRun)
        {
            for (int i = 0; i < driverList.size(); i++)
            {
                ProtocolDriver currentDriver = driverList.get(i);
                currentDriver.driverInit();
            }
        }
        //get incoming records
        for (int i = 0; i < driverList.size(); i++)
        {
            ProtocolDriver currentDriver = driverList.get(i);
            currentDriver.getIncomingRecords();
        }
        if (!headless)
        {
            javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel)bridgeFrame.valuesTable.getModel();
            while (model.getRowCount() > 0)
            {
                model.removeRow(0);
            }
            for (int i = 0; i < dataSourceRecords.size(); i++)
            {
                ProtocolRecord pr = dataSourceRecords.get(i);
                model.addRow(new Object[] {pr.getTag(), pr.getValue()});
            }
            
        }
        //get incoming records
        for (int i = 0; i < driverList.size(); i++)
        {
            ProtocolDriver currentDriver = driverList.get(i);
            currentDriver.mapIncomingValues();
        }
        for (int i = 0; i < dataDestinationRecords.size(); i++)
        {
            ProtocolRecord currentRecord = dataDestinationRecords.get(i);
            ProtocolRecord incomingRecord = getIncomingRecordByTag(currentRecord.getTag());
            if (incomingRecord != null)
            {
                currentRecord.setValue(incomingRecord.getValue());
            }
        }
        for (int i = 0; i < driverList.size(); i++)
        {
            ProtocolDriver currentDriver = driverList.get(i);
            currentDriver.sendOutgoingRecords();
        }
        firstRun = false;
    }
    public ProtocolRecord getIncomingRecordByTag(String tag)
    {
        for (int i = 0; i < dataSourceRecords.size(); i++)
        {
            if (dataSourceRecords.get(i).getTag().equals(tag))
            {
                return dataSourceRecords.get(i);
            }
        }
        return null;
    }
    public void restoreGuiFromFile()
    {
        for (int i = 0; i < dataSourceRecords.size(); i++)
        {
            if (dataSourceRecords.get(i) instanceof ModbusProtocolRecord)
            {
                JTable table = dmh.dispatchDriverEvent(protocolwhisperer.drivers.ProtocolHandler.PANE_TYPE_INCOMING, "From Modbus Slave (act as master)");
                for (int j = 0; j < dmh.getDriverList().size(); j++)
                {
                    if (dmh.getDriverList().get(j) instanceof ModbusProtocolHandler)
                    {
                        ((ModbusProtocolHandler)(dmh.getDriverList().get(j))).setIncomingSettings(table, (ModbusProtocolRecord)dataSourceRecords.get(i));
                    }
                }
            }
            /*
            if (dataSourceRecords.get(i).incomingRecord instanceof CIPProtocolRecord)
            {
                bridgeFrame.addMapping();
                for (int j = 0; j < dmh.getDriverList().size(); j++)
                {
                    if (dmh.getDriverList().get(j) instanceof CIPProtocolHandler)
                    {
                        ((CIPProtocolHandler)(dmh.getDriverList().get(j))).setIncomingSettings((CIPProtocolRecord)dataSourceRecords.get(i).incomingRecord);
                    }
                }
            }
            if (dataSourceRecords.get(i).outgoingRecord instanceof ModbusProtocolRecord)
            {
                for (int j = 0; j < dmh.getDriverList().size(); j++)
                {
                    if (dmh.getDriverList().get(j) instanceof ModbusProtocolHandler)
                    {
                        ((ModbusProtocolHandler)(dmh.getDriverList().get(j))).setOutgoingSettings((ModbusProtocolRecord)dataSourceRecords.get(i).outgoingRecord);
                    }
                }
            }
            if (dataSourceRecords.get(i).outgoingRecord instanceof CIPProtocolRecord)
            {
                for (int j = 0; j < dmh.getDriverList().size(); j++)
                {
                    if (dmh.getDriverList().get(j) instanceof CIPProtocolHandler)
                    {
                        ((CIPProtocolHandler)(dmh.getDriverList().get(j))).setOutgoingSettings((CIPProtocolRecord)dataSourceRecords.get(i).outgoingRecord);
                    }
                }
            }*/
        }
    }
    public ProtocolHandler getHandlerForDriver(String driverSelection)
    {
        for (int i = 0; i < dmh.getDriverList().size(); i++)
        {
            ProtocolHandler currentHandler = dmh.getDriverList().get(i);
            String[] menuNames = currentHandler.getIncomingMenuNames();
            for (int j = 0; j < menuNames.length; j++)
            {
                if (driverSelection.equals(menuNames[j]))
                {
                    return currentHandler;
                }
            }
            menuNames = currentHandler.getOutgoingMenuNames();
            for (int j = 0; j < menuNames.length; j++)
            {
                if (driverSelection.equals(menuNames[j]))
                {
                    return currentHandler;
                }
            }
        }
        return null;
    }
    public void constructSettingsFromGui()
    {
        dataSourceRecords.clear();
        for (int i = 0; i < dataSourceList.size(); i++)
        {
            ProtocolRecord pr = getHandlerForDriver(dataSourceList.get(i).driverSelection).getIncomingProtocolRecord(dataSourceList.get(i));
            dataSourceRecords.add(pr);
        }
        dataDestinationRecords.clear();
        for (int i = 0; i < dataDestinationList.size(); i++)
        {
            ProtocolRecord pr = getHandlerForDriver(dataDestinationList.get(i).driverSelection).getOutgoingProtocolRecord(dataDestinationList.get(i));
            dataDestinationRecords.add(pr);
        }
    }
    public void startBridge()
    {
        bridgeThread = new BridgeThread(this);
        bridgeThread.start();
    }
    public void shutdown()
    {
        isRunning = false;
        firstRun = true;
        bridgeThread.interrupt();
        for (int i = 0; i < driverList.size(); i++)
        {
            driverList.get(i).shutdown();
        }
    }
    
    
}
