package com.school.student.query;

import com.school.common.query.FieldCatalog;
import com.school.common.query.FieldDef;

import java.util.List;

/**
 * Declares which {@link com.school.student.domain.Student} attributes are exposed to the
 * dynamic query engine — the security whitelist for filtering/sorting and the source of
 * truth for the client's column selector. Sensitive or heavy fields are simply omitted.
 */
public final class StudentFieldCatalog {

    private StudentFieldCatalog() {
    }

    public static final FieldCatalog CATALOG = FieldCatalog.builder()
            .field(FieldDef.id())
            .field(FieldDef.text("matricule", "student.matricule", true))
            .field(FieldDef.text("firstName", "student.firstName", true))
            .field(FieldDef.text("lastName", "student.lastName", true))
            .field(FieldDef.enumField("gender", "student.gender", true,
                    List.of("MALE", "FEMALE", "OTHER")))
            .field(FieldDef.date("dateOfBirth", "student.dateOfBirth", true))
            .field(FieldDef.text("placeOfBirth", "student.placeOfBirth", false))
            .field(FieldDef.text("nationality", "student.nationality", false))
            .field(FieldDef.text("countryName", "student.country", true))
            .field(FieldDef.text("regionName", "student.region", true))
            .field(FieldDef.text("city", "student.city", false))
            .field(FieldDef.text("phone", "student.phone", false))
            .field(FieldDef.text("email", "student.email", false))
            .field(FieldDef.uuid("currentClassroomId", "student.classroom", true))
            .field(FieldDef.enumField("status", "student.status", true,
                    List.of("PRE_REGISTERED", "REGISTERED", "SUSPENDED",
                            "TRANSFERRED", "GRADUATED", "WITHDRAWN")))
            .field(FieldDef.dateTime("createdAt", "common.createdAt", false))
            .field(FieldDef.dateTime("updatedAt", "common.updatedAt", false))
            .build();
}