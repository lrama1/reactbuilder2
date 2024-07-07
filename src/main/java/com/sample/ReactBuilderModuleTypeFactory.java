package com.sample;

import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import org.jetbrains.annotations.NotNull;

public class ReactBuilderModuleTypeFactory {
    @NotNull
    public ModuleType<?> createModuleType(@NotNull String id) {
        if (ReactBuilderModuleType.ID.equals(id)) {
            return ReactBuilderModuleType.getInstance();
        }
        return ModuleTypeManager.getInstance().getDefaultModuleType();
    }
}