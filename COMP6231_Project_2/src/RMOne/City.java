package RMOne;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.*;

import IDL.EventManagementIDLPOA;
import RMOne.Enums.CityCode;
import RMOne.Enums.EventType;

//Author SYC

public class City extends EventManagementIDLPOA {
	private ORB orb;
    Enums.CityCode CityCode;
    HashMap<String, User> users;
    public static volatile HashMap<EventType, HashMap<String,Event>> events = new HashMap<EventType, HashMap<String,Event>>();
	private int n;
	private User user;
    public static String folderpath = "G:\\";

    public City(Enums.CityCode citycode) throws RemoteException, Exception {
        CityCode = citycode;
        users = new HashMap<String, User>();
        events = new HashMap<EventType, HashMap<String, Event>>();
        events.put(EventType.SEMINAR, new HashMap<String, Event>());
        events.put(EventType.CONFERENCE, new HashMap<String, Event>());
        events.put(EventType.TRADESHOW, new HashMap<String, Event>());

        
        //prepopulate customers, managers and events
        users.putIfAbsent("TORC2345", new User("TORC2345"));
        users.putIfAbsent("MTLC2345", new User("MTLC2345"));
        users.putIfAbsent("OTWC1234", new User("OTWC1234"));
        users.put("TORM2345", new User("TORM2345"));
        users.put("MTLM1234", new User("MTLM1234"));
        users.put("OTWM4560", new User("OTWM4560"));

        
        //to be moved to client class
//        events.get(EventType.C).put("TORE080619", IDL.Event.parseEvent("TORE080619", EventType.C, 2));
//        events.get(EventType.S).put("TORE110619", IDL.Event.parseEvent("TORE110619", EventType.S, 1));
//        events.get(EventType.C).put("MTLA090619", IDL.Event.parseEvent("MTLA090619", EventType.C, 2));
//        events.get(EventType.T).put("MTLA080619", IDL.Event.parseEvent("MTLA080619", EventType.T, 1));  
//        events.get(EventType.C).put("OTWA190619", IDL.Event.parseEvent("OTWA190619", EventType.C, 1)); 
//        events.get(EventType.S).put("OTWA250619", IDL.Event.parseEvent("OTWA250619", EventType.S, 1));       
        //events.get(EventType.T).put("OTWA100619", IDL.Event.parseEvent("OTWA100619", EventType.T, 23));
        //events.get(EventType.T).put("TORA100619", IDL.Event.parseEvent("TORA100619", EventType.T, 23));
        //events.get(EventType.T).put("MTLA100619", IDL.Event.parseEvent("MTLA100619", EventType.T, 23));
    }
    
    public void SetORB(ORB orb)
    {
    	this.orb = orb;
    }

    public boolean WriteEventsLog() {
        try {
            FileOutputStream fileout = new FileOutputStream(new File("C:\\" + CityCode.toString() + "\\Events.txt"), true);
            ObjectOutputStream objout = new ObjectOutputStream(fileout);
            objout.writeObject(events);
            fileout.close();
            objout.close();
        } catch (IOException e) {
            System.out.println("There was an error writing log file to the server: " + e.getStackTrace());
            return false;
        }
        
        return true;
    }
    
    public boolean WriteToLog(String log, boolean withDate)
    {
    	try
    	{
    		String filename = folderpath + String.valueOf(CityCode) + "\\EventLog.txt";
    		File file = new File(filename);
    		if(!file.exists())
    		{
    			file.createNewFile();
    		}
    		FileWriter fw = new FileWriter(file, true);
    		BufferedWriter buf = new BufferedWriter(fw);
    		if(withDate)
    		{
    			buf.append("Date and time of request: " + String.valueOf(LocalDateTime.now()));
    			buf.newLine();;
    		}
    		buf.append(log);
    		buf.newLine();
    		buf.close();
    		return true;
    	}
    	catch(Exception e)
    	{
    		//e.printStackTrace();
    	}
    	return false;
    }
    
    

