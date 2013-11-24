/*
 * Copyright 2013 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.gui;

import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import publy.data.settings.GeneralSettings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class GeneralSettingsPanel extends javax.swing.JPanel {

    private GeneralSettings settings;

    /**
     * Empty constructor, for use in the NetBeans GUI editor.
     */
    public GeneralSettingsPanel() {
        settings = new GeneralSettings();
        initComponents();
        applyStyles();
        populateValues();
    }

    /**
     * Creates new form GeneralSettingsPanel
     */
    public GeneralSettingsPanel(GeneralSettings settings) {
        this.settings = settings;
        initComponents();
        applyStyles();
        populateValues();
    }
    
    private void applyStyles() {
        UIStyles.applyHeaderStyle(yourNameLabel, authorLabel, numLabel, titleFirstLabel);
    }

    private void populateValues() {
        // Your Name
        List<String> myNames = settings.getMyNames();
        StringBuilder sb = new StringBuilder();
        
        for (String name : myNames) {
            sb.append(';');
            sb.append(name);
        }
        
        sb.deleteCharAt(0); // Delete the first semicolon
        yourNameTextField.setText(sb.toString());
        
        // Author info
        switch (settings.getNameDisplay()) {
            case FULL:
                fullFirstNameRadioButton.setSelected(true);
                break;
            case ABBREVIATED:
                abbrFirstNameRadioButton.setSelected(true);
                break;
            case NONE:
                noFirstNameRadioButton.setSelected(true);
                break;
            default:
                throw new AssertionError("Unknown name display: " + settings.getNameDisplay());
        }

        reverseNamesCheckBox.setSelected(settings.isReverseNames());
        reverseNamesCheckBox.setEnabled(settings.getNameDisplay() != GeneralSettings.NameDisplay.NONE);
        
        listOnlyCoauthorsCheckBox.setSelected(!settings.isListAllAuthors()); // Negation, since the meaning is opposite

        // Title first
        titleFirstCheckBox.setSelected(settings.isTitleFirst());

        // Numbering
        switch (settings.getNumbering()) {
            case NONE:
                numNoneRadioButton.setSelected(true);
                break;
            case LOCAL:
                numLocalRadioButton.setSelected(true);
                break;
            case GLOBAL:
                numGlobalRadioButton.setSelected(true);
                break;
            default:
                throw new AssertionError("Unknown numbering: " + settings.getNumbering());
        }

        reverseNumberingCheckBox.setSelected(settings.isReverseNumbering());
        reverseNumberingCheckBox.setEnabled(settings.getNumbering() != GeneralSettings.Numbering.NONE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        numGroup = new javax.swing.ButtonGroup();
        firstNameGroup = new javax.swing.ButtonGroup();
        numNoneRadioButton = new javax.swing.JRadioButton();
        numGlobalRadioButton = new javax.swing.JRadioButton();
        numLocalRadioButton = new javax.swing.JRadioButton();
        authorLabel = new javax.swing.JLabel();
        authorSeparator = new javax.swing.JSeparator();
        numLabel = new javax.swing.JLabel();
        numSeparator = new javax.swing.JSeparator();
        titleFirstSeparator = new javax.swing.JSeparator();
        titleFirstLabel = new javax.swing.JLabel();
        titleFirstCheckBox = new javax.swing.JCheckBox();
        reverseNumberingCheckBox = new javax.swing.JCheckBox();
        listOnlyCoauthorsCheckBox = new javax.swing.JCheckBox();
        firstNameLabel = new javax.swing.JLabel();
        fullFirstNameRadioButton = new javax.swing.JRadioButton();
        abbrFirstNameRadioButton = new javax.swing.JRadioButton();
        noFirstNameRadioButton = new javax.swing.JRadioButton();
        reverseNamesCheckBox = new javax.swing.JCheckBox();
        yourNameLabel = new javax.swing.JLabel();
        yourNameSeparator = new javax.swing.JSeparator();
        yourNameTextField = new javax.swing.JTextField();

        numGroup.add(numNoneRadioButton);
        numNoneRadioButton.setText("None");
        numNoneRadioButton.setToolTipText("Do not number any publications.");
        numNoneRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numNoneRadioButtonActionPerformed(evt);
            }
        });

        numGroup.add(numGlobalRadioButton);
        numGlobalRadioButton.setText("Global numbering");
        numGlobalRadioButton.setToolTipText("Number all publications sequentially, with no regard to sections.");
        numGlobalRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numGlobalRadioButtonActionPerformed(evt);
            }
        });

        numGroup.add(numLocalRadioButton);
        numLocalRadioButton.setText("Category numbering");
        numLocalRadioButton.setToolTipText("Number the publications in each section independently.");
        numLocalRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numLocalRadioButtonActionPerformed(evt);
            }
        });

        authorLabel.setText("Author information");

        numLabel.setText("Publication numbering");

        titleFirstLabel.setText("Title placement");

        titleFirstCheckBox.setText("Title before authors");
        titleFirstCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                titleFirstCheckBoxItemStateChanged(evt);
            }
        });

        reverseNumberingCheckBox.setText("Reverse numbering");
        reverseNumberingCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                reverseNumberingCheckBoxItemStateChanged(evt);
            }
        });

        listOnlyCoauthorsCheckBox.setText("List only my co-authors");
        listOnlyCoauthorsCheckBox.setToolTipText("The authors will be displayed as \"With\", followed by a list of the other authors on the paper. If you are the only author, no author information is displayed.");
        listOnlyCoauthorsCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                listOnlyCoauthorsCheckBoxItemStateChanged(evt);
            }
        });

        firstNameLabel.setText("First names:");

        firstNameGroup.add(fullFirstNameRadioButton);
        fullFirstNameRadioButton.setText("Full");
        fullFirstNameRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullFirstNameRadioButtonActionPerformed(evt);
            }
        });

        firstNameGroup.add(abbrFirstNameRadioButton);
        abbrFirstNameRadioButton.setText("Abbreviated");
        abbrFirstNameRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abbrFirstNameRadioButtonActionPerformed(evt);
            }
        });

        firstNameGroup.add(noFirstNameRadioButton);
        noFirstNameRadioButton.setText("None");
        noFirstNameRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noFirstNameRadioButtonActionPerformed(evt);
            }
        });

        reverseNamesCheckBox.setText("Last names first (Last name, First name)");
        reverseNamesCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                reverseNamesCheckBoxItemStateChanged(evt);
            }
        });

        yourNameLabel.setText("Your name");

        yourNameTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                yourNameTextFieldTextChanged(e);
            }
            public void removeUpdate(DocumentEvent e) {
                yourNameTextFieldTextChanged(e);
            }
            public void changedUpdate(DocumentEvent e) {
                //Plain text components do not fire these events
            }
        });
        yourNameTextField.setToolTipText("Semicolon-separated list of names or author abbreviations from the BibTeX file that denote you.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(numNoneRadioButton)
                                .addGap(18, 18, 18)
                                .addComponent(numLocalRadioButton)
                                .addGap(18, 18, 18)
                                .addComponent(numGlobalRadioButton)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(titleFirstCheckBox)
                                    .addComponent(reverseNumberingCheckBox))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(reverseNamesCheckBox)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(firstNameLabel)
                                .addGap(18, 18, 18)
                                .addComponent(fullFirstNameRadioButton)
                                .addGap(18, 18, 18)
                                .addComponent(abbrFirstNameRadioButton)
                                .addGap(18, 18, 18)
                                .addComponent(noFirstNameRadioButton))
                            .addComponent(listOnlyCoauthorsCheckBox))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(numLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(numSeparator))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(authorLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(authorSeparator))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(titleFirstLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(titleFirstSeparator))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(yourNameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(yourNameSeparator))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(yourNameTextField)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(yourNameLabel)
                    .addComponent(yourNameSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yourNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(authorSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(authorLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstNameLabel)
                    .addComponent(fullFirstNameRadioButton)
                    .addComponent(abbrFirstNameRadioButton)
                    .addComponent(noFirstNameRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reverseNamesCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(listOnlyCoauthorsCheckBox)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(titleFirstSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(titleFirstLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(titleFirstCheckBox)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(numLabel)
                    .addComponent(numSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numNoneRadioButton)
                    .addComponent(numLocalRadioButton)
                    .addComponent(numGlobalRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reverseNumberingCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void yourNameTextFieldTextChanged(DocumentEvent e) {
        settings.setMyNames(Arrays.asList(yourNameTextField.getText().split(";")));
    }
    
    private void numNoneRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numNoneRadioButtonActionPerformed
        settings.setNumbering(GeneralSettings.Numbering.NONE);
        reverseNumberingCheckBox.setEnabled(false);
    }//GEN-LAST:event_numNoneRadioButtonActionPerformed

    private void numLocalRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numLocalRadioButtonActionPerformed
        settings.setNumbering(GeneralSettings.Numbering.LOCAL);
        reverseNumberingCheckBox.setEnabled(true);
    }//GEN-LAST:event_numLocalRadioButtonActionPerformed

    private void numGlobalRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numGlobalRadioButtonActionPerformed
        settings.setNumbering(GeneralSettings.Numbering.GLOBAL);
        reverseNumberingCheckBox.setEnabled(true);
    }//GEN-LAST:event_numGlobalRadioButtonActionPerformed

    private void titleFirstCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_titleFirstCheckBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            settings.setTitleFirst(false);
        } else if (evt.getStateChange() == ItemEvent.SELECTED) {
            settings.setTitleFirst(true);
        }
    }//GEN-LAST:event_titleFirstCheckBoxItemStateChanged

    private void reverseNumberingCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_reverseNumberingCheckBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            settings.setReverseNumbering(false);
        } else if (evt.getStateChange() == ItemEvent.SELECTED) {
            settings.setReverseNumbering(true);
        }
    }//GEN-LAST:event_reverseNumberingCheckBoxItemStateChanged

    private void listOnlyCoauthorsCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_listOnlyCoauthorsCheckBoxItemStateChanged
        // Negation of usual logic, as the meaning of the two options is opposite
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            settings.setListAllAuthors(true);
        } else if (evt.getStateChange() == ItemEvent.SELECTED) {
            settings.setListAllAuthors(false);
        }
    }//GEN-LAST:event_listOnlyCoauthorsCheckBoxItemStateChanged

    private void fullFirstNameRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullFirstNameRadioButtonActionPerformed
        settings.setNameDisplay(GeneralSettings.NameDisplay.FULL);
        reverseNamesCheckBox.setEnabled(true);
    }//GEN-LAST:event_fullFirstNameRadioButtonActionPerformed

    private void abbrFirstNameRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abbrFirstNameRadioButtonActionPerformed
        settings.setNameDisplay(GeneralSettings.NameDisplay.ABBREVIATED);
        reverseNamesCheckBox.setEnabled(true);
    }//GEN-LAST:event_abbrFirstNameRadioButtonActionPerformed

    private void noFirstNameRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noFirstNameRadioButtonActionPerformed
        settings.setNameDisplay(GeneralSettings.NameDisplay.NONE);
        reverseNamesCheckBox.setEnabled(false);
    }//GEN-LAST:event_noFirstNameRadioButtonActionPerformed

    private void reverseNamesCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_reverseNamesCheckBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            settings.setReverseNames(false);
        } else if (evt.getStateChange() == ItemEvent.SELECTED) {
            settings.setReverseNames(true);
        }
    }//GEN-LAST:event_reverseNamesCheckBoxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton abbrFirstNameRadioButton;
    private javax.swing.JLabel authorLabel;
    private javax.swing.JSeparator authorSeparator;
    private javax.swing.ButtonGroup firstNameGroup;
    private javax.swing.JLabel firstNameLabel;
    private javax.swing.JRadioButton fullFirstNameRadioButton;
    private javax.swing.JCheckBox listOnlyCoauthorsCheckBox;
    private javax.swing.JRadioButton noFirstNameRadioButton;
    private javax.swing.JRadioButton numGlobalRadioButton;
    private javax.swing.ButtonGroup numGroup;
    private javax.swing.JLabel numLabel;
    private javax.swing.JRadioButton numLocalRadioButton;
    private javax.swing.JRadioButton numNoneRadioButton;
    private javax.swing.JSeparator numSeparator;
    private javax.swing.JCheckBox reverseNamesCheckBox;
    private javax.swing.JCheckBox reverseNumberingCheckBox;
    private javax.swing.JCheckBox titleFirstCheckBox;
    private javax.swing.JLabel titleFirstLabel;
    private javax.swing.JSeparator titleFirstSeparator;
    private javax.swing.JLabel yourNameLabel;
    private javax.swing.JSeparator yourNameSeparator;
    private javax.swing.JTextField yourNameTextField;
    // End of variables declaration//GEN-END:variables
}
