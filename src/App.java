/*
 * Author : Abilash Raveendran and Sharan Balraj
 * Date: 09/03/2023
 * Project: Datbase Project 2(Yoober Application)
 */

 import java.sql.*;
 import java.util.Scanner;
 
 public class App {
     
     private static Connection conn = null;
 
     
 public static void main(String[] args) throws Exception{
     String menu = "A";
 
     String jdbcLocation = "jdbc:sqlite:src/yoober_project.db";
 
     try {
         conn = DriverManager.getConnection(jdbcLocation);
         System.out.println("Database connection successful!!");
     }
     catch (SQLException e) {
         System.out.println("Database connection failed!!");
         e.printStackTrace();
     }
         
     
     System.out.println("\n<<<Welcome to Abilash and Sharan's Yoober Database Application!>>>");
     System.out.println("------------------------------------------------------------------");
 
         
 
     while (!menu.equals("exit")) {
 
     System.out.println("Followings are the availble options for you to choose");
     System.out.println("1.View all account details");
     System.out.println("2.Calculate the average rating for a specific driver");
     System.out.println("3.Create a new account");
     System.out.println("4.Submit a ride request");
     System.out.println("5.Complete a ride");
 
     // INPUT 
     Scanner in = new Scanner(System.in);
     System.out.println("Select the option : ");
     menu = in.nextLine();
 
     // MENU OPTIONS
     switch(menu){
         case "1":
             accountDetails();
             break;
         case "2":
             averageRating();
             break;
         case "3":
             newAccount();
             break;
          case "4":
            rideRequest();
             break;
         case "5":
             completeRide();
             break;
         case "exit":
             System.out.println("\nExit successful!!");
             break;
         default:
             System.out.println("\nOption not available. Please enter a number between 1 and 5: ");
             
     }
 }
 }
 
 
 public static void accountDetails(){
 try {
     Statement statement = conn.createStatement();
     ResultSet resultSet = statement.executeQuery("SELECT accounts.FIRST_NAME,accounts.LAST_NAME,accounts.BIRTHDATE,accounts.PHONE_NUMBER,accounts.EMAIL,addresses.STREET,addresses.CITY,addresses.PROVINCE,addresses.POSTAL_CODE, CASE WHEN passengers.id IS NOT NULL AND drivers.id IS NOT NULL THEN 'Both' WHEN passengers.id IS NOT NULL THEN 'Passenger' ELSE 'Driver' END AS user_type FROM accounts  INNER JOIN addresses ON accounts.ADDRESS_ID = addresses.ID LEFT JOIN drivers ON drivers.ID = accounts.ID LEFT JOIN passengers ON passengers.ID = accounts.ID");
     while (resultSet.next()) {
         String firstName = resultSet.getString("FIRST_NAME");
         String lastName = resultSet.getString("LAST_NAME");
         String street = resultSet.getString("STREET");
         String city = resultSet.getString("CITY");
         String province = resultSet.getString("PROVINCE");
         String postalCode = resultSet.getString("POSTAL_CODE");
         String phoneNumber = resultSet.getString("PHONE_NUMBER");
         String email = resultSet.getString("EMAIL");
         String userType = resultSet.getString("user_type");
         System.out.println(" Name: " + firstName + " " + lastName+ ", Address: " + " " + street + " " + city + " " + province + " " + postalCode + ", Phone number: " + phoneNumber + ", Email: " + email + ", User Type: " + userType);
     }
     } catch (SQLException e) {
         e.printStackTrace();
     }
 
 
 }
 
 
 // public static void averageRating(){
     
 //     Scanner avg = new Scanner(System.in);
 //     System.out.println("Enter driver's email address: ");
 //     String email = avg.nextLine();
 // }
 public static void averageRating() {
     Scanner average = new Scanner(System.in);
 
     String getaverage = "SELECT email,avg(RATING_FROM_PASSENGER) FROM accounts as a inner join drivers as d on a.address_id = d.id inner join rides as r on d.id = r.driver_id where email = ? ";
     try (
         PreparedStatement preparedStatement = conn.prepareStatement(getaverage);) {
         System.out.println("Enter the email id of the Driver : ");
         String driverEmail = average.nextLine();
         preparedStatement.setString(1, driverEmail);
         try (ResultSet rs = preparedStatement.executeQuery();) {
             // If there is at least one row
             if (rs.next()) {
                 Double avgRate = rs.getDouble(2);
                 System.out.println("The Average rating for " + driverEmail + " is " + avgRate);
 
             } else {
                 System.out.println("This  " + driverEmail + " is not available database.");
 
             }
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     average.close();
 }
 
 public static void newAccount() {
 
     Scanner account = new Scanner(System.in);
 
     System.out.println("Enter first name: ");
     String firstName = account.nextLine();
 
     System.out.println("Enter last name: ");
     String lastName = account.nextLine();
 
     System.out.println("Enter birthdate (MMM DD, YYYY): ");
     String birthDate = account.nextLine();
 
     System.out.println("Enter street name: ");
     String street = account.nextLine();
 
     System.out.println("Enter city name: ");
     String city = account.nextLine();
 
     System.out.println("Enter province: ");
     String province = account.nextLine();
 
     System.out.println("Enter postal code: ");
     String postalCode = account.nextLine();
 
     System.out.println("Enter phone number: ");
     String phoneNumber = account.nextLine();
 
     System.out.println("Enter email address: ");
     String emailAddress = account.nextLine();
 
     String newAddress = "INSERT INTO addresses (STREET,CITY,PROVINCE,POSTAL_CODE) VALUES (?,? ,?,?)";
     try (PreparedStatement statement2 = conn.prepareStatement(newAddress, Statement.RETURN_GENERATED_KEYS);) {
         statement2.setString(1, street);
         statement2.setString(2, city);
         statement2.setString(3, province);
         statement2.setString(4, postalCode);
 
         statement2.executeUpdate();
         // try (ResultSet generatedKeys = statement2.getGeneratedKeys()) {
         // if (generatedKeys.next()) {
         // Id = generatedKeys.getInt(1);
         // } else {
         // throw new SQLException("Creating address failed, no ID obtained.");
         // }
 
         int Id = 0;
         try (ResultSet resultSet = statement2.getGeneratedKeys()) {
             if (resultSet.next()) {
                 Id = resultSet.getInt(1);
             }
 
         String newAccount = "INSERT INTO accounts(FIRST_NAME,LAST_NAME,BIRTHDATE,ADDRESS_ID,PHONE_NUMBER,EMAIL) VALUES (?,?,?,?,?,? )";
         try (PreparedStatement statement1 = conn.prepareStatement(newAccount);) {
             statement1.setString(1, firstName);
             statement1.setString(2, lastName);
             statement1.setString(3, birthDate);
             statement1.setInt(4, Id);
             statement1.setString(5, phoneNumber);
             statement1.setString(6, emailAddress);
             int rowsInserted1 = statement1.executeUpdate();
 
             if (rowsInserted1 > 0) {
                     System.out.println("New record has been inserted into the customers table.");
 
             }
 
             System.out.println("Will this account be used by a passenger, driver, or both? ");
             String accountType = account.nextLine().trim();
             if (accountType.equalsIgnoreCase("passenger")) {
             System.out.print("Enter credit card number: ");
             String creditCardNumber = account.nextLine();
 
             String passengerData = "INSERT INTO passengers (ID, CREDIT_CARD_NUMBER) VALUES (?, ?)";
             PreparedStatement statement3 = conn.prepareStatement(passengerData);
             statement3.setInt(1, Id);
             statement3.setString(2, creditCardNumber);
             statement3.executeUpdate();
 
             }else if(accountType.equalsIgnoreCase("driver")) {
                 System.out.print("Enter driver's license number: ");
                 String licenseNumber = account.nextLine();
 
                 System.out.print("Enter driver's license expiry date : ");
                 String licenseExpiry = account.nextLine();
                 String driverData = "INSERT INTO licenses (NUMBER, EXPIRY_DATE) VALUES (?, ?)";
                 PreparedStatement statement4 = conn.prepareStatement(driverData);
                 // statement4.setInt(1, Id);
                 statement4.setString(1, licenseNumber);
                 statement4.setString(2, licenseExpiry);
 
                 statement4.executeUpdate();
 
                 ResultSet lId = statement4.getGeneratedKeys();
                 int lID = lId.getInt(1);
 
                 String driverdata = "INSERT INTO drivers(ID, LICENSE_ID) VALUES(?,?)";
                 PreparedStatement statement5 = conn.prepareStatement(driverdata);
                 statement5.setInt(1, Id);
                 statement5.setInt(2, lID);
 
                 statement5.executeUpdate();
 
             }else if(accountType.equalsIgnoreCase("both")) {
 
             System.out.print("Enter credit card number: ");
             String creditCardNumber = account.nextLine();
 
             String passengerData = "INSERT INTO passengers (ID, CREDIT_CARD_NUMBER) VALUES (?, ?)";
             PreparedStatement statement3 = conn.prepareStatement(passengerData);
             statement3.setInt(1, Id);
             statement3.setString(2, creditCardNumber);
             statement3.executeUpdate();
 
             System.out.print("Enter driver's license number: ");
             String licenseNumber = account.nextLine();
 
             System.out.print("Enter driver's license expiry date : ");
             String licenseExpiry = account.nextLine();
             String driverData = "INSERT INTO licenses (NUMBER, EXPIRY_DATE) VALUES (?, ?)";
             PreparedStatement statement4 = conn.prepareStatement(driverData);
             // statement4.setInt(1, Id);
             statement4.setString(1, licenseNumber);
             statement4.setString(2, licenseExpiry);
             statement4.executeUpdate();
 
             ResultSet lId = statement4.getGeneratedKeys();
             int lID = lId.getInt(1);
 
             String driverdata = "INSERT INTO drivers(ID, LICENSE_ID) VALUES(?,?)";
                     
             PreparedStatement statement5 = conn.prepareStatement(driverdata);
             statement5.setInt(1, Id);
             statement5.setInt(2, lID);
             statement5.executeUpdate();
 
             }else{
                     System.out.println("Invalid Account Type");
 
             }
         }catch(SQLException ex){
             ex.printStackTrace();
 
         }
 
     }
 
     } catch(SQLException ex){
         ex.printStackTrace();
     }
 
     account.close();
 
    }

    public static void rideRequest(){

        Scanner complete = new Scanner(System.in);

        System.out.println("Your email here: ");
            String userEmail = complete.nextLine();
    }

 public static void completeRide(){
     //LIST OF UNCOMPLETED RIDES
     try {
        Statement statement = conn.createStatement();
     ResultSet rs = statement.executeQuery("SELECT ride_requests.ID,(SELECT FIRST_NAME FROM accounts WHERE accounts.ID = passengers.ID) AS FIRST_NAME,(SELECT LAST_NAME FROM accounts WHERE accounts.ID = passengers.ID) AS LAST_NAME,(SELECT STREET FROM addresses WHERE addresses.ID = ride_requests.PICKUP_LOCATION_ID) AS PICKUP_STREET,(SELECT CITY FROM addresses WHERE addresses.ID = ride_requests.PICKUP_LOCATION_ID) AS PICKUP_CITY,(SELECT STREET FROM addresses WHERE addresses.ID = ride_requests.DROPOFF_LOCATION_ID) AS DESTINATION_STREET,(SELECT CITY FROM addresses WHERE addresses.ID = ride_requests.DROPOFF_LOCATION_ID) AS DESTINATION_CITY,ride_requests.PICKUP_DATE,ride_requests.PICKUP_TIME FROM ride_requests LEFT JOIN rides ON ride_requests.ID = rides.REQUEST_ID LEFT JOIN passengers ON  ride_requests.PASSENGER_ID = passengers.ID WHERE rides.ID IS NULL");
     //ResultSet rs = statement.executeQuery(uncomplete);
     Scanner complete = new Scanner(System.in);
     while(rs.next()){
         int rideId = rs.getInt("id");
         String passengerFirstName = rs.getString("FIRST_NAME");
         String passengerLastName = rs.getString("LAST_NAME");
         String passengerStreet = rs.getString("PICKUP_STREET");
         String passengerCity = rs.getString("PICKUP_CITY");
         String pickupStreet = rs.getString("DESTINATION_STREET");
         String pickupCity = rs.getString("DESTINATION_CITY");
         String destinationStreet = rs.getString("PICKUP_DATE");
         String destinationCity = rs.getString("PICKUP_TIME");
         
 
         System.out.println("Ride ID: " + rideId);
         System.out.println("Passenger Name: " + passengerFirstName + " " + passengerLastName);
         System.out.println("Pick-up Address: " + passengerStreet + ", " + passengerCity);
         System.out.println("Destination Address: " + pickupStreet + ", " + pickupCity);
         System.out.println("Pick-up Date and Time: " + destinationStreet + ", " + destinationCity);
         
 
     }
 
     //PROMPT USER TO COMPLETE
     System.out.println("Enter the ID of the ride you want to complete: ");
     int rideIdToComplete = complete.nextInt();
 
     //Get email
     System.out.println("Enter the driver's email : ");
     String driverEmail = complete.next();
 
     //Get end date and time
     System.out.println("Enter the end date: ");
     String endDate = complete.next();
 
     System.out.println("Enter the end time: ");
     String endTime = complete.next();
 
     //Get distance travelled
     System.out.println("Enter the distance travelled: ");
     double distanceTravelled = complete.nextDouble();
 
     //Get Cost
     System.out.println("Enter the cost: ");
     double cost = complete.nextDouble();
 
     // Get driver's rating
     System.out.println("Enter the driver's rating: ");
     double driverRating = complete.nextDouble();
 
     // Get passenger's rating
     System.out.println("Enter the passenger's rating: ");
     double passangerRating = complete.nextDouble();

     //Get related driver
     String driverID = "Select accounts.ID from accounts inner join drivers ON accounts.ID=drivers.ID where accounts.EMAIL= ?";
     PreparedStatement preparedStatement = conn.prepareStatement(driverID,Statement.RETURN_GENERATED_KEYS);
     preparedStatement.setString(1, driverEmail);

     ResultSet driverid = preparedStatement.getGeneratedKeys();
        if (driverid.next()) {
            int driversId = driverid.getInt(1);

 
     // Update the ride in the database
     String updateSql = "INSERT INTO rides (DRIVER_ID,REQUEST_ID,ACTUAL_END_DATE,ACTUAL_END_TIME,RATING_FROM_DRIVER,RATING_FROM_PASSENGER,DISTANCE,CHARGE) VALUES (?,?,?,?,?,?,?,?)";
     PreparedStatement updatetable = conn.prepareStatement(updateSql);
     updatetable.setInt(1, driversId);
     updatetable.setInt(2, rideIdToComplete);
     updatetable.setString(3, endDate);
     updatetable.setString(4, endTime);
     updatetable.setDouble(5, driverRating);
     updatetable.setDouble(6, passangerRating);
     updatetable.setDouble(7, distanceTravelled);
     updatetable.setDouble(8, cost);
     //updatetable.setInt(9, rideIdToComplete);
 
     int rowsAffected = preparedStatement.executeUpdate();
 
     if(rowsAffected > 0){
         System.out.println("Ride completion is successful!");
     }else{
         System.out.println("Failed to complete ride.");
     }
 
 }   
 
}catch(SQLException ex){
    ex.printStackTrace();
 
}}}