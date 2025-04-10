package model;

import java.util.ArrayList;
import java.util.List;

public class Patient extends Person {
    private List<Appointment> bookings;

    public Patient(String id, String name, String address, String phoneNumber) {
        super(id, name, address, phoneNumber);
        this.bookings = new ArrayList<>();
    }

    public List<Appointment> getBookings() {
        return new ArrayList<>(bookings);
    }

    public void addBooking(Appointment appointment) {
        bookings.add(appointment);
    }

    public void removeBooking(Appointment appointment) {
        bookings.remove(appointment);
    }

    public boolean hasAppointmentAt(Appointment newAppointment) {
        for (Appointment booking : bookings) {
            // Check if the booking is not cancelled and times overlap
            if (booking.getStatus() != AppointmentStatus.CANCELLED &&
                    booking.getDateTime().equals(newAppointment.getDateTime())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}