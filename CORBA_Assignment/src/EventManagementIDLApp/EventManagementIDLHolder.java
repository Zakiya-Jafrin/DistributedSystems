package EventManagementIDLApp;

/**
* EventManagementIDLApp/EventManagementIDLHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /Users/zakiyajafrin/Documents/COMP6231-DS/CORBA_Assignment/src/EventManagementIDL.idl
* Wednesday, July 10, 2019 12:54:52 o'clock PM EDT
*/


//    typedef sequence<string> KeyValSeq;
public final class EventManagementIDLHolder implements org.omg.CORBA.portable.Streamable
{
  public EventManagementIDLApp.EventManagementIDL value = null;

  public EventManagementIDLHolder ()
  {
  }

  public EventManagementIDLHolder (EventManagementIDLApp.EventManagementIDL initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = EventManagementIDLApp.EventManagementIDLHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    EventManagementIDLApp.EventManagementIDLHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return EventManagementIDLApp.EventManagementIDLHelper.type ();
  }

}
