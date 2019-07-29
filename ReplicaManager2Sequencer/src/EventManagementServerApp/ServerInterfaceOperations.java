package EventManagementServerApp;


/**
* EventManagementServerApp/ServerInterfaceOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /Users/gursimransingh/IdeaProjects/DistributedAssignment2/CORBA/src/WebInterface/LibrariesServer.idl
* Saturday, 6 July, 2019 5:23:54 PM EDT
*/

public interface ServerInterfaceOperations 
{
  String addEvent (String eventID, String eventType, String bookingCapacity, String managerID);
  String removeEvent (String eventID, String eventType, String managerID);
  String listEventAvailability (String eventType, String managerID);
  String bookEvent (String customerID, String eventID, String eventType, String bookingAmount);
  String getBookingSchedule (String customerID, String managerID);
  String cancelEvent (String customerID, String eventID, String eventType);
  String nonOriginCustomerBooking (String customerID, String eventID);
  String swapEvent (String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType);
  String eventAvailable (String eventID, String eventType);
  String validateBooking (String customerID, String eventID, String eventType);

} // interface ServerInterfaceOperations
