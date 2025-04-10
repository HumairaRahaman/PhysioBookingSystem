package tests;

//package model;
import model.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

public class PatientTest {
    private Patient patient;
    private Physiotherapist physio;
    private Treatment treatment;

    @Before
    public void setUp() {
        patient = new Patient("PAT001", "Jane Doe", "123 Main St", "555-1111");
        physio = new Physiotherapist("PT001", "John Smith", "456 Elm St", "555-2222");
        treatment = new Treatment("Massage", "Standard massage therapy");
    }

    @Test
    public void testAddAndRemoveBooking() {
        LocalDateTime now = LocalDateTime.now();
        Appointment appointment = new Appointment(now, now.plusHours(1), physio, treatment);

        patient.addBooking(appointment);
        assertTrue(patient.getBookings().contains(appointment));

        patient.removeBooking(appointment);
        assertFalse(patient.getBookings().contains(appointment));
    }

    @Test
    public void testHasAppointmentAt() {
        LocalDateTime now = LocalDateTime.now();
        Appointment appointment1 = new Appointment(now, now.plusHours(1), physio, treatment);
        Appointment appointment2 = new Appointment(now, now.plusHours(1), physio, treatment);
        Appointment appointment3 = new Appointment(now.plusHours(2), now.plusHours(3), physio, treatment);

        appointment1.book(patient);

        assertTrue(patient.hasAppointmentAt(appointment2)); // Same time as appointment1
        assertFalse(patient.hasAppointmentAt(appointment3)); // Different time

        appointment1.cancel();
        assertFalse(patient.hasAppointmentAt(appointment2)); // Cancelled appointments don't count
    }
}