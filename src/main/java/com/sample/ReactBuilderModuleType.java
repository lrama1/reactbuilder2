package com.sample;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ReactBuilderModuleType extends ModuleType<ReactBuilderModuleBuilder> {
    public static final String ID = "REACT_BUILDER_MODULE";

    private static final ReactBuilderModuleType INSTANCE = new ReactBuilderModuleType();

    private ReactBuilderModuleType() {
        super(ID);
    }

    @NotNull
    public static ReactBuilderModuleType getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public ReactBuilderModuleBuilder createModuleBuilder() {
        return new ReactBuilderModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "React Builder Module";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Module for building React applications";
    }

    @Override
    public @NotNull Icon getNodeIcon(boolean isOpened) {
        return AllIcons.Nodes.Module;
    }
}