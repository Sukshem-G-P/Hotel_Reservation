import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_management";
    private static final String username = "root";
    private static final String password = "Supreeth2007";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            return;
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                switch (choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservation(connection, scanner);
                        break;
                    case 5:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        exit();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter customer name: ");
            String customerName = scanner.nextLine();
            System.out.print("Enter contact number: ");
            String contactNumber = scanner.nextLine();
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            System.out.print("Enter room ID: ");
            int roomId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            System.out.print("Enter check-in date (YYYY-MM-DD): ");
            String checkInDate = scanner.nextLine();
            System.out.print("Enter check-out date (YYYY-MM-DD): ");
            String checkOutDate = scanner.nextLine();

            // Insert customer information
            String insertCustomerSQL = "INSERT INTO customer (customer_name, contact_number, email) VALUES (?, ?, ?)";
            try (PreparedStatement customerStmt = connection.prepareStatement(insertCustomerSQL, Statement.RETURN_GENERATED_KEYS)) {
                customerStmt.setString(1, customerName);
                customerStmt.setString(2, contactNumber);
                customerStmt.setString(3, email);
                customerStmt.executeUpdate();
                ResultSet generatedKeys = customerStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int customerId = generatedKeys.getInt(1);

                    // Insert booking information
                    String insertBookingSQL = "INSERT INTO booking (customer_id, room_id, check_in_date, check_out_date) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement bookingStmt = connection.prepareStatement(insertBookingSQL)) {
                        bookingStmt.setInt(1, customerId);
                        bookingStmt.setInt(2, roomId);
                        bookingStmt.setString(3, checkInDate);
                        bookingStmt.setString(4, checkOutDate);

                        int affectedRows = bookingStmt.executeUpdate();
                        if (affectedRows > 0) {
                            System.out.println("Room reserved successfully!");
                        } else {
                            System.out.println("Room reservation failed.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection connection) {
        String sql = "SELECT booking_id, customer_name, room_id, check_in_date, check_out_date " +
                "FROM booking INNER JOIN customer ON booking.customer_id = customer.customer_id";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Current Reservations:");
            System.out.println("+--------------+----------------+---------+---------------+--------------+");
            System.out.println("| Booking ID   | Customer Name   | Room ID | Check-in Date | Check-out Date |");
            System.out.println("+--------------+----------------+---------+---------------+--------------+");

            while (resultSet.next()) {
                int bookingId = resultSet.getInt("booking_id");
                String customerName = resultSet.getString("customer_name");
                int roomId = resultSet.getInt("room_id");
                String checkInDate = resultSet.getString("check_in_date");
                String checkOutDate = resultSet.getString("check_out_date");

                System.out.printf("| %-12d | %-14s | %-7d | %-13s | %-13s |\n",
                        bookingId, customerName, roomId, checkInDate, checkOutDate);
            }

            System.out.println("+--------------+----------------+---------+---------------+--------------+");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter booking ID: ");
            int bookingId = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            String sql = "SELECT room_id FROM booking WHERE booking_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, bookingId);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    int roomId = resultSet.getInt("room_id");
                    System.out.println("Room number for Booking ID " + bookingId + " is: " + roomId);
                } else {
                    System.out.println("Booking not found for the given ID.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter booking ID to update: ");
            int bookingId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            if (!reservationExists(connection, bookingId)) {
                System.out.println("Booking not found for the given ID.");
                return;
            }

            System.out.print("Enter new check-in date (YYYY-MM-DD): ");
            String newCheckInDate = scanner.nextLine();
            System.out.print("Enter new check-out date (YYYY-MM-DD): ");
            String newCheckOutDate = scanner.nextLine();

            String sql = "UPDATE booking SET check_in_date = ?, check_out_date = ? WHERE booking_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, newCheckInDate);
                stmt.setString(2, newCheckOutDate);
                stmt.setInt(3, bookingId);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Booking updated successfully!");
                } else {
                    System.out.println("Booking update failed.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter booking ID to delete: ");
            int bookingId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (!reservationExists(connection, bookingId)) {
                System.out.println("Booking not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM booking WHERE booking_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, bookingId);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Booking deleted successfully!");
                } else {
                    System.out.println("Booking deletion failed.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int bookingId) {
        try {
            String sql = "SELECT booking_id FROM booking WHERE booking_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, bookingId);
                ResultSet resultSet = stmt.executeQuery();

                return resultSet.next();  // If there's a result, the booking exists
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        for (int i = 5; i > 0; i--) {
            System.out.print(".");
            Thread.sleep(1000);
        }
        System.out.println();
        System.out.println("Thank you for using the Hotel Reservation System!!!");
    }
}

