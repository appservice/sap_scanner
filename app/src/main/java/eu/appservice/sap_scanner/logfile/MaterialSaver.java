package eu.appservice.sap_scanner.logfile;


import eu.appservice.sap_scanner.CollectedMaterial;

/**
 * Created by Lukasz on 23.02.14.
 * ﹕ SAP Skanner
 */
public interface MaterialSaver {
    int save(CollectedMaterial collectedMaterial);
}
