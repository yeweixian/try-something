package com.dangerye.service;

import org.apache.dubbo.common.extension.ExtensionLoader;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        final ExtensionLoader<HelloService> extensionLoader = ExtensionLoader.getExtensionLoader(HelloService.class);
        final Set<String> supportedExtensions = extensionLoader.getSupportedExtensions();
        for (String supportedExtension : supportedExtensions) {
            final HelloService extension = extensionLoader.getExtension(supportedExtension);
            final String result = extension.sayHello("main");
            System.out.println("result: " + result);
        }
    }
}
