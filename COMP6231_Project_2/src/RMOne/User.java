package RMOne;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;

public class User
{
    String ID;
    Enums.Role Role;
    Enums.CityCode City;  

    public User(String userID) throws Exception
    {
        if(userID.length() != 8)
            throw new Exception("There was an error creating user " + userID + " : Invalid ID...");
        try
        {
        	City = Enums.CityCode.valueOf(userID.substring(0,3));
        }
        catch(Exception e)
        {
        	System.out.println("Incorect message format...");
        	throw new Exception("There was an error creating user " + userID + " : Invalid City....");
        }
        	

        try
        {
            ID = userID;
            String rolechar = userID.substring(3, 4);
            if(rolechar.equals("M"))
            	Role = Role.Manager;
            else if(rolechar.equals("C"))
            	Role = Role.Customer;
            else
            	throw new Exception("Could not identify user type...");
            
            City = Enums.CityCode.valueOf(userID.substring(0, 3));

            //create log file
            String filename = "G:\\" + ID + ".txt";
            File file = new File(filename);
            boolean newUser = false;
            if(!file.exists())
            {
            	file.createNewFile();	
            	newUser = true;
            }
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            if(newUser)
            {
            	writer.write("User " + ID + " created...");
            	writer.newLine();
            }
            else
            {
            	 writer.write("User " + ID + " just logged in at " + String.valueOf(LocalDateTime.now()));
            	 writer.newLine();
            }
            writer.close();	
            	

            //TODO : add user to city's server
        }
        catch(Exception ex)
        {
            throw new Exception("Exception creating user: " + ex.getLocalizedMessage());
        }
    }

	public String getID()
    {
        return ID;
    }

    public Enums.Role getRole()
    {
        return Role;
    }

    public Enums.CityCode getCity()
    {
        return City;
    }
}