    @Override
    public String[] bookEvent(String ID, String eventID, String eventtype) {

    	n = 0;
    	user = users.get(ID);
    	String[] result = new String[2];
    	
    	if(user == null)
    	{
    		if(addUser(ID))
    			user = users.get(ID);
    	}
    	if(events.size() > 0)
    	{
        	events.forEach((k, v) -> 
        	{ 
        		HashMap<String, Event> eventsbytype = (HashMap<String, Event>)v;
        		
        		for(Map.Entry<String, Event> event : eventsbytype.entrySet())
        		{
        			Event ev = event.getValue();
        			String monthcurrentevent = ev.ID.substring(6,8);
        			String monthrequestedevent = eventID.substring(6,8);
        			if(ev.City != user.City && ev.BookedUserIDs.contains(ID) && 
        					monthcurrentevent.equals(monthrequestedevent))
        				n++;
        			//System.out.println("n: " + n);
        		}
        	});
        	
        	if(n >= 3 && !eventID.contains(user.getCity().toString()))
        	{
        		result[0] = "false";
        		result[1] = "You have already booked 3 events in other cities in the same month. The maximum is 3...";
        		//System.out.println("You have already booked 3 events in other cities. The maximum is 3...");
        		WriteToLog("You have already booked 3 events in other cities. The maximum is 3...", true);
        		return result;
        	}	
    	}
    	
    	try
        {
        	if(events.get(EventType.valueOf(eventtype)).get(eventID) == null)
        	{
        		result[0] = "false";
        		result[1] = "Event " + eventID + " does not exist in the system...";
        		return result;
        	}
    		
        	result[0] = String.valueOf(events.get(EventType.valueOf(eventtype)).get(eventID).addUser(ID));

    		WriteToLog("Request type: BookEvent", true);
    		WriteToLog("ClientID: " + ID, false);
    		WriteToLog("EventID: " + eventID, false);
    		
            if(result[0].equalsIgnoreCase("true"))
            {
            		WriteToLog("Result: succeeded", false);
            		WriteToLog("Server response: " + String.valueOf(true), false);
            		result[1] = "Event successfully booked for " + user.ID + " (eventID: " + eventID + ")..";
                     return result;
            }
            else
            {
                //System.out.println("There was an error booking " + ID + " for event " + eventID + "...");
                WriteToLog("Result: Failed: \"There was an error booking \"" + ID + " for event \"" + eventID + "...\"", false);
                result[1] = "There was an error booking " + ID + " for event " + eventID;
            }
            //return 1;
        }
        catch(Exception e)
        {
            //System.out.println("There was an error booking " + ID + " for event " + eventID + " : ");
            //e.printStackTrace();
            WriteToLog("Result: Failed with exception: " + e.getStackTrace(), false);
            result[0] = "false";
            result[1] = e.getLocalizedMessage();
        }
        
        return result;
    }

    @Override
    public String[] cancelEvent(String ID, String eventID, String eventtype) {
        String result[] = new String[2];
    	try
        {
        	if(events.get(EventType.valueOf(eventtype)).get(eventID) == null)
        	{
        		result[0] = "false";
        		result[1] = "Event " + eventID + " does not exist...";
        		return result;
        	}

    		WriteToLog("Request type: CancelEvent", true);
    		WriteToLog("ClientID: " + ID, false);
    		WriteToLog("EventID: " + eventID, false);
            if(events.get(EventType.valueOf(eventtype)).get(eventID).removeUser(ID))
            {
            	WriteToLog("Result: User: " + ID + " successfully cancelled event " + eventID, false);
            	result[1] = "Event " + eventID + " successfully cancelled for " + ID;
            	result[0] = "true";
            	return result;
            }
            else
            {
            	WriteToLog("Result: Event " + eventID + " could not be cancelled since the user did not book this event...", false);
            	result[1] = "Event " + eventID + " could not be cancelled since the user did not book this event...";
            	result[0] = "false";
            	return result;
            }
        }
        catch(Exception e)
        {
            result[1] = "There was an error cancelling the booking " + ID + " for event " + eventID + " : " + e.getStackTrace();
            result[0] = "false";
            WriteToLog("Exception cancelling event " + eventID + "for user " + ID, true);
        }
        
        return result;
    }

