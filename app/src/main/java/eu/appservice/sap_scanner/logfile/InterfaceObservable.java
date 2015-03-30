package eu.appservice.sap_scanner.logfile;

/**
 * Created by Lukasz on 25.02.14.
 * ﹕ SAP Skanner
 */
public interface InterfaceObservable {
    public void addObserver(InterfaceObserver iObserver);

    public void deleteObserver(InterfaceObserver iObserver);

    public void notifyObservers();

    public void notifyObservers(Object arg);
}
