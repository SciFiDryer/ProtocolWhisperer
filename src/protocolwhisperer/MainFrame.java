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
import protocolwhisperer.drivers.ModbusProtocolHandler;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import javax.swing.filechooser.*;
import java.beans.*;
import java.net.*;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Creates new form BridgeFrame
     */
    public BridgeManager manager = null;
    JComboBox incomingDataSelector = null;
    JComboBox outgoingDataSelector = null;
    JComboBox tagSelectMenu = null;
    JComboBox datalogSelector = null;
    public URLClassLoader classLoader = null;
    public MainFrame(BridgeManager aManager, URLClassLoader aClassLoader) {
        manager = aManager;
        classLoader = aClassLoader;
        initComponents();
        tagSelectMenu = manager.getOutgoingRecordTags("");
        incomingDataSelector = new JComboBox();
        outgoingDataSelector = new JComboBox();
        datalogSelector = new JComboBox();
        incomingDataSelectorPane.add(incomingDataSelector);
        outgoingDataSelectorPane.add(outgoingDataSelector);
        datalogSelectorPane.add(datalogSelector);
        protocolwhisperer.drivers.DriverMenuHandler dmh = new protocolwhisperer.drivers.DriverMenuHandler(incomingDataSelector, outgoingDataSelector, datalogSelector, this);
        manager.dmh = dmh;
    }
    public MainFrame()
    {
    }
    public void setManager(BridgeManager aManager)
    {
        manager = aManager;
    }
    public BridgeManager getManager()
    {
        return manager;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        generalPane = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        startBridgeButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        restIntervalField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel12 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        enableRedundancy = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        tagSelectPane = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        watchdogTimerField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        incomingTabPane = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        incomingDataSelectorPane = new javax.swing.JPanel();
        addDataSource = new javax.swing.JButton();
        incomingDataParent = new javax.swing.JPanel();
        incomingDataPane = new javax.swing.JPanel();
        outgoingTabPane = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        outgoingDataPaneParent = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        outgoingDataSelectorPane = new javax.swing.JPanel();
        addDataDestination = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        outgoingDataPane = new javax.swing.JPanel();
        datalogPane = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        datalogParentPane = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        datalogSelectorPane = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        datalogRecordPane = new javax.swing.JPanel();
        scriptPane = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        scriptTextArea = new javax.swing.JTextArea();
        viewSourcesPane = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        valuesTable = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        saveConfig = new javax.swing.JMenuItem();
        loadConfig = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        generalPane.setLayout(new javax.swing.BoxLayout(generalPane, javax.swing.BoxLayout.Y_AXIS));

        jLabel9.setText("Bridge control");
        jPanel11.add(jLabel9);

        generalPane.add(jPanel11);

        jPanel1.add(jSeparator1);

        startBridgeButton.setText("Start Bridge");
        startBridgeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBridgeButtonActionPerformed(evt);
            }
        });
        jPanel1.add(startBridgeButton);

        generalPane.add(jPanel1);

        jLabel2.setText("Rest interval");
        jPanel3.add(jLabel2);

        restIntervalField.setColumns(4);
        restIntervalField.setText("1000");
        restIntervalField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restIntervalFieldActionPerformed(evt);
            }
        });
        jPanel3.add(restIntervalField);

        jLabel1.setText("ms");
        jPanel3.add(jLabel1);

        generalPane.add(jPanel3);
        generalPane.add(jSeparator2);

        jLabel10.setText("Redundancy options");
        jPanel12.add(jLabel10);

        generalPane.add(jPanel12);

        enableRedundancy.setText("Enable redundancy");
        jPanel7.add(enableRedundancy);

        generalPane.add(jPanel7);

        jLabel8.setText("Redundancy tag");
        jPanel10.add(jLabel8);
        jPanel10.add(tagSelectPane);

        jLabel5.setText("Redundancy timer");
        jPanel10.add(jLabel5);

        watchdogTimerField.setColumns(4);
        watchdogTimerField.setText("5000");
        jPanel10.add(watchdogTimerField);

        jLabel6.setText("ms");
        jPanel10.add(jLabel6);

        generalPane.add(jPanel10);

        jTabbedPane1.addTab("General", generalPane);

        incomingTabPane.setLayout(new javax.swing.BoxLayout(incomingTabPane, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jLabel3.setText("Driver");
        jPanel4.add(jLabel3);
        jPanel4.add(incomingDataSelectorPane);

        addDataSource.setText("Add");
        addDataSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDataSourceActionPerformed(evt);
            }
        });
        jPanel4.add(addDataSource);

        jPanel2.add(jPanel4);

        incomingDataParent.setLayout(new java.awt.BorderLayout());

        incomingDataPane.setLayout(new javax.swing.BoxLayout(incomingDataPane, javax.swing.BoxLayout.Y_AXIS));
        incomingDataParent.add(incomingDataPane, java.awt.BorderLayout.NORTH);

        jPanel2.add(incomingDataParent);

        jScrollPane1.setViewportView(jPanel2);

        incomingTabPane.add(jScrollPane1);

        jTabbedPane1.addTab("Data Sources", incomingTabPane);

        outgoingTabPane.setLayout(new javax.swing.BoxLayout(outgoingTabPane, javax.swing.BoxLayout.LINE_AXIS));

        outgoingDataPaneParent.setLayout(new javax.swing.BoxLayout(outgoingDataPaneParent, javax.swing.BoxLayout.Y_AXIS));

        jLabel4.setText("Driver");
        jPanel5.add(jLabel4);
        jPanel5.add(outgoingDataSelectorPane);

        addDataDestination.setText("Add");
        addDataDestination.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDataDestinationActionPerformed(evt);
            }
        });
        jPanel5.add(addDataDestination);

        outgoingDataPaneParent.add(jPanel5);

        jPanel6.setLayout(new java.awt.BorderLayout());

        outgoingDataPane.setLayout(new javax.swing.BoxLayout(outgoingDataPane, javax.swing.BoxLayout.Y_AXIS));
        jPanel6.add(outgoingDataPane, java.awt.BorderLayout.NORTH);

        outgoingDataPaneParent.add(jPanel6);

        jScrollPane2.setViewportView(outgoingDataPaneParent);

        outgoingTabPane.add(jScrollPane2);

        jTabbedPane1.addTab("Data Destinations", outgoingTabPane);

        datalogPane.setLayout(new javax.swing.BoxLayout(datalogPane, javax.swing.BoxLayout.Y_AXIS));

        datalogParentPane.setLayout(new javax.swing.BoxLayout(datalogParentPane, javax.swing.BoxLayout.Y_AXIS));

        jLabel7.setText("Driver");
        jPanel8.add(jLabel7);
        jPanel8.add(datalogSelectorPane);

        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton1);

        datalogParentPane.add(jPanel8);

        jPanel9.setLayout(new java.awt.BorderLayout());

        datalogRecordPane.setLayout(new javax.swing.BoxLayout(datalogRecordPane, javax.swing.BoxLayout.Y_AXIS));
        jPanel9.add(datalogRecordPane, java.awt.BorderLayout.NORTH);

        datalogParentPane.add(jPanel9);

        jScrollPane5.setViewportView(datalogParentPane);

        datalogPane.add(jScrollPane5);

        jTabbedPane1.addTab("Datalogging", datalogPane);

        scriptPane.setLayout(new javax.swing.BoxLayout(scriptPane, javax.swing.BoxLayout.LINE_AXIS));

        scriptTextArea.setColumns(20);
        scriptTextArea.setRows(5);
        scriptTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                scriptTextAreaKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(scriptTextArea);

        scriptPane.add(jScrollPane4);

        jTabbedPane1.addTab("Scripting", scriptPane);

        valuesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tag", "Value"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(valuesTable);

        viewSourcesPane.add(jScrollPane3);

        jTabbedPane1.addTab("View Source Values", viewSourcesPane);

        getContentPane().add(jTabbedPane1);

        jMenu1.setText("File");

        saveConfig.setText("Save config to file");
        saveConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConfigActionPerformed(evt);
            }
        });
        jMenu1.add(saveConfig);

        loadConfig.setText("Load config from file");
        loadConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadConfigActionPerformed(evt);
            }
        });
        jMenu1.add(loadConfig);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        setBounds(0, 0, 533, 339);
    }// </editor-fold>//GEN-END:initComponents

    public void updateBridgeOptions()
    {
        manager.options.redundancyEnabled = enableRedundancy.isSelected();
        if (manager.options.redundancyEnabled)
        {
            manager.options.redundancyTimeout = Integer.parseInt(watchdogTimerField.getText());
            manager.options.watchdogGuid = manager.getGuidFromIndex(tagSelectMenu.getSelectedIndex());
        }
        manager.options.restInterval = Integer.parseInt(restIntervalField.getText());
    }
    private void startBridgeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startBridgeButtonActionPerformed
        if (!manager.isRunning)
        {
            try
            {
                manager.options.restInterval = Integer.parseInt(restIntervalField.getText());
                updateBridgeOptions();
            }
            catch (Exception e)
            {
                if (ProtocolWhisperer.debug)
                {
                    e.printStackTrace();
                }
                manager.options.restInterval = 1000;
            }
            manager.startBridge();
            startBridgeButton.setText("Stop bridge");
        }
        else
        {
            manager.shutdown();
            startBridgeButton.setText("Start bridge");
        }
    }//GEN-LAST:event_startBridgeButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (manager.isRunning)
        {
            manager.shutdown();
        }
    }//GEN-LAST:event_formWindowClosing

    private void saveConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveConfigActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CFG file", "cfg");
        chooser.setFileFilter(filter);
        int status = chooser.showSaveDialog(this);
        if (status == JFileChooser.APPROVE_OPTION)
        {
            java.io.File f = chooser.getSelectedFile();
            String filename = f.getName();
            if (filename.length() < 4 || !filename.substring(filename.length()-4).equalsIgnoreCase(".cfg"))
            {
                f = new File(f.getParent() + File.separator + f.getName() + ".cfg");
            }
            try
            {
                updateBridgeOptions();
                XMLEncoder xmle = new XMLEncoder(new FileOutputStream(f));
                xmle.writeObject(manager.options);
                xmle.writeObject(manager.dataSourceRecords);
                xmle.writeObject(manager.dataDestinationRecords);
                xmle.writeObject(manager.datalogRecords);
                xmle.close();
            }
            catch (Exception e)
            {
                if (ProtocolWhisperer.debug)
                {
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_saveConfigActionPerformed

    private void loadConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadConfigActionPerformed
        if (classLoader != null)
        {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CFG file", "cfg");
        chooser.setFileFilter(filter);
        int status = chooser.showOpenDialog(this);
        if (status == JFileChooser.APPROVE_OPTION)
        {
            java.io.File f = chooser.getSelectedFile();
            String filename = f.getName();
            if (filename.length() > 4 && !filename.substring(filename.length()-4).equalsIgnoreCase(".cfg"))
            {
                f = new File(f.getParent() + File.separator + f.getName() + ".cfg");
            }
            try
            {
                manager.loadConfig(f);
                manager.restoreGuiFromFile();
            }
            catch (Exception e)
            {
                if (ProtocolWhisperer.debug)
                {
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_loadConfigActionPerformed

    private void addDataSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDataSourceActionPerformed
        manager.dmh.dispatchDriverEvent(protocolwhisperer.drivers.ProtocolHandler.PANE_TYPE_INCOMING, incomingDataSelector.getSelectedItem().toString());
    }//GEN-LAST:event_addDataSourceActionPerformed

    private void addDataDestinationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDataDestinationActionPerformed
        manager.dmh.dispatchDriverEvent(protocolwhisperer.drivers.ProtocolHandler.PANE_TYPE_OUTGOING, outgoingDataSelector.getSelectedItem().toString());
    }//GEN-LAST:event_addDataDestinationActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if (jTabbedPane1.getSelectedComponent() == generalPane)
        {
            tagSelectPane.removeAll();
            tagSelectMenu = manager.getOutgoingRecordTags(manager.options.watchdogGuid);
            tagSelectPane.add(tagSelectMenu);
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void scriptTextAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scriptTextAreaKeyReleased
        manager.options.scriptContent = scriptTextArea.getText();
    }//GEN-LAST:event_scriptTextAreaKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        manager.dmh.addDatalogDriver(datalogSelector.getSelectedItem().toString());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void restIntervalFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restIntervalFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_restIntervalFieldActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDataDestination;
    private javax.swing.JButton addDataSource;
    private javax.swing.JPanel datalogPane;
    private javax.swing.JPanel datalogParentPane;
    public javax.swing.JPanel datalogRecordPane;
    private javax.swing.JPanel datalogSelectorPane;
    public javax.swing.JCheckBox enableRedundancy;
    private javax.swing.JPanel generalPane;
    public javax.swing.JPanel incomingDataPane;
    public javax.swing.JPanel incomingDataParent;
    private javax.swing.JPanel incomingDataSelectorPane;
    private javax.swing.JPanel incomingTabPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenuItem loadConfig;
    public javax.swing.JPanel outgoingDataPane;
    private javax.swing.JPanel outgoingDataPaneParent;
    private javax.swing.JPanel outgoingDataSelectorPane;
    private javax.swing.JPanel outgoingTabPane;
    public javax.swing.JTextField restIntervalField;
    private javax.swing.JMenuItem saveConfig;
    private javax.swing.JPanel scriptPane;
    public javax.swing.JTextArea scriptTextArea;
    private javax.swing.JButton startBridgeButton;
    public javax.swing.JPanel tagSelectPane;
    public javax.swing.JTable valuesTable;
    private javax.swing.JPanel viewSourcesPane;
    public javax.swing.JTextField watchdogTimerField;
    // End of variables declaration//GEN-END:variables
}
