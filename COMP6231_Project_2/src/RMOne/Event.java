package RMOne;

import java.util.ArrayList;
import java.util.List;

public class Event
{
    String ID;
    Enums.TimeOfDay Time;
    Enums.CityCode City;
    Enums.EventType EventType;
    volatile int BookingCapacity;
    List<String> BookedUserIDs;

    public Event(String id, int bookingCapacity,
    Enums.EventType eventtype) {
            if(id.length() != 10)
                System.out.println("Event ID must be 10 characters long...");
            else
            {
                ID = id;
                City = Enums.CityCode.valueOf(id.substring(0,3));
                BookingCapacity = bookingCapacity;
                EventType = eventtype;
                BookedUserIDs = new ArrayList<String>();
            }
            
	}

	public synchronized boolean addUser(String id)
    {
        int count = BookedUserIDs.size();
        int capacity = BookingCapacity;
        if(count < BookingCapacity)
        {
            if(!BookedUserIDs.contains(id))
            {
                BookedUserIDs.add(id);
                BookingCapacity--;
            }
            else
            {
                System.out.println("This user (" + id + ") has already booked this event...");
            }
        }
        return (BookedUserIDs.size() == count + 1) && (capacity == BookingCapacity + 1);
    }

    public synchronized boolean removeUser(String id)
    {
        int count = BookedUserIDs.size();
        if(BookedUserIDs.remove(id))
            BookingCapacity++;
        return (BookedUserIDs.size() == count - 1);

    }
    
    public synchronized boolean CanSwap(String userID, String oldEventID, Enums.EventType oldEventType, String newEventID, Enums.EventType newEventType)
    {
    	if(ID.equals(oldEventID) && EventType == oldEventType && BookedUserIDs.contains(userID))
    		return true;
    	if(ID.equals(newEventID) && EventType == newEventType && BookingCapacity >= 1)
    		return true;
    	
    	return false;
    }

    public String getID()
    {
        return ID;
    }

    public Enums.CityCode getCity()
    {
        return City;
    }

    public Enums.EventType getEventType()
    {
        return EventType;
    }

    public Enums.TimeOfDay getTimeOfDay()
    {
        return Time;
    }

    public int getBookingCapacity()
    {
        return BookingCapacity;
    }

    public List<String> getBookedUserIDs()
    {
        return BookedUserIDs;
    }
    
    public static Event parseEvent(String eventID, Enums.EventType eventtype, int bookingCapacity) {
        
        try
        {
            Event event = new Event(eventID, bookingCapacity,
                    eventtype);
    
            return event;
        }
        catch(Exception e)
        {
            System.out.println("Exception parsing event from event ID: " + e.getStackTrace());
        }

        return null;
    }

}