    @Override
    public String[] getBookingSchedule(String ID) {
        
    	ArrayList<String> result = new ArrayList<String>();
    	n = 0;

    	try
    	{
    		result.add("Booking schedule for " + ID);
    		System.out.println("Booking schedule for " + ID + ":");
            events.forEach((k, v) -> 
            { 
                ((HashMap<String, Event>)v).forEach((id, event) -> 
                {
                	Event ev = (Event)event;
                	if(ev.BookedUserIDs.contains(ID))
                	{
                		result.add(ev.ID+ ev.EventType.toString());
                		System.out.println(ev.ID+ ev.EventType.toString());
                		n++;
                	}
                });
            });	
    	}
    	catch(Exception e)
    	{
    		WriteToLog("There was an exception while getting the booking schedule for customer " + ID + ": " + e.getLocalizedMessage(), true);
    		System.out.println("There was an exception while getting the booking schedule for customer " + ID + ": " + e.getLocalizedMessage());
    		result.add("There was an exception while getting the booking schedule for customer " + ID + ": " + e.getLocalizedMessage());
            return result.toArray(new String[result.size()]);
    	}

        if(n == 0)
        {
        	result.add("This user did not book any events in the system...");
        	System.out.println("This user did not book any events in the system...");
        }
        
        WriteToLog("Request Type: GetBookingSchedule", true);
        WriteToLog("Booking schedule was successfully listed for customer " + ID, false);
        return result.toArray(new String[result.size()]);
    }
    
    @Override
    public String[] swapEvent(String customerID, String oldEventID, String oldEventType, String newEventID, String newEventType)
    {
    	String[] result = new String[2];
    	
		WriteToLog("Request type: SwapEvent", true);

		Event oldevent = events.get(EventType.valueOf(oldEventType)).get(oldEventID);
		if(oldevent == null)
		{
			WriteToLog("Old event does not exist, cancelling atomic swap...", false);
			result[1] = "Old event does not exist, cancelling atomic swap...";
			result[0] = "false";
			return result;
		}
		Event newevent = events.get(EventType.valueOf(newEventType)).get(newEventID);
		if(newevent == null)
		{
			result[0] = "false";
			result[1] = "New event does not exist, aborting atomic swap...";
			WriteToLog("New event does not exist, aborting atomic swap...", false);
		}
    	//synchronized(this)
    	{
        	if(oldevent.CanSwap(customerID, oldEventID, EventType.valueOf(oldEventType), newEventID, EventType.valueOf(newEventType))
        		&&
        	   newevent.CanSwap(customerID, oldEventID, EventType.valueOf(oldEventType), newEventID, EventType.valueOf(newEventType)))
        	{
        		if((bookEvent(customerID, newEventID, newEventType))[0].equals("true"))
        		{
        			if((cancelEvent(customerID, oldEventID, oldEventType))[0].equals("true"))
        			{
            			WriteToLog("Result: Swap events " + oldEventID + " and " + newEventID + " succeeded for customer " + customerID, false);
            			result[1] = "Swap events " + oldEventID + " and " + newEventID + " succeeded for customer " + customerID;
            			result[0] = "true";
        			}
        			else
        			{
        				if((cancelEvent(customerID, newEventID, newEventType))[0].equals("true"))
        				{
        					WriteToLog("Result: Could not cancel old event - aborting swap...", false);
        					result[1] = "Could not cancel old event - aborting swap...";
        					result[0] = "false";
        				}
        			}
        		}
        		else
        		{
        			WriteToLog("Result: Book event was impossible due to availability of the event, aborting swap", false);
        	    	result[1] = "Book event was impossible due to availability of the event, aborting swap";
        	    	result[0] = "false";
        	    	
        		}
        	}	
    	}
    	
    	return result;
    }

