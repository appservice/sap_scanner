package eu.appservice.sap_scanner.logfile;

/**
 * Created by Lukasz on 25.02.14.
 * ﹕ SAP Skanner
 */
public interface InterfaceObserver {

    void update();

    void update(InterfaceObservable o, Object arg);
}
