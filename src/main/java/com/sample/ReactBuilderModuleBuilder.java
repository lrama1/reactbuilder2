package com.sample;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.table.JBTable;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.*;
import java.util.*;

public class ReactBuilderModuleBuilder extends ModuleBuilder {
    private String packageName;
    private String domainClassName;
    private Vector<Vector> listOfAttributes;
    private String persistenceType;

    private static String USER_HOME;

    static {
        USER_HOME = System.getProperty("user.home");
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        String path = modifiableRootModel.getProject().getBasePath();
        if (path != null) {
            String srcPath = path + "/src/main/java/" + packageName.replace('.', '/');
            try {
                FileUtil.ensureExists(new File(srcPath));
                addSourceRoot(modifiableRootModel, new File(srcPath));

                // Create the Gradle build file
                createGradleBuildFile(path, modifiableRootModel.getProject().getName());

                // Create the .idea directory and modules.xml file
                createIdeaConfigFiles(path, modifiableRootModel.getProject().getName());

                // Create the Spring Boot starter class
                createSpringBootStarterClass(srcPath, packageName, domainClassName, listOfAttributes, persistenceType);
                createJavaClass(srcPath, packageName, domainClassName, listOfAttributes, persistenceType);
                createRepositoryClass(srcPath, packageName, domainClassName, listOfAttributes, persistenceType); // Invoke createRepositoryClass here
                createServiceClass(srcPath, packageName, domainClassName, listOfAttributes, persistenceType);
                createControllerClass(srcPath, packageName, domainClassName, listOfAttributes, persistenceType);

                // Create the ListWrapper class
                createCommonClass(srcPath, packageName, "ListWrapper", listOfAttributes, persistenceType, "templates/java/listwrapper-template.java");
                createCommonClass(srcPath, packageName, "SortedIndicator", listOfAttributes, persistenceType, "templates/java/sortedIndicator-template.java");
                createCommonClass(srcPath, packageName, "NameValuePair", listOfAttributes, persistenceType, "templates/java/namevalue-template.java");
            } catch (IOException e) {
                throw new ConfigurationException("Could not create source directory", "Error");
            }
        }
    }

