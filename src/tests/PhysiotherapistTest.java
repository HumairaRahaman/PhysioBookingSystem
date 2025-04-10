package tests;

//package model;
import model.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.List;

public class PhysiotherapistTest {
    private Physiotherapist physiotherapist;
    private Treatment treatment;

    @Before
    public void setUp() {
        physiotherapist = new Physiotherapist("PT001", "John Smith", "123 Main St", "555-1111");
        treatment = new Treatment("Massage", "Standard massage therapy");
    }

    @Test
    public void testAddExpertise() {
        physiotherapist.addExpertise("Physiotherapy");
        assertTrue(physiotherapist.hasExpertise("Physiotherapy"));
        assertFalse(physiotherapist.hasExpertise("Acupuncture"));
    }

    @Test
    public void testRemoveExpertise() {
        physiotherapist.addExpertise("Physiotherapy");
        physiotherapist.addExpertise("Rehabilitation");

        assertTrue(physiotherapist.hasExpertise("Physiotherapy"));
        assertTrue(physiotherapist.hasExpertise("Rehabilitation"));

        physiotherapist.removeExpertise("Physiotherapy");

        assertFalse(physiotherapist.hasExpertise("Physiotherapy"));
        assertTrue(physiotherapist.hasExpertise("Rehabilitation"));
    }

    @Test
    public void testAddAppointmentSlot() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusHours(1);
        Appointment appointment = new Appointment(now, end, physiotherapist, treatment);

        physiotherapist.addAppointmentSlot(appointment);
        List<Appointment> timetable = physiotherapist.getTimetable();

        assertEquals(1, timetable.size());
        assertEquals(appointment, timetable.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddInvalidAppointment() {
        Physiotherapist otherPhysio = new Physiotherapist("PT002", "Jane Doe", "456 Elm St", "555-2222");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusHours(1);
        Appointment appointment = new Appointment(now, end, otherPhysio, treatment);

        physiotherapist.addAppointmentSlot(appointment);
    }

    @Test
    public void testGetAvailableAppointments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusHours(1);

        Appointment appointment1 = new Appointment(now, end, physiotherapist, treatment);
        Appointment appointment2 = new Appointment(now.plusHours(2), end.plusHours(2), physiotherapist, treatment);

        physiotherapist.addAppointmentSlot(appointment1);
        physiotherapist.addAppointmentSlot(appointment2);

        appointment1.book(new Patient("PAT001", "Test Patient", "Test Address", "555-0000"));

        List<Appointment> available = physiotherapist.getAvailableAppointments();

        assertEquals(1, available.size());
        assertEquals(appointment2, available.get(0));
    }
}
