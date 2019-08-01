package Model;

import java.util.HashMap;

/**
 * @author gursimransingh
 */
public class EventData {
    private HashMap<String, HashMap< String, String>> database;
    private  HashMap<String, HashMap<String, HashMap< String, Integer>>> customerEventsMapping;

    public EventData(HashMap<String, HashMap<String, String>> database, HashMap<String, HashMap<String, HashMap<String, Integer>>> customerEventsMapping) {
        this.database = database;
        this.customerEventsMapping = customerEventsMapping;
    }

    public HashMap<String, HashMap<String, String>> getDatabase() {
        return database;
    }

    public void setDatabase(HashMap<String, HashMap<String, String>> database) {
        this.database = database;
    }

    public HashMap<String, HashMap<String, HashMap<String, Integer>>> getCustomerEventsMapping() {
        return customerEventsMapping;
    }

    public void setCustomerEventsMapping(HashMap<String, HashMap<String, HashMap<String, Integer>>> customerEventsMapping) {
        this.customerEventsMapping = customerEventsMapping;
    }
}
