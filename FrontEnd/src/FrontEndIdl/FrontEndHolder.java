package FrontEndIdl;

/**
* FrontEndIdl/FrontEndHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/s_ursimr/git/DistributedSystemsProjectCOMP6231/ReplicaManager1FEandClient/src/FrontEnd.idl
* Monday, July 29, 2019 2:49:22 PM EDT
*/

public final class FrontEndHolder implements org.omg.CORBA.portable.Streamable
{
  public FrontEndIdl.FrontEnd value = null;

  public FrontEndHolder ()
  {
  }

  public FrontEndHolder (FrontEndIdl.FrontEnd initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = FrontEndIdl.FrontEndHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    FrontEndIdl.FrontEndHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return FrontEndIdl.FrontEndHelper.type ();
  }

}
