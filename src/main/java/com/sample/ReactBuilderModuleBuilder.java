package com.sample;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class ReactBuilderModuleBuilder extends ModuleBuilder {
    private String packageName;
    private String domainClassName;
    private Vector<Vector> tableData;

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        String path = modifiableRootModel.getProject().getBasePath();
        if (path != null) {
            String srcPath = path + "/src/main/java/" + packageName.replace('.', '/');
            try {
                FileUtil.ensureExists(new File(srcPath));
                addSourceRoot(modifiableRootModel, new File(srcPath));
                createJavaClass(srcPath, domainClassName, tableData);
            } catch (IOException e) {
                throw new ConfigurationException("Could not create source directory", "Error");
            }
        }
    }

    private void createJavaClass(String path, String className, Vector<Vector> data) throws IOException {
        File file = new File(path, className + ".java");
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("package " + packageName + ";");
            out.println();
            out.println("public class " + className + " {");
            for (Vector<Object> row : data) {
                String attributeName = (String) row.get(0);
                String dataType = (String) row.get(1);
                out.println("    private " + dataType + " " + attributeName + ";");
            }
            out.println("}");
        }
    }

    @Override
    public ModuleType<?> getModuleType() {
        return ReactBuilderModuleType.getInstance();
    }

    private void addSourceRoot(@NotNull ModifiableRootModel modifiableRootModel, File srcPath) {
        var root = modifiableRootModel.getProject().getBaseDir();
        var srcDir = root.findChild("src");
        if (srcDir != null) {
            modifiableRootModel.addContentEntry(srcDir);
        }
    }

    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new ModuleWizardStep() {
            JPanel panel = new JPanel(new GridBagLayout());
            JTextField packageNameField = new JTextField();
            JTextField domainClassNameField = new JTextField();
            DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"attributeName", "dataType", "isId", "fieldType"}, 0);
            JTable table = new JTable(tableModel);

            @Override
            public JComponent getComponent() {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.fill = GridBagConstraints.HORIZONTAL;

                packageNameField.setPreferredSize(new Dimension(300, 25));
                domainClassNameField.setPreferredSize(new Dimension(300, 25));

                panel.add(new JLabel("Package Name:"), gbc);
                panel.add(packageNameField, gbc);
                panel.add(new JLabel("Domain Class Name:"), gbc);
                panel.add(domainClassNameField, gbc);

                // Set up the table
                table.setPreferredScrollableViewportSize(new Dimension(500, 70));
                table.setFillsViewportHeight(true);

                // Set up the editors for the table columns
                TableColumn dataTypeColumn = table.getColumnModel().getColumn(1);
                JComboBox<String> dataTypeEditor = new JComboBox<>(new String[]{"String", "Integer", "Long", "Double"});
                dataTypeColumn.setCellEditor(new DefaultCellEditor(dataTypeEditor));

                TableColumn isIdColumn = table.getColumnModel().getColumn(2);
                isIdColumn.setCellEditor(new DefaultCellEditor(new JCheckBox()));

                TableColumn fieldTypeColumn = table.getColumnModel().getColumn(3);
                JComboBox<String> fieldTypeEditor = new JComboBox<>(new String[]{"Text", "Number", "Checkbox"});
                fieldTypeColumn.setCellEditor(new DefaultCellEditor(fieldTypeEditor));

                panel.add(new JScrollPane(table), gbc);

                // Add Row button
                JButton addRowButton = new JButton("Add Row");
                addRowButton.addActionListener(e -> tableModel.addRow(new Object[]{"", "", false, ""}));
                panel.add(addRowButton, gbc);

                // Delete Row button
                JButton deleteRowButton = new JButton("Delete Row");
                deleteRowButton.addActionListener(e -> {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        tableModel.removeRow(selectedRow);
                    }
                });
                panel.add(deleteRowButton, gbc);

                return panel;
            }

            @Override
            public void updateDataModel() {
                // Update the data model with the values from the text fields
                packageName = packageNameField.getText();
                domainClassName = domainClassNameField.getText();
                tableData = tableModel.getDataVector();
            }
        };
    }
}