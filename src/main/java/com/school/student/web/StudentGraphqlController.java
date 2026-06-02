package com.school.student.web;

import com.school.common.dto.PageResponse;
import com.school.common.query.FieldDef;
import com.school.common.query.FilterGroup;
import com.school.common.query.PageInput;
import com.school.common.query.SortInput;
import com.school.student.dto.StudentDto;
import com.school.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL read-side for students: dynamic field selection (the GraphQL selection set),
 * filtering and sorting. Shares the same authorities as {@link StudentController}; writes
 * remain on the REST controller.
 */
@Controller
@RequiredArgsConstructor
public class StudentGraphqlController {

    private static final String READ =
            "hasAuthority('STUDENT_READ') or hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('SECRETARY') or hasRole('PEDAGOGIC_MANAGER') or hasRole('ACCOUNTANT')";

    private final StudentService service;

    @QueryMapping
    @PreAuthorize(READ)
    public PageResponse<StudentDto> students(@Argument FilterGroup filter,
                                             @Argument List<SortInput> sort,
                                             @Argument PageInput page) {
        return service.query(filter, sort, page);
    }

    @QueryMapping
    @PreAuthorize(READ)
    public List<FieldDef> studentFields() {
        return service.fields();
    }
}