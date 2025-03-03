package org.bananalaba.jdk24;

public class CpuClassifier {

    public static VmInstanceClass classify(final int numberOfCpuCores) {
        return switch (numberOfCpuCores) {
            case int i when i >= 1 && i <= 4 -> VmInstanceClass.SMALL;
            case int i when i >= 5 && i <= 8 -> VmInstanceClass.MEDIUM;
            case int i when i >= 9 && i < 128 -> VmInstanceClass.LARGE;
            default -> throw new IllegalArgumentException("unsupported core count: " + numberOfCpuCores);
        };
    }

}
