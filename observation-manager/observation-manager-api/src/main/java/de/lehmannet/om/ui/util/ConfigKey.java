package de.lehmannet.om.ui.util;

public enum ConfigKey implements IConfigKey {

    // @formatter:off
    CONFIG_LASTDIR("om.lastOpenedDir"),
    CONFIG_LASTXML("om.lastOpenedXML"),
    CONFIG_OPENONSTARTUP("om.lastOpenedXML.onStartup"),
    CONFIG_CONTENTDEFAULTLANG("om.content.language.default"),
    CONFIG_MAINWINDOW_SIZE("om.mainwindow.size"),
    CONFIG_MAINWINDOW_POS("om.mainwindow.position"),
    CONFIG_MAINWINDOW_MAXIMIZED("om.mainwindow.maximized"),
    CONFIG_IMAGESDIR_RELATIVE("om.imagesDir.relaitve"),
    CONFIG_UILANGUAGE("om.language"),
    CONFIG_DEFAULT_OBSERVER("om.default.observer"),
    CONFIG_DEFAULT_CATALOG("om.default.catalog"),
    CONFIG_HELP_HINTS_STARTUP("om.help.hints.showOnStartup"),
    CONFIG_RETRIEVE_ENDDATE_FROM_SESSION("om.retrieve.endDateFromSession"),
    CONFIG_STATISTICS_USE_COOBSERVERS("om.statistics.useCoObservers"),
    CONFIG_XSL_TEMPLATE("om.transform.xsl.template"),
    CONFIG_MAINWINDOW_DIVIDER_VERTICAL("om.mainwindow.divider.vertical"),
    CONFIG_MAINWINDOW_DIVIDER_HORIZONTAL("om.mainwindow.divider.horizontal"),
    CONFIG_CONSTELLATION_USEI18N("om.constellation.useI18N"),
    CONFIG_UPDATECHECK_STARTUP("om.update.checkForUpdates"),
    CONFIG_NIGHTVISION_ENABLED("om.nightvision.enable");
    // @formatter:on

    private final String key;

    private ConfigKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

}