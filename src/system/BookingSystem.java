package system;

import model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BookingSystem {
    private List<Physiotherapist> physiotherapists;
    private List<Patient> patients;
    private List<Treatment> treatments;

    public BookingSystem() {
        this.physiotherapists = new ArrayList<>();
        this.patients = new ArrayList<>();
        this.treatments = new ArrayList<>();
        initializeTreatments();
    }

    private void initializeTreatments() {
        treatments.add(new Treatment("Neural mobilisation", "Technique to restore normal function of peripheral nerves"));
        treatments.add(new Treatment("Acupuncture", "Traditional Chinese medicine treatment using thin needles"));
        treatments.add(new Treatment("Massage", "Manual therapy to manipulate soft tissues of the body"));
        treatments.add(new Treatment("Mobilisation of the spine and joints", "Manual therapy to improve mobility in the spine or joints"));
        treatments.add(new Treatment("Pool rehabilitation", "Rehabilitation exercises performed in water"));
    }

    // Physiotherapist management
    public void addPhysiotherapist(Physiotherapist physiotherapist) {
        physiotherapists.add(physiotherapist);
    }

    public void removePhysiotherapist(Physiotherapist physiotherapist) {
        physiotherapists.remove(physiotherapist);
    }

    public Physiotherapist findPhysiotherapistById(String id) {
        for (Physiotherapist physio : physiotherapists) {
            if (physio.getId().equals(id)) {
                return physio;
            }
        }
        return null;
    }

    public Physiotherapist findPhysiotherapistByName(String name) {
        for (Physiotherapist physio : physiotherapists) {
            if (physio.getName().equalsIgnoreCase(name)) {
                return physio;
            }
        }
        return null;
    }

    public List<Physiotherapist> getPhysiotherapists() {
        return new ArrayList<>(physiotherapists);
    }

    public List<Physiotherapist> findPhysiotherapistsByExpertise(String expertise) {
        List<Physiotherapist> result = new ArrayList<>();
        for (Physiotherapist physio : physiotherapists) {
            if (physio.hasExpertise(expertise)) {
                result.add(physio);
            }
        }
        return result;
    }

    // Patient management
    public void addPatient(Patient patient) {
        patients.add(patient);
    }

    public void removePatient(Patient patient) {
        // Cancel all appointments of this patient before removing
        for (Appointment appointment : patient.getBookings()) {
            if (appointment.getStatus() == AppointmentStatus.BOOKED) {
                appointment.cancel();
                appointment.release();
            }
        }
        patients.remove(patient);
    }

    public Patient findPatientById(String id) {
        for (Patient patient : patients) {
            if (patient.getId().equals(id)) {
                return patient;
            }
        }
        return null;
    }

    public Patient findPatientByName(String name) {
        for (Patient patient : patients) {
            if (patient.getName().equalsIgnoreCase(name)) {
                return patient;
            }
        }
        return null;
    }

    public List<Patient> getPatients() {
        return new ArrayList<>(patients);
    }

    // Treatment management
    public List<Treatment> getTreatments() {
        return new ArrayList<>(treatments);
    }

    public Treatment findTreatmentByName(String name) {
        for (Treatment treatment : treatments) {
            if (treatment.getName().equalsIgnoreCase(name)) {
                return treatment;
            }
        }
        return null;
    }

    // Appointment management
    public List<Appointment> findAvailableAppointmentsByExpertise(String expertise) {
        List<Appointment> availableAppointments = new ArrayList<>();
        List<Physiotherapist> experts = findPhysiotherapistsByExpertise(expertise);

        for (Physiotherapist physio : experts) {
            availableAppointments.addAll(physio.getAvailableAppointments());
        }

        return availableAppointments;
    }

    public List<Appointment> findAvailableAppointmentsByPhysiotherapist(Physiotherapist physiotherapist) {
        return physiotherapist.getAvailableAppointments();
    }

    public Appointment findAppointmentById(String id) {
        for (Physiotherapist physio : physiotherapists) {
            for (Appointment appointment : physio.getTimetable()) {
                if (appointment.getId().equals(id)) {
                    return appointment;
                }
            }
        }
        return null;
    }

    public boolean bookAppointment(String appointmentId, String patientId) {
        Appointment appointment = findAppointmentById(appointmentId);
        Patient patient = findPatientById(patientId);

        if (appointment == null || patient == null) {
            return false;
        }

        // If patient already has an appointment at this time
        if (patient.hasAppointmentAt(appointment)) {
            return false;
        }

        return appointment.book(patient);
    }

    public boolean cancelAppointment(String appointmentId) {
        Appointment appointment = findAppointmentById(appointmentId);

        if (appointment == null || appointment.getStatus() != AppointmentStatus.BOOKED) {
            return false;
        }

        boolean cancelled = appointment.cancel();
        if (cancelled) {
            appointment.release();
        }
        return cancelled;
    }

    public boolean changeAppointment(String oldAppointmentId, String newAppointmentId, String patientId) {
        Appointment oldAppointment = findAppointmentById(oldAppointmentId);
        Appointment newAppointment = findAppointmentById(newAppointmentId);
        Patient patient = findPatientById(patientId);

        if (oldAppointment == null || newAppointment == null || patient == null) {
            return false;
        }

        // If the old appointment belongs to this patient
        if (oldAppointment.getPatient() == null || !oldAppointment.getPatient().equals(patient)) {
            return false;
        }

        // If the new appointment is available
        if (newAppointment.getStatus() != AppointmentStatus.AVAILABLE) {
            return false;
        }

        // If patient already has another appointment at this new time (excluding the old one)
        if (patient.hasAppointmentAt(newAppointment)) {
            return false;
        }

        // Cancel old appointment and book new one
        boolean cancelled = oldAppointment.cancel();
        if (!cancelled) {
            return false;
        }

        boolean released = oldAppointment.release();
        if (!released) {
            // Restore old appointment if release fails
            oldAppointment.book(patient);
            return false;
        }

        boolean booked = newAppointment.book(patient);
        if (!booked) {
            // restore old appointment if new booking fails
            oldAppointment.book(patient);
            return false;
        }

        return true;
    }

    public boolean attendAppointment(String appointmentId) {
        Appointment appointment = findAppointmentById(appointmentId);

        if (appointment == null) {
            return false;
        }

        return appointment.attend();
    }


    // Reporting
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== BOOST PHYSIO CLINIC REPORT ===\n\n");

        // List all appointments by physiotherapist
        report.append("APPOINTMENTS BY PHYSIOTHERAPIST\n");
        report.append("===============================\n");

        for (Physiotherapist physio : physiotherapists) {
            report.append("\nPhysiotherapist: ").append(physio.getName()).append("\n");
            report.append("----------------------------------------\n");

            List<Appointment> appointments = physio.getTimetable();
            if (appointments.isEmpty()) {
                report.append("No appointments scheduled.\n");
                continue;
            }

            for (Appointment appointment : appointments) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String dateTimeStr = appointment.getDateTime().format(formatter);
                String patientName = (appointment.getPatient() != null) ? appointment.getPatient().getName() : "N/A";

                report.append(String.format("ID: %s, Date/Time: %s, Treatment: %s, Patient: %s, Status: %s\n",
                        appointment.getId(), dateTimeStr, appointment.getTreatment().getName(),
                        patientName, appointment.getStatus()));
            }
        }

        // List physiotherapists in descending order of attended appointments
        report.append("\n\nPHYSIOTHERAPISTS BY ATTENDANCE\n");
        report.append("=============================\n");

        // Map of physiotherapist to attended appointment count
        Map<Physiotherapist, Integer> attendedCountMap = new HashMap<>();
        for (Physiotherapist physio : physiotherapists) {
            int attendedCount = 0;
            for (Appointment appointment : physio.getTimetable()) {
                if (appointment.getStatus() == AppointmentStatus.ATTENDED) {
                    attendedCount++;
                }
            }
            attendedCountMap.put(physio, attendedCount);
        }

        // Sort physiotherapists by attended count in descending order
        List<Map.Entry<Physiotherapist, Integer>> sortedEntries = new ArrayList<>(attendedCountMap.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        for (Map.Entry<Physiotherapist, Integer> entry : sortedEntries) {
            report.append(String.format("%s: %d attended appointments\n",
                    entry.getKey().getName(), entry.getValue()));
        }

        return report.toString();
    }

    // Initialize sample data
    public void initializeSampleData() {
        // Create physiotherapists
        Physiotherapist p1 = new Physiotherapist("PT_1", "Jhon Sanchez", "123 Main St", "555-1111");
        p1.addExpertise("Physiotherapy");
        p1.addExpertise("Rehabilitation");

        Physiotherapist p2 = new Physiotherapist("PT_2", "Helen Johnson", "456 Elm St", "555-2222");
        p2.addExpertise("Osteopathy");
        p2.addExpertise("Rehabilitation");

        Physiotherapist p3 = new Physiotherapist("PT_3", "David Lee", "789 Oak St", "555-3333");
        p3.addExpertise("Physiotherapy");
        p3.addExpertise("Acupuncture");

        addPhysiotherapist(p1);
        addPhysiotherapist(p2);
        addPhysiotherapist(p3);

        // Create patients
        for (int i = 1; i <= 10; i++) {
            Patient patient = new Patient(
                    String.format("PAT_%01d", i),
                    String.format("Patient %d", i),
                    String.format("%d City Street", i * 100),
                    String.format("555-%04d", i));
            addPatient(patient);
        }

        // Appointments for the 4-week timetable
        LocalDateTime startDate = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0);
        startDate = startDate.plusDays(1); // Start from tomorrow

        createTimetableForPhysiotherapist(p1, startDate);
        createTimetableForPhysiotherapist(p2, startDate);
        createTimetableForPhysiotherapist(p3, startDate);
    }

    private void createTimetableForPhysiotherapist(Physiotherapist physio, LocalDateTime startDate) {
        LocalDateTime currentDate = startDate;
        Random random = new Random();

        // 4-week timetable
        for (int week = 0; week < 4; week++) {
            // For each week, create 3-5 days of appointments
            int daysThisWeek = random.nextInt(3) + 3; // 3-5 days

            for (int day = 0; day < daysThisWeek; day++) {
                // Skip weekends (Saturday and Sunday)
                while (currentDate.getDayOfWeek().getValue() > 5) {
                    currentDate = currentDate.plusDays(1);
                }

                // For each day, create 2-4 appointment slots
                int slotsThisDay = random.nextInt(3) + 2; // 2-4 slots

                for (int slot = 0; slot < slotsThisDay; slot++) {
                    // Create an appointment at this time
                    Treatment treatment = treatments.get(random.nextInt(treatments.size()));
                    // 2-hour intervals
                    LocalDateTime appointmentStart = currentDate.plusHours(slot * 2);
                    // 1-hour appointments
                    LocalDateTime appointmentEnd = appointmentStart.plusHours(1);

                    Appointment appointment = new Appointment(appointmentStart, appointmentEnd, physio, treatment);
                    physio.addAppointmentSlot(appointment);
                }

                // Move to the next day
                currentDate = currentDate.plusDays(1);
            }

            // Move to the start of next week
            // Move to Monday
            while (currentDate.getDayOfWeek().getValue() != 1) {
                currentDate = currentDate.plusDays(1);
            }
        }
    }
}