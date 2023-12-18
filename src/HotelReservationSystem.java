import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {

    private static final String url="jdbc:mysql://localhost:3306/hoteldb";
    private static final String username="root";
    private static final String password="ghosh";


    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Drivers loaded successfully!!!");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try{
            Connection conn= DriverManager.getConnection(url,username,password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc= new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get room number");
                System.out.println("4. Upadte reservation");
                System.out.println("5. Delete reservation");
                System.out.println("0. Exit");
                System.out.println("Choose an option: ");
                int choice = sc.nextInt();
                switch(choice){
                    case 1:
                        reserveRoom(conn,sc);
                        break;
                    case 2:
                        viewReservations(conn);
                        break;
                    case 3:
                        getRoomNumber(conn,sc);
                        break;
                    case 4:
                        updateReservation(conn,sc);
                        break;
                    case 5:
                        deleteReservation(conn,sc);
                        break;
                    case 6:
                        exit();
                        sc.close();;
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");

                }
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    private static void reserveRoom(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO reservation (guest_name, room_number, contact_number) " +
                    "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation successful!");
                } else {
                    System.out.println("Reservation failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection conn) throws SQLException {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservation";

        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }
    }

    private static void getRoomNumber(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = sc.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = sc.nextLine();

            String sql = "SELECT room_number FROM reservation " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = conn.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void updateReservation(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = sc.nextInt();
          //  sc.nextLine(); // Consume the newline character

            if (!reservationExists(conn, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = sc.next();
            System.out.print("Enter new room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservation SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try (Statement statement = conn.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void deleteReservation(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = sc.nextInt();

            if (!reservationExists(conn, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservation WHERE reservation_id = " + reservationId;

            try (Statement statement = conn.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static boolean reservationExists(Connection conn, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservation WHERE reservation_id = " + reservationId;

            try (Statement statement = conn.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }
    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
}