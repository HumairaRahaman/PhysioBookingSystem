package ui;

import model.*;
import system.BookingSystem;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class BookingSystemUI {
    private BookingSystem bookingSystem;
    private Scanner scanner;
    private Patient currentPatient;

    public BookingSystemUI() {
        this.bookingSystem = new BookingSystem();
        this.scanner = new Scanner(System.in);
        this.currentPatient = null;

        // Initialize sample data
        bookingSystem.initializeSampleData();
    }

    public void start() {
        System.out.println("Welcome to Boost Physio Clinic Booking System");

        boolean running = true;
        while (running) {
            // First, select patient
            if (currentPatient == null) {
                selectPatient();
            }

            if (currentPatient != null) {
                displayMainMenu();
                int choice = getIntInput("Enter your choice: ");

                switch (choice) {
                    case 1:
                        bookAppointment();
                        break;
                    case 2:
                        changeOrCancelAppointment();
                        break;
                    case 3:
                        attendAppointment();
                        break;
                    case 4:
                        viewMyAppointments();
                        break;
                    case 5:
                        currentPatient = null; // logout
                        System.out.println("Logged out successfully.");
                        break;
                    case 9:
                        adminMenu();
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("Would you like to exit? (y/n)");
                String response = scanner.nextLine();
                if (response.equalsIgnoreCase("y")) {
                    running = false;
                }
            }
        }

        System.out.println("Thank you for using Boost Physio Clinic Booking System.");
        scanner.close();
    }

    private void selectPatient() {
        System.out.println("\n=== Patient Selection ===");
        System.out.println("1. Select existing patient");
        System.out.println("2. Add new patient");
        System.out.println("0. Back");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                selectExistingPatient();
                break;
            case 2:
                addNewPatient();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void selectExistingPatient() {
        List<Patient> patients = bookingSystem.getPatients();

        if (patients.isEmpty()) {
            System.out.println("No patients registered in the system.");
            return;
        }

        System.out.println("\n=== Available Patients ===");
        for (int i = 0; i < patients.size(); i++) {
            System.out.println((i + 1) + ". " + patients.get(i));
        }

        int patientIndex = getIntInput("Select a patient (1-" + patients.size() + "): ");

        if (patientIndex >= 1 && patientIndex <= patients.size()) {
            currentPatient = patients.get(patientIndex - 1);
            System.out.println("Selected patient: " + currentPatient.getName());
        } else {
            System.out.println("Invalid selection.");
        }
    }

    private void addNewPatient() {
        System.out.println("\n=== Add New Patient ===");

        String id = "PAT" + (bookingSystem.getPatients().size() + 1);

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter address: ");
        String address = scanner.nextLine();

        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();

        Patient newPatient = new Patient(id, name, address, phone);
        bookingSystem.addPatient(newPatient);

        System.out.println("Patient added successfully.");
        currentPatient = newPatient;
    }

    private void displayMainMenu() {
        System.out.println("\n=== Boost Physio Clinic Main Menu ===");
        System.out.println("Current Patient: " + currentPatient.getName());
        System.out.println("1. Book an appointment");
        System.out.println("2. Change or cancel an appointment");
        System.out.println("3. Attend an appointment");
        System.out.println("4. View my appointments");
        System.out.println("5. Logout (change patient)");
        System.out.println("9. Admin menu");
        System.out.println("0. Exit");
    }

    private void bookAppointment() {
        System.out.println("\n=== Book an Appointment ===");
        System.out.println("How would you like to view available appointments?");
        System.out.println("1. By expertise area");
        System.out.println("2. By physiotherapist name");
        System.out.println("0. Back to main menu");

        int choice = getIntInput("Enter your choice: ");

        List<Appointment> availableAppointments = null;

        switch (choice) {
            case 1:
                availableAppointments = viewAppointmentsByExpertise();
                break;
            case 2:
                availableAppointments = viewAppointmentsByPhysiotherapist();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
                return;
        }

        if (availableAppointments != null && !availableAppointments.isEmpty()) {
            System.out.println("\nAvailable Appointments:");
            displayAppointmentList(availableAppointments);

            String appointmentId = getStringInput("Enter the ID of the appointment you want to book (or 0 to cancel): ");

            if (!appointmentId.equals("0")) {
                boolean success = bookingSystem.bookAppointment(appointmentId, currentPatient.getId());

                if (success) {
                    System.out.println("Appointment booked successfully!");
                } else {
                    System.out.println("Failed to book appointment. It might already be booked or you have a time conflict.");
                }
            }
        } else {
            System.out.println("No available appointments found.");
        }
    }

    private List<Appointment> viewAppointmentsByExpertise() {
        System.out.println("\nAvailable Expertise Areas:");
        System.out.println("1. Physiotherapy");
        System.out.println("2. Rehabilitation");
        System.out.println("3. Osteopathy");
        System.out.println("4. Acupuncture");

        int choice = getIntInput("Select an expertise area (1-4): ");

        String expertise;
        switch (choice) {
            case 1:
                expertise = "Physiotherapy";
                break;
            case 2:
                expertise = "Rehabilitation";
                break;
            case 3:
                expertise = "Osteopathy";
                break;
            case 4:
                expertise = "Acupuncture";
                break;
            default:
                System.out.println("Invalid selection.");
                return null;
        }

        return bookingSystem.findAvailableAppointmentsByExpertise(expertise);
    }

    private List<Appointment> viewAppointmentsByPhysiotherapist() {
        List<Physiotherapist> physiotherapists = bookingSystem.getPhysiotherapists();

        System.out.println("\nAvailable Physiotherapists:");
        for (int i = 0; i < physiotherapists.size(); i++) {
            System.out.println((i + 1) + ". " + physiotherapists.get(i).getName());
        }

        int index = getIntInput("Select a physiotherapist (1-" + physiotherapists.size() + "): ");

        if (index >= 1 && index <= physiotherapists.size()) {
            Physiotherapist selected = physiotherapists.get(index - 1);
            return bookingSystem.findAvailableAppointmentsByPhysiotherapist(selected);
        } else {
            System.out.println("Invalid selection.");
            return null;
        }
    }

    private void changeOrCancelAppointment() {
        System.out.println("\n=== Change or Cancel an Appointment ===");

        List<Appointment> patientAppointments = currentPatient.getBookings();
        List<Appointment> bookedAppointments = patientAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.BOOKED)
                .collect(java.util.stream.Collectors.toList());

        if (bookedAppointments.isEmpty()) {
            System.out.println("You have no booked appointments.");
            return;
        }

        System.out.println("\nYour Booked Appointments:");
        displayAppointmentList(bookedAppointments);

        String appointmentId = getStringInput("Enter the ID of the appointment you want to change/cancel (or 0 to go back): ");

        if (appointmentId.equals("0")) {
            return;
        }

        Appointment selectedAppointment = bookingSystem.findAppointmentById(appointmentId);

        if (selectedAppointment == null || selectedAppointment.getStatus() != AppointmentStatus.BOOKED ||
                !selectedAppointment.getPatient().equals(currentPatient)) {
            System.out.println("Invalid appointment selection.");
            return;
        }

        System.out.println("\nSelected Appointment:");
        System.out.println(selectedAppointment);
        System.out.println("1. Change appointment");
        System.out.println("2. Cancel appointment");
        System.out.println("0. Back");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                changeAppointment(selectedAppointment);
                break;
            case 2:
                boolean success = bookingSystem.cancelAppointment(appointmentId);
                if (success) {
                    System.out.println("Appointment cancelled successfully!");
                } else {
                    System.out.println("Failed to cancel appointment.");
                }
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void changeAppointment(Appointment oldAppointment) {
        System.out.println("\n=== Change Appointment ===");
        System.out.println("How would you like to view available appointments?");
        System.out.println("1. By expertise area");
        System.out.println("2. By physiotherapist name");
        System.out.println("0. Back");

        int choice = getIntInput("Enter your choice: ");

        List<Appointment> availableAppointments = null;

        switch (choice) {
            case 1:
                availableAppointments = viewAppointmentsByExpertise();
                break;
            case 2:
                availableAppointments = viewAppointmentsByPhysiotherapist();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
                return;
        }

        if (availableAppointments != null && !availableAppointments.isEmpty()) {
            System.out.println("\nAvailable Appointments:");
            displayAppointmentList(availableAppointments);

            String newAppointmentId = getStringInput("Enter the ID of the new appointment (or 0 to cancel): ");

            if (!newAppointmentId.equals("0")) {
                boolean success = bookingSystem.changeAppointment(
                        oldAppointment.getId(),
                        newAppointmentId,
                        currentPatient.getId());

                if (success) {
                    System.out.println("Appointment changed successfully!");
                } else {
                    System.out.println("Failed to change appointment. The new appointment might be unavailable or you have a time conflict.");
                }
            }
        } else {
            System.out.println("No available appointments found.");
        }
    }

    private void attendAppointment() {
        System.out.println("\n=== Attend an Appointment ===");

        List<Appointment> patientAppointments = currentPatient.getBookings();
        List<Appointment> bookedAppointments = patientAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.BOOKED)
                .collect(java.util.stream.Collectors.toList());

        if (bookedAppointments.isEmpty()) {
            System.out.println("You have no booked appointments to attend.");
            return;
        }

        System.out.println("\nYour Booked Appointments:");
        displayAppointmentList(bookedAppointments);

        String appointmentId = getStringInput("Enter the ID of the appointment you want to mark as attended (or 0 to go back): ");

        if (appointmentId.equals("0")) {
            return;
        }

        Appointment selectedAppointment = bookingSystem.findAppointmentById(appointmentId);

        if (selectedAppointment == null || selectedAppointment.getStatus() != AppointmentStatus.BOOKED ||
                !selectedAppointment.getPatient().equals(currentPatient)) {
            System.out.println("Invalid appointment selection.");
            return;
        }

        boolean success = bookingSystem.attendAppointment(appointmentId);

        if (success) {
            System.out.println("Appointment marked as attended successfully!");
        } else {
            System.out.println("Failed to mark appointment as attended.");
        }
    }

    private void viewMyAppointments() {
        System.out.println("\n=== My Appointments ===");

        List<Appointment> patientAppointments = currentPatient.getBookings();

        if (patientAppointments.isEmpty()) {
            System.out.println("You have no appointments.");
            return;
        }

        System.out.println("\nAll Your Appointments:");
        displayAppointmentList(patientAppointments);

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void adminMenu() {
        boolean adminMode = true;

        while (adminMode) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. Add new patient");
            System.out.println("2. Remove patient");
            System.out.println("3. Print clinic report");
            System.out.println("0. Back to main menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addNewPatient();
                    break;
                case 2:
                    removePatient();
                    break;
                case 3:
                    System.out.println(bookingSystem.generateReport());
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    break;
                case 0:
                    adminMode = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void removePatient() {
        List<Patient> patients = bookingSystem.getPatients();

        if (patients.isEmpty()) {
            System.out.println("No patients to remove.");
            return;
        }

        System.out.println("\n=== Remove Patient ===");
        for (int i = 0; i < patients.size(); i++) {
            System.out.println((i + 1) + ". " + patients.get(i));
        }

        int index = getIntInput("Select a patient to remove (1-" + patients.size() + "): ");

        if (index >= 1 && index <= patients.size()) {
            Patient patientToRemove = patients.get(index - 1);

            // Check if this is the current patient
            if (patientToRemove.equals(currentPatient)) {
                System.out.println("Cannot remove the currently active patient.");
                return;
            }

            bookingSystem.removePatient(patientToRemove);
            System.out.println("Patient removed successfully.");
        } else {
            System.out.println("Invalid selection.");
        }
    }

    private void displayAppointmentList(List<Appointment> appointments) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Appointment appointment : appointments) {
            System.out.println("ID: " + appointment.getId() +
                    ", Date: " + appointment.getDateTime().format(formatter) +
                    ", Treatment: " + appointment.getTreatment().getName() +
                    ", Physiotherapist: " + appointment.getPhysiotherapist().getName() +
                    ", Status: " + appointment.getStatus());
        }
    }

    private int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}