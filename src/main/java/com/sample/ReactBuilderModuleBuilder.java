package com.sample;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class ReactBuilderModuleBuilder extends ModuleBuilder {
    @Override
    public void setupRootModel(@NotNull ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        String path = modifiableRootModel.getProject().getBasePath();
        if (path != null) {
            String srcPath = path + "/src";
            try {
                FileUtil.ensureExists(new File(srcPath));
                addSourceRoot(modifiableRootModel, new File(srcPath));
            } catch (IOException e) {
                throw new ConfigurationException("Could not create source directory", "Error");
            }
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
}