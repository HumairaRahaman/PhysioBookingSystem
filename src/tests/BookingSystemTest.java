package tests;


import model.*;
import system.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.List;

public class BookingSystemTest {
    private BookingSystem bookingSystem;
    private Physiotherapist physio1;
    private Physiotherapist physio2;
    private Patient patient1;
    private Patient patient2;
    private Treatment treatment1;
    private Treatment treatment2;
    private LocalDateTime now;

    @Before
    public void setUp() {
        bookingSystem = new BookingSystem();

        physio1 = new Physiotherapist("PT001", "John Smith", "123 Main St", "555-1111");
        physio1.addExpertise("Physiotherapy");
        physio1.addExpertise("Rehabilitation");

        physio2 = new Physiotherapist("PT002", "Helen Johnson", "456 Elm St", "555-2222");
        physio2.addExpertise("Osteopathy");

        patient1 = new Patient("PAT001", "Alice Brown", "789 Oak St", "555-3333");
        patient2 = new Patient("PAT002", "Bob Green", "321 Pine St", "555-4444");

        treatment1 = new Treatment("Massage", "Standard massage therapy");
        treatment2 = new Treatment("Acupuncture", "Traditional acupuncture treatment");

        now = LocalDateTime.now();

        bookingSystem.addPhysiotherapist(physio1);
        bookingSystem.addPhysiotherapist(physio2);
        bookingSystem.addPatient(patient1);
        bookingSystem.addPatient(patient2);
    }

    @Test
    public void testAddAndFindPhysiotherapist() {
        Physiotherapist found = bookingSystem.findPhysiotherapistById("PT001");
        assertEquals(physio1, found);

        found = bookingSystem.findPhysiotherapistByName("Helen Johnson");
        assertEquals(physio2, found);
    }

    @Test
    public void testAddAndFindPatient() {
        Patient found = bookingSystem.findPatientById("PAT001");
        assertEquals(patient1, found);

        found = bookingSystem.findPatientByName("Bob Green");
        assertEquals(patient2, found);
    }

    @Test
    public void testFindPhysiotherapistsByExpertise() {
        List<Physiotherapist> physios = bookingSystem.findPhysiotherapistsByExpertise("Physiotherapy");
        assertEquals(1, physios.size());
        assertTrue(physios.contains(physio1));

        physios = bookingSystem.findPhysiotherapistsByExpertise("Osteopathy");
        assertEquals(1, physios.size());
        assertTrue(physios.contains(physio2));
    }

    @Test
    public void testBookAppointment() {
        Appointment appointment = new Appointment(now, now.plusHours(1), physio1, treatment1);
        physio1.addAppointmentSlot(appointment);

        boolean result = bookingSystem.bookAppointment(appointment.getId(), patient1.getId());

        assertTrue(result);
        assertEquals(AppointmentStatus.BOOKED, appointment.getStatus());
        assertEquals(patient1, appointment.getPatient());
        assertTrue(patient1.getBookings().contains(appointment));
    }

    @Test
    public void testCancelAppointment() {
        Appointment appointment = new Appointment(now, now.plusHours(1), physio1, treatment1);
        physio1.addAppointmentSlot(appointment);

        bookingSystem.bookAppointment(appointment.getId(), patient1.getId());
        boolean result = bookingSystem.cancelAppointment(appointment.getId());

        assertTrue(result);
        assertEquals(AppointmentStatus.AVAILABLE, appointment.getStatus());
        assertNull(appointment.getPatient());
        assertFalse(patient1.getBookings().contains(appointment));
    }

    @Test
    public void testChangeAppointment() {
        Appointment oldAppointment = new Appointment(now, now.plusHours(1), physio1, treatment1);
        Appointment newAppointment = new Appointment(now.plusHours(2), now.plusHours(3), physio1, treatment1);

        physio1.addAppointmentSlot(oldAppointment);
        physio1.addAppointmentSlot(newAppointment);

        bookingSystem.bookAppointment(oldAppointment.getId(), patient1.getId());
        boolean result = bookingSystem.changeAppointment(oldAppointment.getId(), newAppointment.getId(), patient1.getId());

        assertTrue(result);
        assertEquals(AppointmentStatus.AVAILABLE, oldAppointment.getStatus());
        assertEquals(AppointmentStatus.BOOKED, newAppointment.getStatus());
        assertNull(oldAppointment.getPatient());
        assertEquals(patient1, newAppointment.getPatient());
        assertTrue(patient1.getBookings().contains(newAppointment));
        assertFalse(patient1.getBookings().contains(oldAppointment));
    }

    @Test
    public void testChangeAppointmentFailsWithTimeConflict() {
        Appointment appointment1 = new Appointment(now, now.plusHours(1), physio1, treatment1);
        Appointment appointment2 = new Appointment(now, now.plusHours(1), physio2, treatment2); // Same time
        Appointment appointment3 = new Appointment(now.plusHours(2), now.plusHours(3), physio1, treatment1);

        physio1.addAppointmentSlot(appointment1);
        physio2.addAppointmentSlot(appointment2);
        physio1.addAppointmentSlot(appointment3);

        assertTrue("Appointment 1 should be booked", bookingSystem.bookAppointment(appointment1.getId(), patient1.getId()));

        // Attempt to change to a conflicting appointment that overlaps in time
        boolean result = bookingSystem.changeAppointment(appointment1.getId(), appointment2.getId(), patient1.getId());

        assertFalse("Change should fail due to time conflict", result);
        assertEquals(AppointmentStatus.BOOKED, appointment1.getStatus());
        assertEquals(AppointmentStatus.AVAILABLE, appointment2.getStatus());
    }

    @Test
    public void testAttendAppointment() {
        Appointment appointment = new Appointment(now, now.plusHours(1), physio1, treatment1);
        physio1.addAppointmentSlot(appointment);

        bookingSystem.bookAppointment(appointment.getId(), patient1.getId());
        boolean result = bookingSystem.attendAppointment(appointment.getId());

        assertTrue(result);
        assertEquals(AppointmentStatus.ATTENDED, appointment.getStatus());
    }

    @Test
    public void testRemovePatient() {
        Appointment appointment = new Appointment(now, now.plusHours(1), physio1, treatment1);
        physio1.addAppointmentSlot(appointment);

        bookingSystem.bookAppointment(appointment.getId(), patient1.getId());
        bookingSystem.removePatient(patient1);

        assertFalse(bookingSystem.getPatients().contains(patient1));
        assertEquals(AppointmentStatus.AVAILABLE, appointment.getStatus());
        assertNull(appointment.getPatient());
    }
}
