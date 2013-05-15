/*
 */
package publistgenerator.gui;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.ListModel;
import publistgenerator.data.category.CategoryIdentifier;
import publistgenerator.data.settings.FormatSettings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class GeneralSettingsPanel extends javax.swing.JPanel {

    private FormatSettings settings;
    private DefaultListModel<CategoryIdentifier> inListModel;
    private DefaultListModel<CategoryIdentifier> outListModel;
    private CategoryIdentifier selectedCategory;
    
    /**
     * Creates new form GeneralSettingsPanel
     */
    public GeneralSettingsPanel(FormatSettings settings) {
        this.settings = settings;
        initComponents();
        populateValues();
    }

    @SuppressWarnings("unchecked")
    private void populateValues() {
        // Target
        if (settings.getTarget() == null) {
            targetTextField.setText("");
        } else {
            targetTextField.setText(settings.getTarget().getPath());
        }

        // List all authors
        if (settings.isListAllAuthors()) {
            listAllRadioButton.setSelected(true);
        } else {
            listOtherRadioButton.setSelected(true);
        }

        // PresentedText
        if (settings.getPresentedText() == null) {
            presentedTextField.setText("");
        } else {
            presentedTextField.setText(settings.getPresentedText());
        }

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

        // Categories
        Set<CategoryIdentifier> in = EnumSet.noneOf(CategoryIdentifier.class);
        Set<CategoryIdentifier> out = EnumSet.allOf(CategoryIdentifier.class);

        for (CategoryIdentifier c : settings.getCategories()) {
            in.add(c);
            out.remove(c);
        }

        inListModel = new DefaultListModel<>();
        outListModel = new DefaultListModel<>();

        for (CategoryIdentifier c : in) {
            inListModel.addElement(c);
        }

        for (CategoryIdentifier c : out) {
            outListModel.addElement(c);
        }

        inCatList.setModel(inListModel);
        outCatList.setModel(outListModel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listGroup = new javax.swing.ButtonGroup();
        numGroup = new javax.swing.ButtonGroup();
        targetFileChooser = new javax.swing.JFileChooser();
        targetTextField = new javax.swing.JTextField();
        targetBrowseButton = new javax.swing.JButton();
        presentedLabel = new javax.swing.JLabel();
        presentedTextField = new javax.swing.JTextField();
        numNoneRadioButton = new javax.swing.JRadioButton();
        numGlobalRadioButton = new javax.swing.JRadioButton();
        numLocalRadioButton = new javax.swing.JRadioButton();
        targetLabel = new javax.swing.JLabel();
        targetSeparator = new javax.swing.JSeparator();
        authorLabel = new javax.swing.JLabel();
        authorSeparator = new javax.swing.JSeparator();
        presentedSeparator = new javax.swing.JSeparator();
        listAllRadioButton = new javax.swing.JRadioButton();
        listOtherRadioButton = new javax.swing.JRadioButton();
        numLabel = new javax.swing.JLabel();
        numSeparator = new javax.swing.JSeparator();
        catLabel = new javax.swing.JLabel();
        catSeparator = new javax.swing.JSeparator();
        catPanel = new javax.swing.JPanel();
        noteLabel = new javax.swing.JLabel();
        noteSeparator = new javax.swing.JSeparator();
        noteTextField = new javax.swing.JTextField();
        inCatScrollPane = new javax.swing.JScrollPane();
        inCatList = new javax.swing.JList();
        inButton = new javax.swing.JButton();
        outButton = new javax.swing.JButton();
        catButtonSeparator = new javax.swing.JSeparator();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        outCatScrollPane = new javax.swing.JScrollPane();
        outCatList = new javax.swing.JList();

        targetFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        setBorder(javax.swing.BorderFactory.createTitledBorder("General Settings"));

        targetBrowseButton.setText("Browse...");
        targetBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetBrowseButtonActionPerformed(evt);
            }
        });

        presentedLabel.setText("Text added after papers I presented");

        numGroup.add(numNoneRadioButton);
        numNoneRadioButton.setText("None");

        numGroup.add(numGlobalRadioButton);
        numGlobalRadioButton.setText("Global numbering");

        numGroup.add(numLocalRadioButton);
        numLocalRadioButton.setText("Section numbering");

        targetLabel.setText("Output file");

        authorLabel.setText("Author information");

        listGroup.add(listAllRadioButton);
        listAllRadioButton.setText("List all authors");

        listGroup.add(listOtherRadioButton);
        listOtherRadioButton.setText("List only my co-authors");

        numLabel.setText("Publication numbering");

        catLabel.setText("Category selection");

        catPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Category Settings"));

        noteLabel.setText("Text at the start");

        noteTextField.setEnabled(false);

        javax.swing.GroupLayout catPanelLayout = new javax.swing.GroupLayout(catPanel);
        catPanel.setLayout(catPanelLayout);
        catPanelLayout.setHorizontalGroup(
            catPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(catPanelLayout.createSequentialGroup()
                .addGroup(catPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(catPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(noteLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(noteSeparator))
                    .addGroup(catPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(noteTextField)))
                .addContainerGap())
        );
        catPanelLayout.setVerticalGroup(
            catPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(catPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(catPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(noteSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(noteLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noteTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        inCatList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        inCatList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        inCatList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                inCatListValueChanged(evt);
            }
        });
        inCatScrollPane.setViewportView(inCatList);

        inButton.setText("<html>&larr;</html>");
        inButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inButtonActionPerformed(evt);
            }
        });

        outButton.setText("<html>&rarr;</html>");
        outButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outButtonActionPerformed(evt);
            }
        });

        upButton.setText("<html>&uarr;</html>");
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.setText("<html>&darr;</html>");
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        outCatList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        outCatList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        outCatList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                outCatListValueChanged(evt);
            }
        });
        outCatScrollPane.setViewportView(outCatList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(catPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(numLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numSeparator)
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(presentedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(presentedSeparator)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(authorLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(authorSeparator))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(targetLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(targetSeparator))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(listAllRadioButton)
                                        .addGap(18, 18, 18)
                                        .addComponent(listOtherRadioButton)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(targetTextField)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(targetBrowseButton)))))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(catLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(catSeparator)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(presentedTextField)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(numNoneRadioButton)
                                        .addGap(18, 18, 18)
                                        .addComponent(numLocalRadioButton)
                                        .addGap(18, 18, 18)
                                        .addComponent(numGlobalRadioButton))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(inCatScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(inButton)
                                            .addComponent(outButton)
                                            .addComponent(upButton)
                                            .addComponent(downButton)
                                            .addComponent(catButtonSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(outCatScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {inCatScrollPane, outCatScrollPane});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(targetLabel)
                    .addComponent(targetSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(targetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(targetBrowseButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(authorSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(authorLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(listAllRadioButton)
                    .addComponent(listOtherRadioButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(presentedLabel)
                    .addComponent(presentedSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(presentedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(numLabel)
                    .addComponent(numSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numNoneRadioButton)
                    .addComponent(numLocalRadioButton)
                    .addComponent(numGlobalRadioButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(catSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(catLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(inButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(outButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(catButtonSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(upButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(downButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(inCatScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(outCatScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(catPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void targetBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetBrowseButtonActionPerformed
        int opened = targetFileChooser.showOpenDialog(this);

        if (opened == JFileChooser.APPROVE_OPTION) {
            targetTextField.setText(targetFileChooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_targetBrowseButtonActionPerformed

    private void inButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inButtonActionPerformed
        CategoryIdentifier selected = (CategoryIdentifier) outCatList.getSelectedValue();

        inListModel.addElement(selected);
        outListModel.removeElement(selected);

        inCatList.setSelectedValue(selected, true);
    }//GEN-LAST:event_inButtonActionPerformed

    private void outButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outButtonActionPerformed
        CategoryIdentifier selected = (CategoryIdentifier) inCatList.getSelectedValue();

        outListModel.addElement(selected);
        inListModel.removeElement(selected);

        outCatList.setSelectedValue(selected, true);
    }//GEN-LAST:event_outButtonActionPerformed

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        int selected = inCatList.getSelectedIndex();

        if (selected > 0) {
            CategoryIdentifier up = inListModel.set(selected - 1, inListModel.get(selected));
            inListModel.set(selected, up);
        }
    }//GEN-LAST:event_upButtonActionPerformed

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        int selected = inCatList.getSelectedIndex();

        if (selected < inListModel.getSize() - 1) {
            CategoryIdentifier down = inListModel.set(selected + 1, inListModel.get(selected));
            inListModel.set(selected, down);
        }
    }//GEN-LAST:event_downButtonActionPerformed

    private void setSelectedCategory(CategoryIdentifier c) {
        selectedCategory = c;
        
        if (c == null) {
            // Note
            noteTextField.setText("");
            noteTextField.setEnabled(false);
        } else {
            // Note
            noteTextField.setText(settings.getCategoryNotes().get(c));
            noteTextField.setEnabled(true);
        }
    }
    
    private void inCatListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_inCatListValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (inCatList.getSelectedIndex() == -1) {
                // No selection, disable buttons
                outButton.setEnabled(false);
                upButton.setEnabled(false);
                downButton.setEnabled(false);
                
                if (outCatList.getSelectedIndex() == -1) {
                    // No selection at all, disable category settings
                    setSelectedCategory(null);
                }
            } else {
                // Selection, enable buttons
                outButton.setEnabled(true);
                upButton.setEnabled(true);
                downButton.setEnabled(true);
                
                // Remove selection in the out list
                outCatList.setSelectedIndex(-1);
                
                setSelectedCategory((CategoryIdentifier) inCatList.getSelectedValue());
            }
        }
    }//GEN-LAST:event_inCatListValueChanged

    private void outCatListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_outCatListValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (outCatList.getSelectedIndex() == -1) {
                //No selection, disable button
                inButton.setEnabled(false);
                
                if (inCatList.getSelectedIndex() == -1) {
                    // No selection at all, disable category settings
                    setSelectedCategory(null);
                }
            } else {
                //Selection, enable button
                inButton.setEnabled(true);
                
                // Remove selection in the in list
                inCatList.setSelectedIndex(-1);
                
                setSelectedCategory((CategoryIdentifier) outCatList.getSelectedValue());
            }
        }
    }//GEN-LAST:event_outCatListValueChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel authorLabel;
    private javax.swing.JSeparator authorSeparator;
    private javax.swing.JSeparator catButtonSeparator;
    private javax.swing.JLabel catLabel;
    private javax.swing.JPanel catPanel;
    private javax.swing.JSeparator catSeparator;
    private javax.swing.JButton downButton;
    private javax.swing.JButton inButton;
    private javax.swing.JList inCatList;
    private javax.swing.JScrollPane inCatScrollPane;
    private javax.swing.JRadioButton listAllRadioButton;
    private javax.swing.ButtonGroup listGroup;
    private javax.swing.JRadioButton listOtherRadioButton;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JSeparator noteSeparator;
    private javax.swing.JTextField noteTextField;
    private javax.swing.JRadioButton numGlobalRadioButton;
    private javax.swing.ButtonGroup numGroup;
    private javax.swing.JLabel numLabel;
    private javax.swing.JRadioButton numLocalRadioButton;
    private javax.swing.JRadioButton numNoneRadioButton;
    private javax.swing.JSeparator numSeparator;
    private javax.swing.JButton outButton;
    private javax.swing.JList outCatList;
    private javax.swing.JScrollPane outCatScrollPane;
    private javax.swing.JLabel presentedLabel;
    private javax.swing.JSeparator presentedSeparator;
    private javax.swing.JTextField presentedTextField;
    private javax.swing.JButton targetBrowseButton;
    private javax.swing.JFileChooser targetFileChooser;
    private javax.swing.JLabel targetLabel;
    private javax.swing.JSeparator targetSeparator;
    private javax.swing.JTextField targetTextField;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables
}
