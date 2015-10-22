package eu.appservice.sap_scanner.logfile;

/**
 * Created by Lukasz on 25.02.14.
 * ﹕ SAP Skanner
 */
public interface InterfaceObservable {
    void addObserver(InterfaceObserver iObserver);

    void deleteObserver(InterfaceObserver iObserver);

    void notifyObservers();

    void notifyObservers(Object arg);
}
