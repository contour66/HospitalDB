CREATE TABLE person
(
  person_id INTEGER PRIMARY KEY,  
  first_name TEXT NOT NULL,
  last_name TEXT NOT NULL UNIQUE
);

CREATE TABLE patient
(
    person_id INTEGER PRIMARY KEY REFERENCES people(personid),
    ins_policy INTEGER,
    ins_name TEXT
);

CREATE TABLE doctor
(
    person_id INTEGER PRIMARY KEY REFERENCES people(person_id)
);

CREATE TABLE adminstrator
(
    person_id INTEGER PRIMARY KEY REFERENCES people(person_id)
);

CREATE TABLE technician
(
    person_id INTEGER PRIMARY KEY REFERENCES people(person_id)
);

CREATE TABLE volunteer
(
    person_id INTEGER PRIMARY KEY REFERENCES people(person_id)
);

CREATE TABLE nurse
(
    person_id INTEGER PRIMARY KEY REFERENCES people(person_id)
);


CREATE TABLE emergency_contact
(
    person_id INTEGER PRIMARY KEY REFERENCES people(person_id),
    phone_number TEXT,
    patient_id INTEGER,
    CONSTRAINT fk_patient_id FOREIGN KEY (patient_id) REFERENCES patient(person_id)
);


CREATE TABLE room(
    room_id INTEGER(20) PRIMARY KEY,
    occupied FALSE,
    patient_id INTEGER,
    CONSTRAINT fk_patient_id FOREIGN KEY(patient_id) REFERENCES patient(person_id)
);

CREATE TABLE admission_record (
    admission_record_id INTEGER PRIMARY KEY,
    date_admitted TEXT,
    date_discharged TEXT,
    diagnosis TEXT,
    discharged FALSE,
    patient_id INTEGER,
    doctor_id INTEGER,	
    room_number INTEGER(20),
    CONSTRAINT fk_patient_id FOREIGN KEY(patient_id) REFERENCES patient(person_id),
    CONSTRAINT fk_doctor_id FOREIGN KEY(doctor_id) REFERENCES doctor(person_id),
      CONSTRAINT fk_room_number FOREIGN KEY(room_number) REFERENCES room(room_id)
);



CREATE TABLE treatment (
    treatment_id INTEGER PRIMARY KEY,
    treatment_date TEXT,
    procedure_type TEXT,
    medication TEXT,
    treatment_completed FALSE,
    room_number INTEGER(20),
    patient_id INTEGER,
    admitting_doctor_id INTEGER,
    admission_record_id INTEGER,
    diagnosis TEXT,
    CONSTRAINT fk_room_number FOREIGN KEY(room_number) REFERENCES room(room_id),
    CONSTRAINT fk_patient_id FOREIGN KEY(patient_id) REFERENCES patient(person_id),
    CONSTRAINT fk_admitting_doctor_id FOREIGN KEY(admitting_doctor_id) REFERENCES doctor(person_id),
    CONSTRAINT fk_admission_record_id FOREIGN KEY(admission_record_id) REFERENCES admission_record(admission_record_id), 
    CONSTRAINT fk_diagnosis FOREIGN KEY(diagnosis) REFERENCES admission_record(diagnosis)  
);



ALTER TABLE patient RENAME TO _patient_old;

CREATE TABLE patient
(
   person_id INTEGER PRIMARY KEY REFERENCES people(personid),
   ins_policy INTEGER,
   ins_name TEXT,
   emergency_contact_id INTEGER,
   admission_record_id INTEGER,
   CONSTRAINT fk_emergency_contact_id FOREIGN KEY(emergency_contact_id) REFERENCES emergency_contact(person_id),
   CONSTRAINT fk_admission_record_id FOREIGN KEY(admission_record_id) REFERENCES admission_record(admission_record_id)
);



-- DROP TABLE person;
-- DROP TABLE _patient_old;
-- DROP TABLE adminstrator;
-- DROP TABLE admission_record;
-- DROP TABLE doctor;
-- DROP TABLE emergency_contact;
-- DROP TABLE nurse;
-- DROP TABLE patient;
-- DROP TABLE technician;
-- DROP TABLE treatment;
-- DROP TABLE volunteer;
-- DROP TABLE room;