package com.school.student.mapper;

import com.school.student.domain.Classroom;
import com.school.student.domain.Guardian;
import com.school.student.domain.Student;
import com.school.student.domain.StudentDocument;
import com.school.student.domain.StudentGuardian;
import com.school.student.dto.ClassroomDto;
import com.school.student.dto.GuardianDto;
import com.school.student.dto.StudentDocumentDto;
import com.school.student.dto.StudentDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentMapper {

    public StudentDto toDto(Student s) {
        return StudentDto.builder()
                .id(s.getId())
                .matricule(s.getMatricule())
                .photoUrl(s.getPhotoUrl())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .gender(s.getGender())
                .dateOfBirth(s.getDateOfBirth())
                .placeOfBirth(s.getPlaceOfBirth())
                .nationality(s.getNationality())
                .address(s.getAddress())
                .countryCode(s.getCountryCode())
                .countryName(s.getCountryName())
                .regionId(s.getRegionId())
                .regionName(s.getRegionName())
                .cityId(s.getCityId())
                .city(s.getCity())
                .phone(s.getPhone())
                .email(s.getEmail())
                .currentClassroomId(s.getCurrentClassroomId())
                .status(s.getStatus())
                .notes(s.getNotes())
                .guardians(s.getGuardians() == null ? List.of()
                        : s.getGuardians().stream().map(this::toDto).toList())
                .documents(s.getDocuments() == null ? List.of()
                        : s.getDocuments().stream().map(this::toDto).toList())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    /**
     * Lightweight mapping for list/table rows: skips the lazy guardians/documents
     * collections (open-in-view is disabled) so the dynamic query stays a single,
     * cheap select per page.
     */
    public StudentDto toSummary(Student s) {
        return StudentDto.builder()
                .id(s.getId())
                .matricule(s.getMatricule())
                .photoUrl(s.getPhotoUrl())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .gender(s.getGender())
                .dateOfBirth(s.getDateOfBirth())
                .placeOfBirth(s.getPlaceOfBirth())
                .nationality(s.getNationality())
                .address(s.getAddress())
                .countryCode(s.getCountryCode())
                .countryName(s.getCountryName())
                .regionId(s.getRegionId())
                .regionName(s.getRegionName())
                .cityId(s.getCityId())
                .city(s.getCity())
                .phone(s.getPhone())
                .email(s.getEmail())
                .currentClassroomId(s.getCurrentClassroomId())
                .status(s.getStatus())
                .notes(s.getNotes())
                .guardians(List.of())
                .documents(List.of())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    public GuardianDto toDto(StudentGuardian sg) {
        Guardian g = sg.getGuardian();
        return GuardianDto.builder()
                .id(g.getId())
                .firstName(g.getFirstName())
                .lastName(g.getLastName())
                .phone(g.getPhone())
                .email(g.getEmail())
                .address(g.getAddress())
                .profession(g.getProfession())
                .employer(g.getEmployer())
                .nationalId(g.getNationalId())
                .relationship(sg.getRelationship())
                .primary(sg.isPrimary())
                .emergencyContact(sg.isEmergencyContact())
                .build();
    }

    public StudentDocumentDto toDto(StudentDocument d) {
        return StudentDocumentDto.builder()
                .id(d.getId())
                .type(d.getType())
                .label(d.getLabel())
                .fileUrl(d.getFileUrl())
                .contentType(d.getContentType())
                .sizeBytes(d.getSizeBytes())
                .verified(d.isVerified())
                .build();
    }

    public ClassroomDto toDto(Classroom c, long studentCount) {
        return ClassroomDto.builder()
                .id(c.getId())
                .code(c.getCode())
                .name(c.getName())
                .academicYearId(c.getAcademicYearId())
                .levelId(c.getLevelId())
                .trackId(c.getTrackId())
                .mainTeacherId(c.getMainTeacherId())
                .capacity(c.getCapacity())
                .studentCount(studentCount)
                .build();
    }
}
