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
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class ModbusConfigFrame extends javax.swing.JFrame {

    /**
     * Creates new form ModbusConfigFrame
     */
    static String[] dataTypeMenuNames = new String[] {"Select data type", "Float", "Unsigned Int16", "Unsigned Int32"};
    ModbusProtocolRecord currentRecord = null;
    ArrayList<TagMapper> tagGuiRecords = new ArrayList();
    public ModbusConfigFrame(ModbusProtocolRecord aCurrentRecord) {
        initComponents();
        currentRecord = aCurrentRecord;
        
        if (currentRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_SLAVE)
        {
            hostIpField.setVisible(false);
            hostIpLabel.setVisible(false);
        }
        if (currentRecord.configured)
        {
            if (currentRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_MASTER)
            {
                hostIpField.setText(currentRecord.slaveHost);
            }
            portField.setText(currentRecord.slavePort + "");
            idField.setText(currentRecord.node + "");
            buildTagRecords(currentRecord.tagRecords); 
        }
    }
    public void buildTagRecords(ArrayList<TagRecord> tagRecords)
    {
        for (int i = 0; i < tagRecords.size(); i++)
        {
            ModbusTagRecord currentRecord = (ModbusTagRecord)tagRecords.get(i);
            buildTagRecord(currentRecord);
        }
    }
    public void buildTagRecord(ModbusTagRecord currentRecord)
    {
        JPanel currentTagPane = new JPanel();
        JTextField tagField = new JTextField(10);
        JComboBox functionCodeSelector = new JComboBox(new String[] {"Select register type", "Holding registers", "InputRegisters"});
        JTextField registerField = new JTextField(4);
        JComboBox dataTypeSelector = new JComboBox();
        JCheckBox wordSwapCheckbox = new JCheckBox("Word swap");
        JCheckBox byteSwapCheckbox = new JCheckBox("Byte swap");
        dataTypeSelector.setModel(new DefaultComboBoxModel(dataTypeMenuNames));
        if (currentRecord.configured)
        {
            tagField.setText(currentRecord.tag);
            if (currentRecord.functionCode == 3)
            {
                functionCodeSelector.setSelectedIndex(1);
            }
            if (currentRecord.functionCode == 4)
            {
                functionCodeSelector.setSelectedIndex(2);
            }
            registerField.setText(currentRecord.startingRegister + "");
            dataTypeSelector.setSelectedItem(ModbusProtocolHandler.getMenuItemFromFormat(currentRecord.formatType));
            wordSwapCheckbox.setSelected(currentRecord.wordSwap);
            byteSwapCheckbox.setSelected(currentRecord.byteSwap);
        }
        currentTagPane.add(tagField);
        currentTagPane.add(functionCodeSelector);
        currentTagPane.add(registerField);
        currentTagPane.add(dataTypeSelector);
        currentTagPane.add(byteSwapCheckbox);
        currentTagPane.add(wordSwapCheckbox);
        tagRecordPane.add(currentTagPane);
        tagGuiRecords.add(new TagMapper()
        {
            public ModbusTagRecord mapTagRecord()
            {
                ModbusTagRecord outputRecord = new ModbusTagRecord();
                outputRecord.tag = tagField.getText();
                outputRecord.startingRegister = Integer.parseInt(registerField.getText());
                outputRecord.formatType = ModbusProtocolHandler.getFormatFromMenuItem(dataTypeSelector.getSelectedItem().toString());
                outputRecord.quantity = 2;
                if (outputRecord.formatType == ModbusTagRecord.FORMAT_TYPE_UINT_16)
                {
                    outputRecord.quantity = 1;
                }
                if (functionCodeSelector.getSelectedIndex() == 1)
                {
                    outputRecord.functionCode = 3;
                }
                if (functionCodeSelector.getSelectedIndex() == 2)
                {
                    outputRecord.functionCode = 4;
                }
                outputRecord.byteSwap = byteSwapCheckbox.isSelected();
                outputRecord.wordSwap = wordSwapCheckbox.isSelected();
                outputRecord.configured = true;
                return outputRecord;
            }
        });
    }
    public void mapTagRecords()
    {
        currentRecord.tagRecords.clear();
        for (int i = 0; i < tagGuiRecords.size(); i++)
        {
            currentRecord.tagRecords.add(tagGuiRecords.get(i).mapTagRecord());
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
        configPane = new javax.swing.JPanel();
        hostIpLabel = new javax.swing.JLabel();
        hostIpField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        portField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        idField = new javax.swing.JTextField();
        addTagPane = new javax.swing.JPanel();
        addTagButton = new javax.swing.JButton();
        headerPane = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jLabel1 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(70, 0), new java.awt.Dimension(70, 0), new java.awt.Dimension(10, 32767));
        jLabel4 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        jLabel5 = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        jLabel6 = new javax.swing.JLabel();
        tagParentPane = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        tagRecordPane = new javax.swing.JPanel();
        okPane = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modbus Config");

        layoutPane.setLayout(new javax.swing.BoxLayout(layoutPane, javax.swing.BoxLayout.Y_AXIS));

        hostIpLabel.setText("Host IP:");
        configPane.add(hostIpLabel);

        hostIpField.setColumns(10);
        hostIpField.setToolTipText("");
        configPane.add(hostIpField);

        jLabel2.setText("Port:");
        configPane.add(jLabel2);

        portField.setColumns(3);
        portField.setText("502");
        portField.setToolTipText("");
        configPane.add(portField);

        jLabel3.setText("ID:");
        configPane.add(jLabel3);

        idField.setColumns(3);
        idField.setText("1");
        idField.setToolTipText("");
        configPane.add(idField);

        layoutPane.add(configPane);

        addTagButton.setText("Add tag");
        addTagButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTagButtonActionPerformed(evt);
            }
        });
        addTagPane.add(addTagButton);

        layoutPane.add(addTagPane);

        headerPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        headerPane.add(filler3);

        jLabel1.setText("Tag");
        headerPane.add(jLabel1);
        headerPane.add(filler1);

        jLabel4.setText("Register group");
        headerPane.add(jLabel4);
        headerPane.add(filler2);

        jLabel5.setText("Register");
        headerPane.add(jLabel5);
        headerPane.add(filler4);

        jLabel6.setText("Data Type");
        headerPane.add(jLabel6);

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

        setBounds(0, 0, 591, 325);
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        currentRecord.slaveHost = hostIpField.getText();
        currentRecord.slavePort = Integer.parseInt(portField.getText());
        currentRecord.node = Integer.parseInt(idField.getText());
        currentRecord.configured = true;
        mapTagRecords();
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void addTagButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTagButtonActionPerformed
        ModbusTagRecord tagRecord = new ModbusTagRecord();
        currentRecord.tagRecords.add(tagRecord);
        buildTagRecord(tagRecord);
        revalidate();
        repaint();
    }//GEN-LAST:event_addTagButtonActionPerformed
    public interface TagMapper
    {
        public ModbusTagRecord mapTagRecord();
    }
    
    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addTagButton;
    private javax.swing.JPanel addTagPane;
    private javax.swing.JPanel configPane;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JPanel headerPane;
    private javax.swing.JTextField hostIpField;
    private javax.swing.JLabel hostIpLabel;
    private javax.swing.JTextField idField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel layoutPane;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel okPane;
    private javax.swing.JTextField portField;
    private javax.swing.JScrollPane tagParentPane;
    private javax.swing.JPanel tagRecordPane;
    // End of variables declaration//GEN-END:variables
}

