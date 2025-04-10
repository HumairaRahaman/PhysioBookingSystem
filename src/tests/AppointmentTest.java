package tests;
import model.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

public class AppointmentTest {
    private Physiotherapist physiotherapist;
    private Patient patient;
    private Treatment treatment;
    private Appointment appointment;
    private LocalDateTime now;
    private LocalDateTime end;

    @Before
    public void setUp() {
        physiotherapist = new Physiotherapist("PT001", "John Smith", "123 Main St", "555-1111");
        patient = new Patient("PAT001", "Jane Doe", "456 Elm St", "555-2222");
        treatment = new Treatment("Massage", "Standard massage therapy");
        now = LocalDateTime.now();
        end = now.plusHours(1);
        appointment = new Appointment(now, end, physiotherapist, treatment);
    }

    @Test
    public void testNewAppointmentIsAvailable() {
        assertEquals(AppointmentStatus.AVAILABLE, appointment.getStatus());
        assertNull(appointment.getPatient());
    }

    @Test
    public void testBookAppointment() {
        boolean result = appointment.book(patient);

        assertTrue(result);
        assertEquals(AppointmentStatus.BOOKED, appointment.getStatus());
        assertEquals(patient, appointment.getPatient());
        assertTrue(patient.getBookings().contains(appointment));
    }

    @Test
    public void testCannotBookAlreadyBookedAppointment() {
        appointment.book(patient);

        Patient anotherPatient = new Patient("PAT002", "Another Patient", "789 Oak St", "555-3333");
        boolean result = appointment.book(anotherPatient);

        assertFalse(result);
        assertEquals(patient, appointment.getPatient()); // Original patient still assigned
    }

    @Test
    public void testCancelAppointment() {
        appointment.book(patient);
        boolean result = appointment.cancel();

        assertTrue(result);
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        assertEquals(patient, appointment.getPatient()); // Patient still associated
    }

    @Test
    public void testCannotCancelAvailableAppointment() {
        boolean result = appointment.cancel();

        assertFalse(result);
        assertEquals(AppointmentStatus.AVAILABLE, appointment.getStatus());
    }

    @Test
    public void testAttendAppointment() {
        appointment.book(patient);
        boolean result = appointment.attend();

        assertTrue(result);
        assertEquals(AppointmentStatus.ATTENDED, appointment.getStatus());
    }

    @Test
    public void testCannotAttendUnbookedAppointment() {
        boolean result = appointment.attend();

        assertFalse(result);
        assertEquals(AppointmentStatus.AVAILABLE, appointment.getStatus());
    }

    @Test
    public void testReleaseAppointment() {
        appointment.book(patient);
        boolean result = appointment.release();

        assertTrue(result);
        assertEquals(AppointmentStatus.AVAILABLE, appointment.getStatus());
        assertNull(appointment.getPatient());
        assertFalse(patient.getBookings().contains(appointment));
    }

    @Test
    public void testCannotReleaseAttendedAppointment() {
        appointment.book(patient);
        appointment.attend();
        boolean result = appointment.release();

        assertFalse(result);
        assertEquals(AppointmentStatus.ATTENDED, appointment.getStatus());
    }
}