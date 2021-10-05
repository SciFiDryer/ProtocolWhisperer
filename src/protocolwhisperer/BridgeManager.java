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
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.script.*;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class BridgeManager{
    public ArrayList<BridgeEntryContainer> dataSourceList = new ArrayList();
    public ArrayList<ProtocolRecord> dataSourceRecords = new ArrayList();
    public ArrayList<BridgeEntryContainer> dataDestinationList = new ArrayList();
    public ArrayList<ProtocolRecord> dataDestinationRecords = new ArrayList();
    public ArrayList<DatalogRecord> datalogRecords = new ArrayList();
    public BridgeOptions options = new BridgeOptions();
    ArrayList<ProtocolDriver> driverList = new ArrayList();
    ArrayList<DatalogDriver> datalogDrivers = new ArrayList();
    ArrayList<ProtocolHandler> handlerList = new ArrayList();
    boolean firstRun = true;
    int restTime = 1000;
    boolean isRunning = false;
    BridgeThread bridgeThread = null;
    public DriverMenuHandler dmh = null;
    MainFrame bridgeFrame = null;
    boolean headless = false;
    long redundancyTimerStart = 0;
    public URLClassLoader classLoader = null;
    public BridgeManager(boolean aHeadless, String fileName)
    {
        headless = aHeadless;
        driverList.add(new ModbusProtocolDriver());
        driverList.add(new CIPProtocolDriver());
        driverList.add(new DriveParameterProtocolDriver());
        driverList.add(new StaticTagProtocolDriver());
        loadDrivers();
        loadDatalogDrivers();
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
            bridgeFrame = new MainFrame(this, classLoader);
            bridgeFrame.setVisible(true);
        }
        if (headless)
        {
            loadConfig(new File(fileName));
            startBridge();
        }
    }
    public JComboBox getOutgoingRecordTags(String selectedTag)
    {
        ArrayList<String> menu = new ArrayList();
        int selectedIndex = 0;
        for (int i = 0; i < dataSourceRecords.size(); i++)
        {
            ProtocolRecord currentRecord = dataSourceRecords.get(i);
            for (int j = 0; j < currentRecord.tagRecords.size(); j++)
            {
                menu.add(currentRecord.tagRecords.get(j).tag);
                if (selectedTag.equals(currentRecord.tagRecords.get(j).tag))
                {
                    selectedIndex = menu.size() - 1;
                }
            }
        }
        JComboBox menuBox = new JComboBox(menu.toArray());
        if (menu.size() > 0)
        {
            menuBox.setSelectedIndex(selectedIndex);
        }
        return menuBox;
    }
    /*public String getGuidFromIndex(int index)
    {
        int counter = 0;
        for (int i = 0; i < dataSourceRecords.size(); i++)
        {
            ProtocolRecord currentRecord = dataSourceRecords.get(i);
            for (int j = 0; j < currentRecord.tagRecords.size(); j++)
            {
                if (index == counter)
                {
                    return currentRecord.tagRecords.get(j).guid;
                }
                counter++;
            }
        }
        return "";
    }*/
    public void loadConfig(File f)
    {
        try
        {
            XMLDecoder xmld = new XMLDecoder(new FileInputStream(f));
            dataSourceRecords.clear();
            dataDestinationRecords.clear();
            datalogRecords.clear();
            options = (BridgeOptions)xmld.readObject();
            dataSourceRecords = (ArrayList<ProtocolRecord>)xmld.readObject();
            dataDestinationRecords = (ArrayList<ProtocolRecord>)xmld.readObject();
            datalogRecords = (ArrayList<DatalogRecord>)xmld.readObject();
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
    public ArrayList<ProtocolHandler> getHandlerList()
    {
        return handlerList;
    }
    public ArrayList<DatalogDriver> getDatalogDrivers()
    {
        return datalogDrivers;
    }
    public void runBridge()
    {
        if (firstRun)
        {
            //do first run init and build respective protocol record lists

            for (int i = 0; i < dataSourceRecords.size(); i++)
            {
                ProtocolRecord pr = dataSourceRecords.get(i);
                processProtocolRecord(pr);
            }
            for (int i = 0; i < dataDestinationRecords.size(); i++)
            {
                ProtocolRecord pr = dataDestinationRecords.get(i);
                processProtocolRecord(pr);
            }
            for (int i = 0; i < driverList.size(); i++)
            {
                ProtocolDriver currentDriver = driverList.get(i);
                currentDriver.driverInit();
            }
            for (int i = 0; i < datalogRecords.size(); i++)
            {
                DatalogRecord dr = datalogRecords.get(i);
                processDatalogRecord(dr);
            }
        }
        //get incoming values
        for (int i = 0; i < driverList.size(); i++)
        {
            ProtocolDriver currentDriver = driverList.get(i);
            currentDriver.getIncomingRecords();
            currentDriver.mapIncomingValues();
        }
        runScripting();
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
        //check if redundancy watchdog has elapsed
        if (options.redundancyEnabled)
        {
            if (getIncomingRecordByTag(options.watchdogTag).getValue() == 1)
            {
                if (redundancyTimerStart == 0)
                {
                    redundancyTimerStart = System.currentTimeMillis();
                }
            }
            else
            {
                redundancyTimerStart = 0;
            }
        }
        if (!options.redundancyEnabled || (redundancyTimerStart > 0 && System.currentTimeMillis() - redundancyTimerStart > options.redundancyTimeout))
        {
            for (int i = 0; i < dataDestinationRecords.size(); i++)
            {
                ProtocolRecord currentRecord = dataDestinationRecords.get(i);
                boolean recordChanged = false;
                for (int j = 0; j < currentRecord.tagRecords.size(); j++)
                {
                    TagRecord currentTagRecord = currentRecord.tagRecords.get(j);
                    TagRecord incomingRecord = getIncomingRecordByTag(currentTagRecord.tag);
                    if (incomingRecord != null)
                    {
                        if (currentTagRecord.lastChangedTime == 0 || currentTagRecord.lastChangedTime != incomingRecord.lastChangedTime)
                        {
                            recordChanged = true;
                        }
                        currentTagRecord.lastChangedTime = incomingRecord.lastChangedTime;
                        currentTagRecord.setValue(incomingRecord.getValue());
                    }
                }
                currentRecord.recordChanged = recordChanged;
            }
            for (int i = 0; i < driverList.size(); i++)
            {
                ProtocolDriver currentDriver = driverList.get(i);
                currentDriver.sendOutgoingRecords();
            }
        }
        firstRun = false;
    }
    public void runDatalog()
    {
        for (int i = 0; i < datalogDrivers.size(); i++)
        {
            DatalogDriver currentDriver = datalogDrivers.get(i);
            currentDriver.logPoints();
        }
    }
    public void runScripting()
    {
        if (options.scriptContent.length() > 0)
        {
            ScriptEngineManager engineManager = new ScriptEngineManager();
            ScriptEngine engine = engineManager.getEngineByName("JavaScript");
            String builtin = "function tagByName(name)"+
                    "{"+
                        "for (i = 0; i < tags.size(); i++)"+
                        "{"+
                            "if (tags.get(i).tag == name)"+
                            "{"+
                                "return tags.get(i);"+
                            "}"+
                        "}"+
                    "}";
            ArrayList<TagRecord> tags = new ArrayList<TagRecord>();
            for (int i = 0; i < dataSourceRecords.size(); i++)
            {
                ProtocolRecord currentProtocolRecord = dataSourceRecords.get(i);
                for (int j = 0; j < currentProtocolRecord.tagRecords.size(); j++)
                {
                    TagRecord currentTagRecord = currentProtocolRecord.tagRecords.get(j);
                    tags.add(currentTagRecord);
                }
            }
            try
            {
                engine.put("tags", tags);
                engine.eval(builtin);
                engine.eval(options.scriptContent);
            }
            catch(Exception e)
            {
                if (ProtocolWhisperer.debug)
                {
                    e.printStackTrace();
                }
            }
        }
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
        bridgeFrame.enableRedundancy.setSelected(options.redundancyEnabled);
        bridgeFrame.restIntervalField.setText(options.restInterval + "");
        bridgeFrame.tagSelectMenu = getOutgoingRecordTags(options.watchdogTag);
        bridgeFrame.scriptTextArea.setText(options.scriptContent);
        bridgeFrame.tagSelectPane.removeAll();
        bridgeFrame.tagSelectPane.add(bridgeFrame.tagSelectMenu);
        bridgeFrame.repaint();
        bridgeFrame.watchdogTimerField.setText(options.redundancyTimeout + "");
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
        bridgeFrame.datalogRecordPane.removeAll();
        for (int i = 0; i < datalogRecords.size(); i++)
        {
            dmh.constructDatalogRecord(datalogRecords.get(i));
        }
    }
    public void processProtocolRecord(ProtocolRecord currentRecord)
    {
        for (int i = 0; i < driverList.size(); i++)
        {
            ProtocolDriver currentDriver = driverList.get(i);
            if (currentDriver.getProtocolRecordClass().isInstance(currentRecord))
            {
                currentDriver.storeProtocolRecord(currentRecord);
            }
        }
    }
    public void processDatalogRecord(DatalogRecord currentRecord)
    {
        for (int i = 0; i < datalogDrivers.size(); i++)
        {
            DatalogDriver currentDriver = datalogDrivers.get(i);
            if (currentDriver.getDatalogRecordClass().isInstance(currentRecord))
            {
                currentDriver.storeDatalogRecord(currentRecord);
            }
        }
    }
    public ProtocolHandler getHandlerForDriver(String driverSelection)
    {
        for (int i = 0; i < handlerList.size(); i++)
        {
            ProtocolHandler currentHandler = handlerList.get(i);
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
    public class DatalogThread extends Thread
    {
        public synchronized void run()
        {
            isRunning = true;
            if (ProtocolWhisperer.debug)
            {
                System.out.println("Starting Datalog thread");
            }
            while (isRunning)
            {
                try
                {
                    runDatalog();
                    wait(1000);
                }
                catch (InterruptedException e)
                {
                    if (protocolwhisperer.ProtocolWhisperer.debug)
                    {
                        e.printStackTrace();
                    }
                }
            }
            if (ProtocolWhisperer.debug)
            {
                System.out.println("Datalog thread stopped");
            }
        }
    }
    public void startBridge()
    {
        bridgeThread = new BridgeThread(this);
        bridgeThread.start();
        new DatalogThread().start();
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
    public void loadDrivers()
    {
        try
        {
            File workingDir = new File(DriverMenuHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            if (workingDir.isFile() || workingDir.getPath().endsWith(".jar"))
            {
                workingDir = new File(workingDir.getParent());
            }
            File pluginDir = new File(URLDecoder.decode(workingDir.getPath(), "UTF-8") + File.separator + "plugins");
            if (pluginDir.exists() && pluginDir.isDirectory())
            {
                File[] fileList = pluginDir.listFiles();
                URL[] jarList = new URL[fileList.length];
                for (int i = 0; i < fileList.length; i++)
                {
                    jarList[i] = fileList[i].toURI().toURL();
                }
                URLClassLoader classLoader = new URLClassLoader(jarList, ClassLoader.getSystemClassLoader());
                Thread.currentThread().setContextClassLoader(classLoader);
            }
        }
        catch (Exception e)
        {
            if (ProtocolWhisperer.debug)
            {
                e.printStackTrace();
            }
        }
        //add builtin handlers
        for (int i = 0; i < driverList.size(); i++)
        {
            try
            {
                handlerList.add((ProtocolHandler)driverList.get(i).getProtocolHandlerClass().getDeclaredConstructor().newInstance());
            }
            catch(Exception e)
            {
                if (ProtocolWhisperer.debug)
                {
                    e.printStackTrace();
                }
            }
        }
        ServiceLoader<ProtocolDriver> driverLoader = ServiceLoader.load(ProtocolDriver.class);
        Iterator drivers = driverLoader.iterator();
        while (drivers.hasNext())
        {
            try
            {
                ProtocolDriver currentDriver = (ProtocolDriver)drivers.next();
                driverList.add(currentDriver);
                ProtocolHandler currentHandler = (ProtocolHandler)currentDriver.getProtocolHandlerClass().getDeclaredConstructor().newInstance();
                handlerList.add(currentHandler);
            }
            catch(Exception e)
            {
                if (ProtocolWhisperer.debug)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    public void loadDatalogDrivers()
    {
        ServiceLoader<Driver> jdbcLoader = ServiceLoader.load(Driver.class);
        Iterator drivers = jdbcLoader.iterator();
        while (drivers.hasNext())
        {
            try
            {
                Driver loadedDriver = (Driver)drivers.next();
                DriverManager.registerDriver(new DriverBroker(loadedDriver));
            }
            catch(Exception e)
            {
                if (ProtocolWhisperer.debug)
                {
                    e.printStackTrace();
                }
            }
        }
        datalogDrivers.add(new SQLDatalogDriver());
    }
    class DriverBroker implements Driver
    {
        Driver driver = null;
        public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException
        {
            return this.driver.getParentLogger();
        }
        public DriverBroker(Driver aDriver)
        {
            this.driver = aDriver;
        }
        public boolean acceptsURL(String u) throws SQLException
        {
		return this.driver.acceptsURL(u);
	}
	public Connection connect(String s, Properties prop) throws SQLException
        {
		return this.driver.connect(s, prop);
	}
	public int getMajorVersion()
        {
		return this.driver.getMajorVersion();
	}
	public int getMinorVersion()
        {
		return this.driver.getMinorVersion();
	}
	public DriverPropertyInfo[] getPropertyInfo(String s, Properties prop) throws SQLException
        {
		return this.driver.getPropertyInfo(s, prop);
	}
	public boolean jdbcCompliant()
        {
		return this.driver.jdbcCompliant();
	}
    }
}

