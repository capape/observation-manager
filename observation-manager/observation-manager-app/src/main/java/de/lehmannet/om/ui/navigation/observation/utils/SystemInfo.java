package de.lehmannet.om.ui.navigation.observation.utils;

public final class SystemInfo {

    public static final String printMemoryUsage() {

        String mem = "Memory Usage:\n\t- Current Heap Size: ";

        // Currently used memory
        long fMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        mem = mem + fMem;

        mem = mem + "\n\t- Max. Heap Size: " + Runtime.getRuntime().maxMemory();

        return mem;
    }
}
