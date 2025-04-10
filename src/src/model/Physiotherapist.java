package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Physiotherapist extends Person {
    private Set<String> expertiseAreas;
    private List<Appointment> timetable;

    public Physiotherapist(String id, String name, String address, String phoneNumber) {
        super(id, name, address, phoneNumber);
        this.expertiseAreas = new HashSet<>();
        this.timetable = new ArrayList<>();
    }

    public void addExpertise(String expertise) {
        expertiseAreas.add(expertise);
    }

    public void removeExpertise(String expertise) {
        expertiseAreas.remove(expertise);
    }

    public Set<String> getExpertiseAreas() {
        return new HashSet<>(expertiseAreas);
    }

    public boolean hasExpertise(String expertise) {
        return expertiseAreas.contains(expertise);
    }

    public void addAppointmentSlot(Appointment appointment) {
        if (appointment.getPhysiotherapist().equals(this)) {
            timetable.add(appointment);
        } else {
            throw new IllegalArgumentException("Appointment must be associated with this physiotherapist");
        }
    }

    public void removeAppointmentSlot(Appointment appointment) {
        timetable.remove(appointment);
    }

    public List<Appointment> getTimetable() {
        return new ArrayList<>(timetable);
    }

    public List<Appointment> getAvailableAppointments() {
        List<Appointment> availableSlots = new ArrayList<>();
        for (Appointment appointment : timetable) {
            if (appointment.getStatus() == AppointmentStatus.AVAILABLE) {
                availableSlots.add(appointment);
            }
        }
        return availableSlots;
    }

    public List<Appointment> getAppointmentsInDateRange(LocalDateTime start, LocalDateTime end) {
        List<Appointment> appointmentsInRange = new ArrayList<>();
        for (Appointment appointment : timetable) {
            LocalDateTime appointmentTime = appointment.getDateTime();
            if (!appointmentTime.isBefore(start) && !appointmentTime.isAfter(end)) {
                appointmentsInRange.add(appointment);
            }
        }
        return appointmentsInRange;
    }

    @Override
    public String toString() {
        return super.toString() + ", Expertise: " + expertiseAreas;
    }
}