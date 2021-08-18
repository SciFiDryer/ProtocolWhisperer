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
import javax.swing.*;
import java.util.*;
import protocolwhisperer.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.filechooser.*;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class SQLConfigFrame extends javax.swing.JFrame {

    /**
     * Creates new form ModbusConfigFrame
     */
    static String[] dataTypeMenuNames = new String[] {"Select data type", "Float", "Unsigned Int16", "Unsigned Int32"};
    SQLDatalogRecord currentRecord = null;
    ArrayList<PointMapper> pointGuiRecords = new ArrayList();
    BridgeManager manager = null;
    public SQLConfigFrame(SQLDatalogRecord aCurrentRecord, BridgeManager aManager) {
        initComponents();
        manager = aManager;
        currentRecord = aCurrentRecord;
        if (currentRecord.driverName.equals("sqlite"))
        {
            configPane1.setVisible(false);
            configPane2.setVisible(false);
        }
        if (currentRecord.driverName.equals("mysql"))
        {
            filePane.setVisible(false);
        }
        if (currentRecord.configured)
        {
            buildPointRecords(currentRecord.points);
            hostIpField.setText(currentRecord.sqlHost);
            databaseField.setText(currentRecord.databaseName);
            usernameField.setText(currentRecord.sqlUser);
            passwordField.setText(currentRecord.sqlPassword);
            filePathField.setText(currentRecord.filePath);
            try
            {
                logIntervalField.setText((currentRecord.logInterval / 1000) + "");
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
    public void buildPointRecords(ArrayList<DatalogPoint> points)
    {
        for (int i = 0; i < points.size(); i++)
        {
            DatalogPoint currentRecord = (DatalogPoint)points.get(i);
            buildPointRecord(currentRecord);
        }
    }
    public void buildPointRecord(DatalogPoint currentPoint)
    {
        JPanel currentTagPane = new JPanel();
        final java.awt.Component tagField;
        if (currentPoint.configured)
        {
            tagField = manager.getOutgoingRecordTags(currentPoint.tagRecord.guid);
        }
        else
        {
            tagField = manager.getOutgoingRecordTags("");
        }
        currentTagPane.add(tagField);
        JButton deleteButton = new JButton("Delete");
        currentTagPane.add(deleteButton);
        PointMapper pm = new PointMapper()
        {
            public DatalogPoint mapPointRecord()
            {
                
                String targetGuid = manager.getGuidFromIndex(((JComboBox)(tagField)).getSelectedIndex());
                DatalogPoint point = new DatalogPoint(manager.getIncomingRecordByGuid(targetGuid));
                return point;
            }
        };
        deleteButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                tagRecordPane.remove(currentTagPane);
                pointGuiRecords.remove(pm);
                revalidate();
                repaint();
            }
        });
        tagRecordPane.add(currentTagPane);
        pointGuiRecords.add(pm);
    }
    public void mapPoints()
    {
        currentRecord.points.clear();
        for (int i = 0; i < pointGuiRecords.size(); i++)
        {
            currentRecord.points.add(pointGuiRecords.get(i).mapPointRecord());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        layoutPane = new javax.swing.JPanel();
        configPane1 = new javax.swing.JPanel();
        hostIpLabel = new javax.swing.JLabel();
        hostIpField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        databaseField = new javax.swing.JTextField();
        configPane2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        filePane = new javax.swing.JPanel();
        filePathField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        logIntervalField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        addTagPane = new javax.swing.JPanel();
        addPointButton = new javax.swing.JButton();
        headerPane = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        tagParentPane = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        tagRecordPane = new javax.swing.JPanel();
        okPane = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modbus Config");

        layoutPane.setLayout(new javax.swing.BoxLayout(layoutPane, javax.swing.BoxLayout.Y_AXIS));

        hostIpLabel.setText("Server host:");
        configPane1.add(hostIpLabel);

        hostIpField.setColumns(10);
        hostIpField.setToolTipText("");
        configPane1.add(hostIpField);

        jLabel2.setText("Database Name:");
        configPane1.add(jLabel2);

        databaseField.setColumns(10);
        databaseField.setToolTipText("");
        configPane1.add(databaseField);

        layoutPane.add(configPane1);

        jLabel7.setText("Username");
        configPane2.add(jLabel7);

        usernameField.setColumns(10);
        configPane2.add(usernameField);

        jLabel1.setText("Password");
        configPane2.add(jLabel1);

        passwordField.setColumns(10);
        configPane2.add(passwordField);

        layoutPane.add(configPane2);

        filePathField.setColumns(20);
        filePathField.setText("No file selected");
        filePane.add(filePathField);

        browseButton.setText("Browse");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        filePane.add(browseButton);

        layoutPane.add(filePane);

        jLabel3.setText("Log interval");
        jPanel3.add(jLabel3);

        logIntervalField.setColumns(3);
        logIntervalField.setText("60");
        jPanel3.add(logIntervalField);

        jLabel4.setText("sec");
        jPanel3.add(jLabel4);

        layoutPane.add(jPanel3);

        addPointButton.setText("Add point");
        addPointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPointButtonActionPerformed(evt);
            }
        });
        addTagPane.add(addPointButton);

        layoutPane.add(addTagPane);

        jLabel8.setText("Tag");
        headerPane.add(jLabel8);

        layoutPane.add(headerPane);

        getContentPane().add(layoutPane, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        tagRecordPane.setLayout(new javax.swing.BoxLayout(tagRecordPane, javax.swing.BoxLayout.Y_AXIS));
        jPanel2.add(tagRecordPane, java.awt.BorderLayout.NORTH);

        tagParentPane.setViewportView(jPanel2);

        getContentPane().add(tagParentPane, java.awt.BorderLayout.CENTER);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        okPane.add(okButton);

        getContentPane().add(okPane, java.awt.BorderLayout.SOUTH);

        setBounds(0, 0, 402, 325);
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        currentRecord.sqlHost = hostIpField.getText();
        currentRecord.databaseName = databaseField.getText();
        currentRecord.sqlUser = usernameField.getText();
        currentRecord.sqlPassword = new String(passwordField.getPassword());
        try
        {
            currentRecord.logInterval = Integer.parseInt(logIntervalField.getText()) * 1000;
        }
        catch (Exception e)
        {
            if (ProtocolWhisperer.debug)
            {
                e.printStackTrace();
            }
        }
        currentRecord.configured = true;
        mapPoints();
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void addPointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPointButtonActionPerformed
        DatalogPoint point = new DatalogPoint();
        currentRecord.points.add(point);
        buildPointRecord(point);
        revalidate();
        repaint();
    }//GEN-LAST:event_addPointButtonActionPerformed

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("db file", "db");
        chooser.setFileFilter(filter);
        int status = chooser.showSaveDialog(this);
        if (status == JFileChooser.APPROVE_OPTION)
        {
            java.io.File f = chooser.getSelectedFile();
            String filename = f.getParent() + File.separator + f.getName();
            if (filename.length() > 3 && !filename.substring(filename.length()-3).equalsIgnoreCase(".db"))
            {
                filename = f.getParent() + File.separator + f.getName() + ".db";
            }
            
            currentRecord.filePath = filename;
            filePathField.setText(currentRecord.filePath);
        }
    }//GEN-LAST:event_browseButtonActionPerformed
    public interface PointMapper
    {
        public DatalogPoint mapPointRecord();
    }
    
    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPointButton;
    private javax.swing.JPanel addTagPane;
    private javax.swing.JButton browseButton;
    private javax.swing.JPanel configPane1;
    private javax.swing.JPanel configPane2;
    private javax.swing.JTextField databaseField;
    private javax.swing.JPanel filePane;
    private javax.swing.JTextField filePathField;
    private javax.swing.JPanel headerPane;
    private javax.swing.JTextField hostIpField;
    private javax.swing.JLabel hostIpLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel layoutPane;
    private javax.swing.JTextField logIntervalField;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel okPane;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JScrollPane tagParentPane;
    private javax.swing.JPanel tagRecordPane;
    private javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
