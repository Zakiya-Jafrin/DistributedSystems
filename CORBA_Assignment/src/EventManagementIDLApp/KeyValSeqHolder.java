package EventManagementIDLApp;


/**
* EventManagementIDLApp/KeyValSeqHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /Users/zakiyajafrin/Documents/COMP6231-DS/CORBA_Assignment/src/EventManagementIDL.idl
* Monday, July 8, 2019 3:20:11 o'clock AM EDT
*/

public final class KeyValSeqHolder implements org.omg.CORBA.portable.Streamable
{
  public String value[] = null;

  public KeyValSeqHolder ()
  {
  }

  public KeyValSeqHolder (String[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = EventManagementIDLApp.KeyValSeqHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    EventManagementIDLApp.KeyValSeqHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return EventManagementIDLApp.KeyValSeqHelper.type ();
  }

}