    @Override
    public boolean addEvent(String managerID, String eventID, String eventtype, int bookingCapacity) {
        try
        {
        	Event ev = events.get(EventType.valueOf(eventtype)).put(eventID, Event.parseEvent(eventID, EventType.valueOf(eventtype), bookingCapacity));

            {
        		WriteToLog("Request type: AddEvent", true);
        		WriteToLog("EventID: " + eventID, false);
        		WriteToLog("Result: Event " + eventID + " (" + String.valueOf(eventtype) + ") added...", false);
        		return true;
            }
        }
        catch(Exception e)
        {
        	WriteToLog("There was an error adding event : " + eventID + " to " + eventtype.toString() + " : " + e.getLocalizedMessage(), true);
            //e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeEvent(String managerID, String eventID, String eventtype) {
        try
        {
    		WriteToLog("Request type: RemoveEvent", true);
    		WriteToLog("EventID: " + eventID, false);
    		
    		HashMap<String, Event> eventsbytype = events.get(EventType.valueOf(eventtype));
            if(eventsbytype.remove(eventID) != null)
            {
                WriteToLog("Result: Event " + eventID + " (" + eventtype + ") removed...", false);
                return true;
            }
            else
            {
            	WriteToLog("Result: Event does not exist...", false);
            	return false;
            }
        }
        catch(Exception e)
        {
            WriteToLog("Result: Exception removing event " + eventID + " : " + e.getLocalizedMessage(), true);
            return false;
        }
    }

    @Override
    public String[] listEventAvailability(String managerID, String eventtype) {
    	
    	HashMap<String, Event> availableEvents = new HashMap<String, Event>(); 
    	if(eventtype.equalsIgnoreCase("A"))
    	{
    		availableEvents.putAll(events.get(EventType.CONFERENCE));
    		availableEvents.putAll(events.get(EventType.SEMINAR));
    		availableEvents.putAll(events.get(EventType.TRADESHOW));
    	}
    	else
    		availableEvents = events.get(EventType.valueOf(eventtype));
    	
        ArrayList<String> result = new ArrayList<String>();
        
        WriteToLog("Request type: ListEventAvailability", true);
        
        //if null, will throw exception at foreach in next statement => return empty hashmap
        if(availableEvents == null)
        {
        	result.add("Result: no events to display");
        	WriteToLog("Result: no events to display", false);
        	System.out.println("There are no events (" + eventtype + ") to display...");
        }
        
    	try
    	{
    		System.out.println(eventtype + " - ");
            availableEvents.forEach((k, v) -> 
            { 		
                	Event ev = (Event)v;
                	System.out.println(ev.ID + " " + ev.EventType.toString() + " " + String.valueOf(ev.BookingCapacity));
                	result.add(ev.ID + " " + ev.EventType.toString() + " " + String.valueOf(ev.BookingCapacity));
            });	
    	}
    	catch(Exception e)
    	{
    		WriteToLog("There was an error listing events: " + e.getLocalizedMessage(), false);
    		//e.printStackTrace();
    	}

        return result.toArray(new String[result.size()]);
    }
    
	public boolean addUser(String ID) {
		if(users.containsKey(ID))
		{
			System.out.println("User is already in the system...");
		}
		else
		{
			try
			{
				users.put(ID, new User(ID));
				WriteToLog("User: " + ID + " was added to the system...", true);
				System.out.println("User " + ID + " was added to the system...");
				return true;	
			}
			catch(Exception e)
			{
				return false;
			}
		}
		return false;
	}
	
	public User GetUser(String userID)
	{
		return users.get(userID);
	}

	@Override
	public void shutdown() {
		orb.shutdown(true);
	}
}