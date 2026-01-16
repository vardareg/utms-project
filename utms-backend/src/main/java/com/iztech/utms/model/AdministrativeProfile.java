package com.iztech.utms.model;

import com.iztech.utms.model.UniversityStructure.Department;
import com.iztech.utms.model.UniversityStructure.Faculty;
import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to 'administrative_profiles' table.
 * Links a User (Dean/YGK) to a specific Faculty or Department.
 */
@Entity
@Table(name = "administrative_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministrativeProfile {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    // If set, user has access to all Departments in this Faculty (Faculty Dean
    // Scope)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    // If set, user has access ONLY to this Department (Department Dean Scope)
    // Takes precedence over faculty if both are somehow set (though usually
    // mutually exclusive or hierarchical)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}
