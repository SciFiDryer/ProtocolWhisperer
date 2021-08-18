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
import java.io.*;
import java.net.*;
import protocolwhisperer.*;
import java.sql.*;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */

public class DriverMenuHandler implements ActionListener, java.io.Serializable{
    JComboBox incomingDataSelector = null;
    public JComboBox outgoingDataSelector = null;
    JComboBox datalogSelector = null;
    MainFrame parentFrame = null;
    BridgeEntryContainer parentEntryContainer = null;
    ArrayList<ProtocolHandler> driverList = new ArrayList();
    ArrayList<DatalogHandler> datalogHandlers = new ArrayList();
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
    public DriverMenuHandler(JComboBox aIncomingDataSelector, JComboBox aOutgoingDataSelector, JComboBox aDatalogSelector, MainFrame aParentFrame)
    {
        incomingDataSelector = aIncomingDataSelector;
        outgoingDataSelector = aOutgoingDataSelector;
        parentFrame = aParentFrame;
        datalogSelector = aDatalogSelector;
        
        loadDrivers();
        loadDatalogDrivers();
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
            File pluginDir = new File(workingDir.getPath() + File.separator + "plugins");
            
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
        ServiceLoader<ProtocolDriver> driverLoader = ServiceLoader.load(ProtocolDriver.class);
        Iterator drivers = driverLoader.iterator();
        while (drivers.hasNext())
        {
            try
            {
                ProtocolDriver currentDriver = (ProtocolDriver)drivers.next();
                parentFrame.manager.getDriverList().add(currentDriver);
                ProtocolHandler currentHandler = (ProtocolHandler)currentDriver.getProtocolHandlerClass().getDeclaredConstructor().newInstance();
                driverList.add(currentHandler);
            }
            catch(Exception e)
            {
                if (ProtocolWhisperer.debug)
                {
                    e.printStackTrace();
                }
            }
        }
        
        driverList.add(new ModbusProtocolHandler());
        driverList.add(new CIPProtocolHandler());
        driverList.add(new StaticTagHandler());
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
        parentFrame.manager.getDatalogDrivers().add(new SQLDatalogDriver());
        for (int i = 0; i < parentFrame.manager.getDatalogDrivers().size(); i++)
        {
            try
            {
                DatalogDriver currentDriver = parentFrame.manager.getDatalogDrivers().get(i);
                DatalogHandler currentHandler = (DatalogHandler)currentDriver.getDatalogHandlerClass().getDeclaredConstructor().newInstance();
                datalogHandlers.add(currentHandler);
            }
            catch (Exception e)
            {
                if (ProtocolWhisperer.debug)
                {
                    e.printStackTrace();
                }
            }
        }
        ArrayList<String> menuItems = new ArrayList();
        menuItems.add("Select");
        for (int i = 0; i < datalogHandlers.size(); i++)
        {
            String[] menuNames = datalogHandlers.get(i).getMenuNames();
            for (int j = 0; j < menuNames.length; j++)
            {
                menuItems.add(menuNames[j]);
            }
        }
        DefaultComboBoxModel model = new DefaultComboBoxModel(menuItems.toArray());
        datalogSelector.setModel(model);
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
        ProtocolHandler currentHandler = getProtocolHandler(currentRecord.getProtocolHandlerClass());
        configButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                currentHandler.configure(currentRecord, parentFrame.manager);
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
    public void constructDatalogRecord(DatalogRecord currentRecord)
    {
        
        JPanel currentPanel = new JPanel();
        currentPanel.add(new JLabel(currentRecord.selectedItem));
        
        JButton configButton = new JButton("Configure");
        DatalogHandler currentHandler = getDatalogHandler(currentRecord.getDatalogHandlerClass());
        configButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                currentHandler.configure(currentRecord, parentFrame.manager);
            }
        });
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parentFrame.datalogRecordPane.remove(currentPanel);
                parentFrame.manager.datalogRecords.remove(currentRecord);
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
        currentPanel.add(configButton);
        currentPanel.add(deleteButton);
        parentFrame.datalogRecordPane.add(currentPanel);
        parentFrame.repaint();
    }
    public DatalogHandler getDatalogHandler(String selectedItem)
    {
        for (int i = 0; i < datalogHandlers.size(); i++)
        {
            DatalogHandler currentHandler = datalogHandlers.get(i);
            String[] menuNames = currentHandler.getMenuNames();
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
    public DatalogHandler getDatalogHandler(Class handlerClass)
    {
        for (int i = 0; i < datalogHandlers.size(); i++)
        {
            DatalogHandler currentHandler = datalogHandlers.get(i);
            if (handlerClass.isInstance(currentHandler))
            {
                return currentHandler;
            }  
        }
        return null;
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
    public void addDatalogDriver(String selectedItem)
    {
        DatalogHandler currentHandler = getDatalogHandler(selectedItem);
        DatalogRecord currentRecord = currentHandler.getNewDatalogRecord(selectedItem);
        parentFrame.manager.datalogRecords.add(currentRecord);
        constructDatalogRecord(currentRecord);
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

