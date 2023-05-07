
// author : Nimesha Udugama & Anson Joseph
// Purpose : Project 2
// Date : 27th march 2023

import java.sql.*; // Gives us access to all the database connection related code in the JDBC jar file
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {

    // Connection class represents a connection to the database
    private static Connection conn = null;

    public static void main(String[] args) throws Exception {
        String menuOption;

        // Define the loaction of the databse
        String databaseLocationUrl = "jdbc:sqlite:src/yoober_project.db";

        try {
            conn = DriverManager.getConnection(databaseLocationUrl); // initializing the connection
        } catch (SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }

        System.out.println(
                "=============================================================================================");
        System.out.println("Welcome to Yoober Database Application !.Created By Nimesha Udugama & Anson Joseph.");
        System.out.println(
                "==============================================================================================");
        System.out.println();

        Scanner input = new Scanner(System.in);
        try {
            do {
                System.out.println("Followings are the availble options for you to choose");
                System.out.println("1.View all account details");
                System.out.println("2.Calculate the average rating for a specific driver");
                System.out.println("3.Create a new account");
                System.out.println("4.Submit a ride request");
                System.out.println("5.Complete a ride");

                // Getting User input for the menu option
                System.out.println("Select the option : ");
                menuOption = input.nextLine();

                // Handle menu option

                switch (menuOption) {
                    case "1":
                        viewAllAccountDetails();
                        break;
                    case "2":
                        calculateAverageRating();
                        break;
                    case "3":
                        createNewAccount();
                        break;
                    case "4":
                        submitRideRequest();
                        break;
                    case "5":
                        completeRide();
                        break;
                    case "exit":
                        System.out.println("Exiting application.");
                        break;
                    default:
                        System.out.println("Invalid Option. Please enter a number between 1 and 5: ");
                        menuOption = input.nextLine();
                        break;
                }

            } while (!menuOption.equals("exit"));
        } finally {
            input.close();
        }
    }

    public static void viewAllAccountDetails() {

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT accounts.FIRST_NAME,accounts.LAST_NAME,accounts.BIRTHDATE,accounts.PHONE_NUMBER,accounts.EMAIL,addresses.STREET,addresses.CITY,addresses.PROVINCE,addresses.POSTAL_CODE, CASE WHEN passengers.id IS NOT NULL AND drivers.id IS NOT NULL THEN 'Both' WHEN passengers.id IS NOT NULL THEN 'Passenger' ELSE 'Driver' END AS user_type FROM accounts  INNER JOIN addresses ON accounts.ADDRESS_ID = addresses.ID LEFT JOIN drivers ON drivers.ID = accounts.ID LEFT JOIN passengers ON passengers.ID = accounts.ID");) {
            int rowNum = 0;
            // rs.next() returns true when there is a row of data to process
           
            while (rs.next()) {
                rowNum++;
                // Extracting data from a row in the ResultSet object
                
                String First_Name = rs.getString(1);
                String Last_Name = rs.getString(2);
                String Birth_Day = rs.getString(3);
                String Phone = rs.getString(4);
                String Email = rs.getString(5);
                String Street = rs.getString(6);
                String City = rs.getString(7);
                String Province = rs.getString(8);
                String Postal_Code = rs.getString(9);
                String User_Type = rs.getString("user_type");

                System.out.printf(
                        "Row #%d: First_Name:%s,Last Name: %s, Birth_Day:%s,Phone:%s ,Email:%s ,Address:%s, %s,%s,%s,UserType:%s \n",
                        rowNum, First_Name, Last_Name, Birth_Day, Phone, Email, Street, City, Province, Postal_Code,
                        User_Type);
            }
        } catch (SQLException e) {
            System.out.println("Oops, something went wrong: " + e.getMessage());
            System.exit(0);
        }

    }

    
    public static void calculateAverageRating() {

        Scanner in = new Scanner(System.in);
        String preparedSql = "SELECT avg(rides.RATING_FROM_PASSENGER)as average_rating,accounts.EMAIL from drivers INNER JOIN accounts on accounts.ID=drivers.ID left join rides on drivers.ID=rides.DRIVER_ID where accounts.EMAIL = ? GROUP BY accounts.EMAIL;";

        try (
                PreparedStatement preparedStatement = conn.prepareStatement(preparedSql);) {
            // in.nextLine();
            String email;
            boolean validEmail = false;
            do {
                System.out.print("Please enter the email of the driver: ");

                email = in.nextLine();
                preparedStatement.setString(1, email);
                try (ResultSet rs = preparedStatement.executeQuery();) {
                    if (rs.next()) {
                        validEmail = true;
                    } else {
                        System.out.println("Invalid email. Please try again.");

                    }
                }
            } while (!validEmail);

            try (ResultSet rs = preparedStatement.executeQuery();) {
                // If there is (at least) one row
                while (rs.next()) {
                    String average = rs.getString("average_rating");
                    // or rs.getString(1);
                    System.out.println("The average for " + email + " is " + average);
                }
            }

        } catch (SQLException e) {
            System.out.println("Oops, something went wrong: " + e.getMessage());
            System.exit(0);
        }
    }

    // public static void createNewAccount() {

    // Scanner input = new Scanner(System.in);

    // System.out.println("Enter First Name");
    // String firstName = input.nextLine();
    // System.out.println("Enter Last Name");
    // String lastName = input.nextLine();
    // System.out.println("Birthdate");
    // String birthDate = input.nextLine();
    // System.out.println("Street :");
    // String street = input.nextLine();
    // System.out.println("City");
    // String city = input.nextLine();
    // System.out.println("Province");
    // String province = input.nextLine();
    // System.out.println("Postal Code");
    // String postalCode = input.nextLine();
    // System.out.println("Phone Number");
    // String phoneNumber = input.nextLine();
    // System.out.println("Email");
    // String email = input.nextLine();

    // String insertaddressql = "INSERT INTO addresses
    // (STREET,CITY,PROVINCE,POSTAL_CODE) VALUES (?,? ,?,?)";
    // try (PreparedStatement insertaddStmt = conn.prepareStatement(insertaddressql,
    // Statement.RETURN_GENERATED_KEYS)) {
    // insertaddStmt.setString(1, street);
    // insertaddStmt.setString(2, city);
    // insertaddStmt.setString(3, province);
    // insertaddStmt.setString(4, postalCode);

    // insertaddStmt.executeUpdate();

    // if (insertaddStmt.executeUpdate() == 0) {
    // throw new SQLException("Creating address failed, no rows affected.");
    // }

    // try (ResultSet generatedKeys = insertaddStmt.getGeneratedKeys()) {
    // if (generatedKeys.next()) {
    // int addressId = generatedKeys.getInt(1);

    // String insertaccountsql = "INSERT INTO accounts
    // (FIRST_NAME,LAST_NAME,BIRTHDATE,ADDRESS_ID,PHONE_NUMBER,EMAIL) VALUES
    // (?,?,?,?,?,? );";
    // try (PreparedStatement insertaccStmt =
    // conn.prepareStatement(insertaccountsql)) {
    // insertaccStmt.setString(1, firstName);
    // insertaccStmt.setString(2, lastName);
    // insertaccStmt.setString(3, birthDate);
    // insertaccStmt.setInt(4, addressId);
    // insertaccStmt.setString(5, phoneNumber);
    // insertaccStmt.setString(6, email);

    // insertaccStmt.executeUpdate();

    // }
    // } else {
    // throw new SQLException("Creating address failed, no ID obtained.");
    // // executeUpdate() is used to run INSERT, UPDATE, and DELETE statements

    // }
    // }

    // catch (SQLException e) {
    // System.out.println("Oops, something went wrong: " + e.getMessage());
    // System.exit(0);
    // }

    // } catch (SQLException e) {
    // System.out.println("Something went wrong: " + e.getMessage());

    // }

    // input.close();

    // }

    public static void createNewAccount() {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter First Name");
        String firstName = input.nextLine();
        System.out.println("Enter Last Name");
        String lastName = input.nextLine();
        System.out.println("Birthdate");
        String birthDate = input.nextLine();
        System.out.println("Street :");
        String street = input.nextLine();
        System.out.println("City");
        String city = input.nextLine();
        System.out.println("Province");
        String province = input.nextLine();
        System.out.println("Postal Code");
        String postalCode = input.nextLine();
        System.out.println("Phone Number");
        String phoneNumber = input.nextLine();
        System.out.println("Email");
        String email = input.nextLine();

        String selectaddresssql = "SELECT ID FROM addresses WHERE STREET=? AND CITY=? AND PROVINCE=? AND POSTAL_CODE=?";
        String insertaddresssql = "INSERT INTO addresses (STREET,CITY,PROVINCE,POSTAL_CODE) VALUES (?,?,?,?)";
        try (PreparedStatement selectaddStmt = conn.prepareStatement(selectaddresssql);
                PreparedStatement insertaddStmt = conn.prepareStatement(insertaddresssql,
                        Statement.RETURN_GENERATED_KEYS)) {
            selectaddStmt.setString(1, street);
            selectaddStmt.setString(2, city);
            selectaddStmt.setString(3, province);
            selectaddStmt.setString(4, postalCode);

            ResultSet rs = selectaddStmt.executeQuery();
            int addressId;
            if (rs.next()) {
                addressId = rs.getInt("ID");
            } else {
                insertaddStmt.setString(1, street);
                insertaddStmt.setString(2, city);
                insertaddStmt.setString(3, province);
                insertaddStmt.setString(4, postalCode);

                insertaddStmt.executeUpdate();

                try (ResultSet generatedKeys = insertaddStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        addressId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating address failed, no ID obtained.");
                    }
                }
            }

            String insertaccountsql = "INSERT INTO accounts (FIRST_NAME,LAST_NAME,BIRTHDATE,ADDRESS_ID,PHONE_NUMBER,EMAIL) VALUES (?,?,?,?,?,?);";
            try (PreparedStatement insertaccStmt = conn.prepareStatement(insertaccountsql)) {
                insertaccStmt.setString(1, firstName);
                insertaccStmt.setString(2, lastName);
                insertaccStmt.setString(3, birthDate);
                insertaccStmt.setInt(4, addressId);
                insertaccStmt.setString(5, phoneNumber);
                insertaccStmt.setString(6, email);

                insertaccStmt.executeUpdate();

                try (ResultSet generatedKeys2 = insertaccStmt.getGeneratedKeys()) {
                    if (generatedKeys2.next()) {
                        int accountId = generatedKeys2.getInt(1);

                        // Ask if the new account will be used by a passenger, driver, or both.

                        System.out.println(
                                "Who is going to use this account? (P for passenger, D for driver, B for both):");

                        String userType = input.nextLine();

                        // usertype = Passanger
                        if (userType.equalsIgnoreCase("passenger")) {
                            System.out.println("Enter Credit Card Number:");
                            String creditCardNumber = input.nextLine();

                            String insertpassengerInfoSql = "INSERT INTO passangers (ID, CREDIT_CARD_NUMBER) VALUES (?,?)";
                            try (PreparedStatement insertpassangerInfoStmt = conn
                                    .prepareStatement(insertpassengerInfoSql)) {
                                insertpassangerInfoStmt.setInt(1, accountId);
                                insertpassangerInfoStmt.setString(2, creditCardNumber);

                                insertpassangerInfoStmt.executeUpdate();

                                System.out.println("Adding passanger is succesful");

                            }
                        }

                        // usertype = driver

                        else if (userType.equalsIgnoreCase("driver")) {
                            System.out.println("Enter Driver's License Number:");
                            String driverLicenseNumber = input.nextLine();
                            System.out.println("Enter Driver's License Expiry Date:");
                            String driverLicenseExpiry = input.nextLine();

                            String insertLicenseIfoSql = "INSERT INTO licenses(NUMBER, EXPIRY_DATE) VALUES(?,?)";
                            try (PreparedStatement insertLicenseIfoStmt = conn.prepareStatement(insertLicenseIfoSql)) {
                                insertLicenseIfoStmt.setString(1, driverLicenseNumber);
                                insertLicenseIfoStmt.setString(2, driverLicenseExpiry);

                                insertLicenseIfoStmt.executeUpdate();

                                try (ResultSet generatedKeys = insertLicenseIfoStmt.getGeneratedKeys()) {
                                    if (generatedKeys.next()) {
                                        int licensId = generatedKeys.getInt(1);

                                        String insertdriversIfoSql = "INSERT INTO drivers(ID, LICENSE_ID) VALUES(?,?)";
                                        try (PreparedStatement iinsertdriversIfoStmt = conn
                                                .prepareStatement(insertdriversIfoSql)) {
                                            iinsertdriversIfoStmt.setInt(1, accountId);
                                            iinsertdriversIfoStmt.setInt(2, licensId);

                                            iinsertdriversIfoStmt.executeUpdate();

                                            System.out.println("Adding driver is succesful");
                                        }
                                    }
                                }
                            }
                        }

                        // usertype = both
                        if (userType.equalsIgnoreCase("both")) {
                            System.out.println("Enter Credit Card Number:");
                            String creditCardNumber = input.nextLine();
                            String insertCreditInfoSql = "INSERT INTO passangers (ID, CREDIT_CARD_NUMBER) VALUES (?,?)";
                            try (PreparedStatement insertCreditInfoStmt = conn.prepareStatement(insertCreditInfoSql)) {
                                insertCreditInfoStmt.setInt(1, accountId);
                                insertCreditInfoStmt.setString(2, creditCardNumber);
                                insertCreditInfoStmt.executeUpdate();
                            }

                            System.out.println("Enter Driver's License Number:");
                            String driverLicenseNumber = input.nextLine();
                            System.out.println("Enter Driver's License Expiry Date:");
                            String driverLicenseExpiry = input.nextLine();
                            String insertDriverInfoSql = "INSERT INTO licenses(NUMBER, EXPIRY_DATE) VALUES(?,?)";
                            try (PreparedStatement insertLicenseIfoStmt = conn.prepareStatement(insertDriverInfoSql)) {

                                insertLicenseIfoStmt.setString(2, driverLicenseNumber);
                                insertLicenseIfoStmt.setString(3, driverLicenseExpiry);
                                insertLicenseIfoStmt.executeUpdate();

                                try (ResultSet generatedKeys = insertLicenseIfoStmt.getGeneratedKeys()) {
                                    if (generatedKeys.next()) {
                                        int licensId = generatedKeys.getInt(1);

                                        String insertdriversIfoSql = "INSERT INTO drivers(ID, LICENSE_ID) VALUES(?,?)";
                                        try (PreparedStatement iinsertdriversIfoStmt = conn
                                                .prepareStatement(insertdriversIfoSql)) {
                                            iinsertdriversIfoStmt.setInt(1, accountId);
                                            iinsertdriversIfoStmt.setInt(2, licensId);

                                            iinsertdriversIfoStmt.executeUpdate();

                                            System.out.println("Adding both is succesful");
                                        }
                                    }

                                }

                            }
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Something went wrong: " + e.getMessage());

                }
            } catch (SQLException e) {
                System.out.println("Something went wrong: " + e.getMessage());

            }
        } catch (SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());

        }
    }

    public static void submitRideRequest() throws SQLException {

        int passanger_id = 0;
        Scanner input = new Scanner(System.in);

        ResultSet resultset = null;
        String userEmail;
        do {
            System.out.println("Enter your email here: ");
            userEmail = input.nextLine();

            try {
                String checkEmail = " select accounts.EMAIL from accounts where EMAIL= ?";
                PreparedStatement preparedcheckmailStatement = conn.prepareStatement(checkEmail);
                preparedcheckmailStatement.setString(1, userEmail);
                resultset = preparedcheckmailStatement.executeQuery();

            } catch (SQLException e) {
                System.out.println("Oops, something went wrong: " + e.getMessage());
                System.exit(0);
            }

        } while (!resultset.next());

        System.out.println(
                "Do you need to select the destination from the specified passenger’s list of favourite destinations? (Yes/No)");
        String choice = input.nextLine();
        if (choice.toUpperCase().equals("YES")) {
            try {
                String insertEmail = "SELECT f.LOCATION_ID, f.name, a.CITY, a.STREET, a.PROVINCE, a.POSTAL_CODE,f.PASSENGER_ID FROM favourite_locations f LEFT JOIN addresses a ON f.LOCATION_ID = a.ID LEFT JOIN accounts ON a.ID = accounts.ADDRESS_ID WHERE EMAIL = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(insertEmail);
                preparedStatement.setString(1, userEmail);
                ResultSet rs = preparedStatement.executeQuery();
                if (!rs.next()) {
                    System.out.println("No results found.");
                    return;
                }
                int rowNum = 0;
                do {
                    rowNum++;
                    int location_id = rs.getInt(1);
                    String name = rs.getString(2);
                    String city = rs.getString(3);
                    String street = rs.getString(4);
                    String province = rs.getString(5);
                    String postal_code = rs.getString(6);
                    passanger_id = rs.getInt(7);
                    System.out.printf(
                            "Row #%d: ID: %d, Name: %s, City: %s, Street: %s, Province: %s, Postal Code: %s\n",
                            rowNum, location_id, name, city, street, province, postal_code);

                } while (rs.next());

                System.out.println(
                        "Enter the ID corresponding to your choice of destination from your list of favourites: ");
                int choiceId = input.nextInt();
                input.nextLine(); // clear input buffer

            } catch (SQLException e) {
                System.out.println("Oops, something went wrong: " + e.getMessage());
                System.exit(0);
            }
        } else {
            System.out.println("Enter your full address here: ");
            System.out.println("Street : ");
            String street = input.nextLine();
            System.out.println("City : ");
            String city = input.nextLine();
            System.out.println("Province : ");
            String province = input.nextLine();
            System.out.println("Postal Code : ");
            String postalCode = input.nextLine();

            System.out.println("Do you want to make this destination a new favourite? (Yes/No)");
            String Choice = input.nextLine();

            if (Choice.toUpperCase().equals("YES")) {
                System.out.println("Give a name to your favourite Destinaltion : ");
                String destinationName = input.nextLine();

                // Add new favourite location address to the address table
                String insertnewaddressql = "INSERT INTO addresses (STREET,CITY,PROVINCE,POSTAL_CODE) VALUES (?,? ,?,?)";
                try (PreparedStatement insertnewaddStmt = conn.prepareStatement(insertnewaddressql,
                        Statement.RETURN_GENERATED_KEYS)) {
                    insertnewaddStmt.setString(1, street);
                    insertnewaddStmt.setString(2, city);
                    insertnewaddStmt.setString(3, province);
                    insertnewaddStmt.setString(4, postalCode);

                    insertnewaddStmt.executeUpdate();

                    try (ResultSet generatedKeys = insertnewaddStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int addressId = generatedKeys.getInt(1);

                            // Add new favourite location to the favourite location table
                            String insertnewfavouriteplacessql = "INSERT INTO favourite_locations (PASSENGER_ID,LOCATION_ID,NAME) VALUES (?,?,?)";
                            try (PreparedStatement insertfavlocStmt = conn.prepareStatement(insertnewfavouriteplacessql,
                                    Statement.RETURN_GENERATED_KEYS)) {
                                insertfavlocStmt.setInt(1, passanger_id);
                                insertfavlocStmt.setInt(2, addressId);
                                insertfavlocStmt.setString(3, destinationName);

                                insertfavlocStmt.executeUpdate();

                                System.out.println("The favourite location added succesfully");

                                System.out.println("Enter desire pick up date :");
                                String pickupDate = input.nextLine();

                                System.out.println("Enter desired pickup time : ");
                                String pickupTime = input.nextLine();

                            } catch (SQLException e) {
                                System.out.println("Oops, something went wrong: " + e.getMessage());
                                System.exit(0);

                            }
                        } else {

                            System.out.println("Insertion failed");
                        }
                    }

                } catch (SQLException e) {
                    System.out.println("Oops, something went wrong: " + e.getMessage());
                    System.exit(0);

                }
            } else {

                System.out.println("This is invalid input");
            }
        }
    }

    public static void completeRide() {

        Scanner input = new Scanner(System.in);

        String userEmail = "";
        // ResultSet rs = null;
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT ride_requests.ID,accounts.FIRST_NAME, accounts.LAST_NAME,pickup_location.STREET AS PICKUP_STREET, pickup_location.CITY AS PICKUP_CITY,dropoff_location.STREET AS DESTINATION_STREET,dropoff_location.CITY AS DESTINATION_CITY,ride_requests.PICKUP_DATE,ride_requests.PICKUP_TIME FROM ride_requests LEFT JOIN rides ON ride_requests.ID = rides.REQUEST_ID LEFT JOIN addresses AS pickup_location ON ride_requests.PICKUP_LOCATION_ID = pickup_location.ID LEFT JOIN addresses AS dropoff_location ON ride_requests.DROPOFF_LOCATION_ID = dropoff_location.ID LEFT JOIN passengers ON ride_requests.PASSENGER_ID = passengers.ID LEFT JOIN accounts ON passengers.ID = accounts.ID WHERE rides.ID IS NULL;");) {

            if (!rs.isBeforeFirst()) {
                System.out.println("There are no uncompleted rides.");
                System.out.println();

            } else {

                int rowNum = 0;

                // rs.next() returns true when there is a row of data to process
                // It will return false once you reach the end of your ResultSet
                while (rs.next()) {

                    rowNum++;
                    // Extracting data from a row in the ResultSet object
                    // Column indexing for ResultSets start at 1, not 0
                    String ID = rs.getString(1);
                    String First_Name = rs.getString(2);
                    String Last_Name = rs.getString(3);
                    String Pickup_Street = rs.getString(4);
                    String Pickup_City = rs.getString(5);
                    String Destination_Street = rs.getString(6);
                    String Destination_City = rs.getString(7);
                    String Pickup_Date = rs.getString(8);
                    String Pickup_Time = rs.getString(9);

                    System.out.printf(
                            "Row #%d: ID : %s, First_Name: %s, Last_Name : %s, Pickup_Street : %s , Pickup_City : %s , Destination_Street : %s,Destination_City :  %s ,Pickup_Date: %s ,Pickup_Time :  %s \n",
                            rowNum, ID, First_Name, Last_Name, Pickup_Street, Pickup_City, Destination_Street,
                            Destination_City, Pickup_Date, Pickup_Time);
                }

                // ask user to select the ID
                System.out.print("Enter the ID of the ride you want to complete: ");
                int rideId = input.nextInt();
                input.nextLine(); // flush the buffer

                // Retrieve the necessary information from the user
                System.out.print("Enter the driver's email address: ");
                String driverEmail = input.nextLine();

                System.out.print("Enter the end date (yyyy-MM-dd) HH:mm:ss): ");
                String endDate = input.nextLine();

                System.out.print("Enter the end time (HH:mm:ss): ");
                String endTime = input.nextLine();

                System.out.print("Enter the driver's rating (0-5): ");
                double driverRating = input.nextDouble();

                System.out.print("Enter the passenger's rating (0-5): ");
                double passengerRating = input.nextDouble();

                System.out.print("Enter the distance travelled: ");
                double distance = input.nextDouble();

                System.out.print("Enter the cost: ");
                double charge = input.nextDouble();

                // get the driver id

                String driverIdsql = "Select accounts.ID from accounts inner join drivers ON accounts.ID=drivers.ID where accounts.EMAIL= ?";
                try (PreparedStatement driverIdStmt = conn.prepareStatement(driverIdsql,
                        Statement.RETURN_GENERATED_KEYS)) {
                    driverIdStmt.setString(1, driverEmail);

                    // driverIdStmt.executeUpdate();

                    try (ResultSet generatedKeys = driverIdStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int getdriverId = generatedKeys.getInt(1);

                            // Insert data to rides table to comlete the request
                            String insertRidesssql = "INSERT INTO rides (DRIVER_ID,REQUEST_ID,ACTUAL_END_DATE,ACTUAL_END_TIME,RATING_FROM_DRIVER,RATING_FROM_PASSENGER,DISTANCE,CHARGE) VALUES (?,?,?,?,?,?,?,?)";
                            try (PreparedStatement insertridesStmt = conn.prepareStatement(insertRidesssql,
                                    Statement.RETURN_GENERATED_KEYS)) {
                                insertridesStmt.setInt(1, getdriverId);
                                insertridesStmt.setInt(2, rideId);
                                insertridesStmt.setString(3, endDate);
                                insertridesStmt.setString(4, endTime);
                                insertridesStmt.setDouble(5, driverRating);
                                insertridesStmt.setDouble(6, passengerRating);
                                insertridesStmt.setDouble(7, distance);
                                insertridesStmt.setDouble(8, charge);

                                insertridesStmt.executeUpdate();

                                System.out.println("The ride completed succesfully");

                            } catch (SQLException e) {
                                System.out.println("Oops, something went wrong: " + e.getMessage());
                                System.exit(0);
                            }

                        }
                    } catch (SQLException e) {
                        System.out.println("Oops, something went wrong: " + e.getMessage());
                        System.exit(0);
                    }
                } catch (SQLException e) {
                    System.out.println("Oops, something went wrong: " + e.getMessage());
                    System.exit(0);
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
