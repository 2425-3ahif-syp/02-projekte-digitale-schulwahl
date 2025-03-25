package at.htl.digitaleschulwahl.config;

public class FontCacheConfig {
    static {
        System.setProperty("pdfbox.fontcache", "cache/pdfbox-fontcache");
    }
}
