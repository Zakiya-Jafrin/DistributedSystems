package IDL;

/**
* IDL/EventManagementIDLHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/schen/git/repository/COMP6231/COMP6231_Project_2/COMP6231_Project_2/src/EventManagementIDL.idl
* Friday, July 19, 2019 3:02:31 o'clock PM EDT
*/

public final class EventManagementIDLHolder implements org.omg.CORBA.portable.Streamable
{
  public IDL.EventManagementIDL value = null;

  public EventManagementIDLHolder ()
  {
  }

  public EventManagementIDLHolder (IDL.EventManagementIDL initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = IDL.EventManagementIDLHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    IDL.EventManagementIDLHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return IDL.EventManagementIDLHelper.type ();
  }

}