    private void createJavaClass(String path, String packageName, String className, Vector<Vector> listOfAttributesFromTable, String persistenceType) {
        Thread thread = Thread.currentThread();
        ClassLoader loader = thread.getContextClassLoader();
        thread.setContextClassLoader(ReactBuilderModuleBuilder.class.getClassLoader());
        String domainPath = path + "/domain/";
        try {
            FileUtil.ensureExists(new File(domainPath));
            VelocityEngine velocityEngine = new VelocityEngine();

            // Set the resource loader to load resources from the classpath
            Properties props = new Properties();
            props.setProperty("resource.loader", "classpath");
            props.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityEngine.init(props);

            Template template = velocityEngine.getTemplate("templates/ClassTemplate.vm");

            VelocityContext context = new VelocityContext();
            context.put("packageName", packageName);
            context.put("className", className);
            context.put("persistenceType", persistenceType);

            ArrayList<Map<String, String>> attributes = new ArrayList<>();
            for (Vector<Object> row : listOfAttributesFromTable) {
                Map<String, String> attribute = new HashMap<>();
                attribute.put("name", (String) row.get(0));
                attribute.put("dataType", (String) row.get(1));
                attributes.add(attribute);
            }
            context.put("attributes", attributes);
            context.put("domainClassIdAttributeName", getIdAttributeName(listOfAttributesFromTable));

            StringWriter writer = new StringWriter();
            template.merge(context, writer);

            File file = new File(domainPath, className + ".java");
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(writer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            thread.setContextClassLoader(loader);
        }
    }

    private void createGradleBuildFile(String path, String projectName) {
        Thread thread = Thread.currentThread();
        ClassLoader loader = thread.getContextClassLoader();
        thread.setContextClassLoader(ReactBuilderModuleBuilder.class.getClassLoader());
        try {
            VelocityEngine velocityEngine = new VelocityEngine();

            // Set the resource loader to load resources from the classpath
            Properties props = new Properties();
            props.setProperty("resource.loader", "classpath");
            props.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityEngine.init(props);

            Template template = velocityEngine.getTemplate("templates/GradleBuildTemplate.vm");

            VelocityContext context = new VelocityContext();
            context.put("projectName", projectName); // Add the project name to the context

            StringWriter writer = new StringWriter();
            template.merge(context, writer);

            File file = new File(path, "build.gradle");
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(writer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            thread.setContextClassLoader(loader);
        }
    }

    private void createSpringBootStarterClass(String path, String packageName, String className, Vector<Vector> data, String persistenceType) {
        Thread thread = Thread.currentThread();
        ClassLoader loader = thread.getContextClassLoader();
        thread.setContextClassLoader(ReactBuilderModuleBuilder.class.getClassLoader());
        try {
            VelocityEngine velocityEngine = new VelocityEngine();

            // Set the resource loader to load resources from the classpath
            Properties props = new Properties();
            props.setProperty("resource.loader", "classpath");
            props.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityEngine.init(props);

            Template template = velocityEngine.getTemplate("templates/java/springboot-start-template.java");

            VelocityContext context = new VelocityContext();
            context.put("packageName", packageName); // Add the package name to the context

            StringWriter writer = new StringWriter();
            template.merge(context, writer);

            File file = new File(path, "SpringBootStarter.java");
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(writer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            thread.setContextClassLoader(loader);
        }
    }

    private void createRepositoryClass(String path, String packageName, String className, Vector<Vector> data, String persistenceType) {
        Thread thread = Thread.currentThread();
        ClassLoader loader = thread.getContextClassLoader();
        thread.setContextClassLoader(ReactBuilderModuleBuilder.class.getClassLoader());
        String repositoryPath = path + "/dao/";
        try {
            FileUtil.ensureExists(new File(repositoryPath));
            VelocityEngine velocityEngine = new VelocityEngine();

            // Set the resource loader to load resources from the classpath
            Properties props = new Properties();
            props.setProperty("resource.loader", "classpath");
            props.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityEngine.init(props);

            Template template = velocityEngine.getTemplate("templates/java/repository-template.java");

            VelocityContext context = new VelocityContext();
            context.put("domainClassName", className);
            context.put("persistenceType", persistenceType);
            context.put("packageName", packageName);

            ArrayList<Map<String, String>> attributes = new ArrayList<>();
            for (Vector<Object> row : data) {
                Map<String, String> attribute = new HashMap<>();
                attribute.put("name", (String) row.get(0));
                attribute.put("dataType", (String) row.get(1));
                attributes.add(attribute);
            }
            context.put("attributes", attributes);

            StringWriter writer = new StringWriter();
            template.merge(context, writer);

            File file = new File(repositoryPath, className + "Repository.java");
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(writer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            thread.setContextClassLoader(loader);
        }
    }

    private void createServiceClass(String path, String packageName, String className, Vector<Vector> data, String persistenceType) {
        Thread thread = Thread.currentThread();
        ClassLoader loader = thread.getContextClassLoader();
        thread.setContextClassLoader(ReactBuilderModuleBuilder.class.getClassLoader());
        String servicePath = path + "/service/";
        try {
            FileUtil.ensureExists(new File(servicePath));
            VelocityEngine velocityEngine = new VelocityEngine();

            // Set the resource loader to load resources from the classpath
            Properties props = new Properties();
            props.setProperty("resource.loader", "classpath");
            props.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityEngine.init(props);

            Template template = velocityEngine.getTemplate("templates/java/service-template.java");

            VelocityContext context = new VelocityContext();
            context.put("domainClassName", className);
            context.put("packageName", packageName);

            ArrayList<Map<String, String>> attributes = new ArrayList<>();
            for (Vector<Object> row : data) {
                Map<String, String> attribute = new HashMap<>();
                attribute.put("name", (String) row.get(0));
                attribute.put("dataType", (String) row.get(1));
                attributes.add(attribute);
            }
            context.put("attributes", attributes);
            context.put("domainClassIdAttributeName", getIdAttributeName(data));

            StringWriter writer = new StringWriter();
            template.merge(context, writer);

            File file = new File(servicePath, className + "Service.java");
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(writer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            thread.setContextClassLoader(loader);
        }
    }

    private void createCommonClass(String path, String packageName, String className, Vector<Vector> data, String persistenceType,
                                   String templateName) {
        Thread thread = Thread.currentThread();
        ClassLoader loader = thread.getContextClassLoader();
        thread.setContextClassLoader(ReactBuilderModuleBuilder.class.getClassLoader());
        String commonPath = path + "/common/";
        try {
            FileUtil.ensureExists(new File(commonPath));
            VelocityEngine velocityEngine = new VelocityEngine();

            // Set the resource loader to load resources from the classpath
            Properties props = new Properties();
            props.setProperty("resource.loader", "classpath");
            props.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityEngine.init(props);

            Template template = velocityEngine.getTemplate(templateName);

            VelocityContext context = new VelocityContext();
            context.put("packageName", packageName);

            StringWriter writer = new StringWriter();
            template.merge(context, writer);

            File file = new File(commonPath, className + ".java");
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(writer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            thread.setContextClassLoader(loader);
        }
    }

    private void createControllerClass(String path, String packageName, String className, Vector<Vector> data, String persistenceType) {
        Thread thread = Thread.currentThread();
        ClassLoader loader = thread.getContextClassLoader();
        thread.setContextClassLoader(ReactBuilderModuleBuilder.class.getClassLoader());
        String controllerPath = path + "/controller/";
        try {
            FileUtil.ensureExists(new File(controllerPath));
            VelocityEngine velocityEngine = new VelocityEngine();

            // Set the resource loader to load resources from the classpath
            Properties props = new Properties();
            props.setProperty("resource.loader", "classpath");
            props.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityEngine.init(props);

            Template template = velocityEngine.getTemplate("templates/java/controller-template.java");

            VelocityContext context = new VelocityContext();
            context.put("domainClassName", className);
            context.put("packageName", packageName);

            ArrayList<Map<String, String>> attributes = new ArrayList<>();
            for (Vector<Object> row : data) {
                Map<String, String> attribute = new HashMap<>();
                attribute.put("name", (String) row.get(0));
                attribute.put("dataType", (String) row.get(1));
                attributes.add(attribute);
            }
            context.put("attributes", attributes);

            StringWriter writer = new StringWriter();
            template.merge(context, writer);

            File file = new File(controllerPath, className + "Controller.java");
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(writer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            thread.setContextClassLoader(loader);
        }
    }

    private void createIdeaConfigFiles(String path, String projectName) {
        File ideaDir = new File(path, ".idea");
        ideaDir.mkdir();

        File modulesFile = new File(ideaDir, "modules.xml");
        try (PrintWriter out = new PrintWriter(modulesFile)) {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<project version=\"4\">");
            out.println("  <component name=\"ProjectModuleManager\">");
            out.println("    <modules>");
            out.println("      <module fileurl=\"file://$PROJECT_DIR$/" + projectName + ".iml\" filepath=\"$PROJECT_DIR$/" + projectName + ".iml\" />");
            out.println("    </modules>");
            out.println("  </component>");
            out.println("</project>");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getIdAttributeName(Vector<Vector> attributes) {
        for (Vector<Object> attribute : attributes) {
            Boolean isId = (Boolean) attribute.get(2);
            if (isId) {
                return (String) attribute.get(0);
            }
        }
        return null;
    }

    @Override
    public ModuleType<?> getModuleType() {
        return ReactBuilderModuleType.getInstance();
    }

    private void addSourceRoot(@NotNull ModifiableRootModel modifiableRootModel, File srcPath) {
        String basePath = modifiableRootModel.getProject().getBasePath();
        if (basePath != null) {
            VirtualFile root = LocalFileSystem.getInstance().findFileByPath(basePath);
            modifiableRootModel.addContentEntry(root);
        }
    }



    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new ModuleWizardStep() {
            JPanel panel = new JPanel(new GridBagLayout());
            JTextField packageNameField = new JTextField();
            JTextField domainClassNameField = new JTextField();
            DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"attributeName", "dataType", "isId", "fieldType"}, 0);
            JBTable table = new JBTable(tableModel);
            JComboBox<String> persistenceTypeComboBox = new JComboBox<>(new String[]{"HSQL", "Mongo"}); // Add the combo box

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
                panel.add(new JLabel("Persistence Type:"), gbc); // Add the label
                panel.add(persistenceTypeComboBox, gbc); // Add the combo box

                // Set up the table
                table.setPreferredScrollableViewportSize(new Dimension(500, 150));
                table.setFillsViewportHeight(true);

                // Set up the editors for the table columns
                TableColumn dataTypeColumn = table.getColumnModel().getColumn(1);
                ComboBox<String> dataTypeEditor = new ComboBox<>(new String[]{"String", "Integer", "Long", "Double"});
                dataTypeColumn.setCellEditor(new DefaultCellEditor(dataTypeEditor));

                TableColumn isIdColumn = table.getColumnModel().getColumn(2);
                isIdColumn.setCellEditor(new DefaultCellEditor(new JCheckBox()));

                TableColumn fieldTypeColumn = table.getColumnModel().getColumn(3);
                ComboBox<String> fieldTypeEditor = new ComboBox<>(new String[]{"Text", "Number", "Checkbox"});
                fieldTypeColumn.setCellEditor(new DefaultCellEditor(fieldTypeEditor));

                panel.add(new JScrollPane(table), gbc);

                // Add Row button
                JButton addRowButton = new JButton("Add Row");
                addRowButton.addActionListener(e -> tableModel.addRow(new Object[]{"", "String", false, "Text"}));
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
                listOfAttributes = tableModel.getDataVector();
                persistenceType = (String) persistenceTypeComboBox.getSelectedItem(); // Get the selected persistence type
            }
        };
    }
}