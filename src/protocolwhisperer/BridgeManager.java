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
import java.io.*;
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
            loadConfig(new File(fileName));
            startBridge();
        }
    }
    public void loadConfig(File f)
    {
        try
        {
            XMLDecoder xmld = new XMLDecoder(new FileInputStream(f));
            dataSourceRecords.clear();
            dataDestinationRecords.clear();
            dataSourceRecords = (ArrayList<ProtocolRecord>)xmld.readObject();
            dataDestinationRecords = (ArrayList<ProtocolRecord>)xmld.readObject();
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
                for (int j = 0; j < pr.tagRecords.size(); j++)
                {
                    TagRecord tr = pr.tagRecords.get(j);
                    model.addRow(new Object[] {tr.tag, tr.getValue()});
                }
                
            }
            
        }
        
        for (int i = 0; i < driverList.size(); i++)
        {
            ProtocolDriver currentDriver = driverList.get(i);
            currentDriver.mapIncomingValues();
        }
        for (int i = 0; i < dataDestinationRecords.size(); i++)
        {
            ProtocolRecord currentRecord = dataDestinationRecords.get(i);
            for (int j = 0; j < currentRecord.tagRecords.size(); j++)
            {
                TagRecord currentTagRecord = currentRecord.tagRecords.get(j);
                TagRecord incomingRecord = getIncomingRecordByTag(currentTagRecord.tag);
                if (incomingRecord != null)
                {
                    currentTagRecord.setValue(incomingRecord.getValue());
                }
            }
        }
        for (int i = 0; i < driverList.size(); i++)
        {
            ProtocolDriver currentDriver = driverList.get(i);
            currentDriver.sendOutgoingRecords();
        }
        firstRun = false;
    }
    public TagRecord getIncomingRecordByTag(String tag)
    {
        for (int i = 0; i < dataSourceRecords.size(); i++)
        {
            ProtocolRecord currentRecord = dataSourceRecords.get(i);
            for (int j = 0; j < currentRecord.tagRecords.size(); j++)
            {
                if (currentRecord.tagRecords.get(j).tag.equals(tag))
                {
                    return currentRecord.tagRecords.get(j);
                }
            }
        }
        return null;
    }
    public void restoreGuiFromFile()
    {
        bridgeFrame.incomingDataPane.removeAll();
        for (int i = 0; i < dataSourceRecords.size(); i++)
        {
            dmh.constructGuiRecord(dataSourceRecords.get(i));
        }
        bridgeFrame.outgoingDataPane.removeAll();
        for (int i = 0; i < dataDestinationRecords.size(); i++)
        {
            dmh.constructGuiRecord(dataDestinationRecords.get(i));
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
