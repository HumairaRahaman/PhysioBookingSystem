package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private static int nextId = 1000;
    private String id;
    private LocalDateTime dateTime;
    private LocalDateTime endTime;
    private Physiotherapist physiotherapist;
    private Patient patient;
    private Treatment treatment;
    private AppointmentStatus status;

    public Appointment(LocalDateTime dateTime, LocalDateTime endTime, Physiotherapist physiotherapist, Treatment treatment) {
        this.id = "APT" + (nextId++);
        this.dateTime = dateTime;
        this.endTime = endTime;
        this.physiotherapist = physiotherapist;
        this.treatment = treatment;
        this.status = AppointmentStatus.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Physiotherapist getPhysiotherapist() {
        return physiotherapist;
    }

    public Patient getPatient() {
        return patient;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public boolean book(Patient patient) {
        if (status != AppointmentStatus.AVAILABLE) {
            return false;
        }
        this.patient = patient;
        this.status = AppointmentStatus.BOOKED;
        patient.addBooking(this);
        return true;
    }

    public boolean cancel() {
        if (status != AppointmentStatus.BOOKED) {
            return false;
        }
        this.status = AppointmentStatus.CANCELLED;
        return true;
    }

    public boolean attend() {
        if (status != AppointmentStatus.BOOKED) {
            return false;
        }
        this.status = AppointmentStatus.ATTENDED;
        return true;
    }

    public boolean release() {
        if (status == AppointmentStatus.ATTENDED) {
            return false;  // Cannot release an attended appointment
        }
        if (patient != null) {
            patient.removeBooking(this);
            patient = null;
        }
        status = AppointmentStatus.AVAILABLE;
        return true;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String patientInfo = (patient != null) ? patient.getName() : "Not booked";
        return String.format("ID: %s, %s - %s, Treatment: %s, Physiotherapist: %s, Patient: %s, Status: %s",
                id, dateTime.format(formatter), endTime.format(formatter),
                treatment.getName(), physiotherapist.getName(), patientInfo, status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Appointment that = (Appointment) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}