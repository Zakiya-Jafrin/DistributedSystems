package EventManagementIDLApp;


/**
* EventManagementIDLApp/EventManagementIDLPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /Users/zakiyajafrin/Documents/COMP6231-DS/CORBA_Assignment/src/EventManagementIDL.idl
* Wednesday, July 10, 2019 12:54:52 o'clock PM EDT
*/


//    typedef sequence<string> KeyValSeq;
public abstract class EventManagementIDLPOA extends org.omg.PortableServer.Servant
 implements EventManagementIDLApp.EventManagementIDLOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("addEvent", new java.lang.Integer (0));
    _methods.put ("removeEvent", new java.lang.Integer (1));
    _methods.put ("listEventAvailability", new java.lang.Integer (2));
    _methods.put ("bookEvent", new java.lang.Integer (3));
    _methods.put ("cancelEvent", new java.lang.Integer (4));
    _methods.put ("getBookingSchedule", new java.lang.Integer (5));
    _methods.put ("swapEvent", new java.lang.Integer (6));
    _methods.put ("shutdown", new java.lang.Integer (7));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // EventManagementIDLApp/EventManagementIDL/addEvent
       {
         String managerID = in.read_string ();
         String eventID = in.read_string ();
         String eventType = in.read_string ();
         int bookingCapacity = in.read_long ();
         boolean $result = false;
         $result = this.addEvent (managerID, eventID, eventType, bookingCapacity);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 1:  // EventManagementIDLApp/EventManagementIDL/removeEvent
       {
         String managerID = in.read_string ();
         String eventID = in.read_string ();
         String eventType = in.read_string ();
         boolean $result = false;
         $result = this.removeEvent (managerID, eventID, eventType);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 2:  // EventManagementIDLApp/EventManagementIDL/listEventAvailability
       {
         String managerID = in.read_string ();
         String eventType = in.read_string ();
         String $result[] = null;
         $result = this.listEventAvailability (managerID, eventType);
         out = $rh.createReply();
         EventManagementIDLApp.ResultListHelper.write (out, $result);
         break;
       }

       case 3:  // EventManagementIDLApp/EventManagementIDL/bookEvent
       {
         String customerId = in.read_string ();
         String eventId = in.read_string ();
         String eventType = in.read_string ();
         String $result[] = null;
         $result = this.bookEvent (customerId, eventId, eventType);
         out = $rh.createReply();
         EventManagementIDLApp.ResultListHelper.write (out, $result);
         break;
       }

       case 4:  // EventManagementIDLApp/EventManagementIDL/cancelEvent
       {
         String customerId = in.read_string ();
         String eventId = in.read_string ();
         String $result[] = null;
         $result = this.cancelEvent (customerId, eventId);
         out = $rh.createReply();
         EventManagementIDLApp.ResultListHelper.write (out, $result);
         break;
       }

       case 5:  // EventManagementIDLApp/EventManagementIDL/getBookingSchedule
       {
         String customerId = in.read_string ();
         String $result[] = null;
         $result = this.getBookingSchedule (customerId);
         out = $rh.createReply();
         EventManagementIDLApp.ResultListHelper.write (out, $result);
         break;
       }

       case 6:  // EventManagementIDLApp/EventManagementIDL/swapEvent
       {
         String customerId = in.read_string ();
         String newEventId = in.read_string ();
         String newEventType = in.read_string ();
         String oldEventId = in.read_string ();
         String oldEventType = in.read_string ();
         String $result[] = null;
         $result = this.swapEvent (customerId, newEventId, newEventType, oldEventId, oldEventType);
         out = $rh.createReply();
         EventManagementIDLApp.ResultListHelper.write (out, $result);
         break;
       }


  //	    boolean AddUser(in string ID);
       case 7:  // EventManagementIDLApp/EventManagementIDL/shutdown
       {
         this.shutdown ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:EventManagementIDLApp/EventManagementIDL:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public EventManagementIDL _this() 
  {
    return EventManagementIDLHelper.narrow(
    super._this_object());
  }

  public EventManagementIDL _this(org.omg.CORBA.ORB orb) 
  {
    return EventManagementIDLHelper.narrow(
    super._this_object(orb));
  }


} // class EventManagementIDLPOA
