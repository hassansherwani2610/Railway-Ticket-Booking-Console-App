package org.example.app.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.entities.User;
import org.example.app.utils.PasswordHashUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserBookingService {
    private ObjectMapper objectMapper = new ObjectMapper(); // Converts JSON to Java OR JSON reader/writer
    private List<User> userList; // List of all users
    private User user; // To set user GLOBALLY

    private static final String USER_FILE_PATH = "app/src/main/java/org/example/app/localDb/users.json"; // Path of "users.json" - LOCAL DB

    // To load USERS from file
    private void loadUsersFromFile() throws IOException {
        userList = objectMapper.readValue(new File(USER_FILE_PATH), new TypeReference<List<User>>(){});
    }

    // To save "userList" in file
    private void savesUserListToFile() throws IOException {
        File usersFile = new File(USER_FILE_PATH);
        objectMapper.writeValue(usersFile , userList);
    }
    
    public UserBookingService(User user) throws IOException{
        this.user = user;
        loadUsersFromFile();
    }

    public UserBookingService() throws IOException {
        loadUsersFromFile();
    }

    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(u -> {
            return u.getName().equals(user.getName()) && PasswordHashUtil.checkPassword(user.getPassword() , u.getHashedPassword());
        }).findFirst();

        return foundUser.isPresent();
    }

    public Boolean signUpUser(User newUser){
        try{
            boolean userExistsAlready = userList.stream().anyMatch(u -> u.getName().equalsIgnoreCase(newUser.getName())); // Checking the "newUser" name while Ignoring Letter Case
            if (userExistsAlready){
                System.out.println("User already exists.");
                return Boolean.FALSE;
            }

            String hashedPassword = PasswordHashUtil.hashPassword(newUser.getPassword()); // To hash the user entered Plain Password
            newUser.setHashedPassword(hashedPassword); // To set Hashed Password in User Class
            newUser.setPassword(null); // To set Plain Password as NULL in User Class

            userList.add(newUser); // Save all data of NEW USER in "userList"

            savesUserListToFile(); // Again save that "userList" in "users.json" File

            return Boolean.TRUE;
        }
        catch (IOException exception){
            System.out.println("Error while signing up user.");
            return Boolean.FALSE;
        }
    }

    // Show all booked tickets of the currently logged-in user
    public void fetchBookings(){
        Optional<User> userFetched = userList.stream().filter(u -> {
            return u.getName().equals(user.getName()) && PasswordHashUtil.checkPassword(user.getPassword() , u.getHashedPassword());
        }).findFirst();

        if (userFetched.isPresent()){
            userFetched.get().printTickets();
        }
        else {
            System.out.println("Invalid credentials. Cannot fetch bookings.");
        }
    }

    // To cancel the ticket with the help of "ticketId" and save changes in the file (Local DB)
    public boolean cancelBooking(String ticketId){
        if (ticketId == null || ticketId.isEmpty()){ // To check if "ticketId" is null or empty
            System.out.println("Ticket ID cannot be null or empty.");
            return false;
        }

        // Find the logged-in user in the "userList"
        Optional<User> findLoggedInUser = userList.stream().filter(u ->
            u.getName().equals(user.getName()) && PasswordHashUtil.checkPassword(user.getPassword() , u.getHashedPassword())
        ).findFirst();

        // If Logged-in user is not found
        if (!findLoggedInUser.isPresent()){
            System.out.println("User not found.");
            return false;
        }

        User loggedInUser = findLoggedInUser.get(); // If Logged-in user is found

        // Remove that Logged-in user ticket with help of "ticketId"
        boolean removeTicket = loggedInUser.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(ticketId));


        if (removeTicket){ // If ticket is successfully removed
            try{ // Try to save changes in the file (Local DB)
                savesUserListToFile(); // Save the changes in the file (Local DB)
                System.out.println("Ticket with ID: " + ticketId + " - has been canceled.");
                return true;
            }
            catch (IOException exception){ // If changes is not saved in the file (Local DB)
                System.out.println("Failed to update booking data.");
                return false;
            }
        }
        else { // If ticket is not removed
            System.out.println("No ticket found with ID: " + ticketId);
            return false;
        }
    }
}