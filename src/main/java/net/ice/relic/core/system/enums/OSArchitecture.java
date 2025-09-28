package net.ice.relic.core.system.enums;

public enum OSArchitecture {
    AMD64("x86_64"),
    ARM64("aarch64"),
    x86("IA-32"),
    UNKNOWN("unknown");

    public String alias;

    OSArchitecture(String alias) {
        this.alias = alias;
    }

}
