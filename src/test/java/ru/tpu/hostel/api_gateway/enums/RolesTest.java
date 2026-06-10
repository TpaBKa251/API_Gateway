package ru.tpu.hostel.api_gateway.enums;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.tpu.hostel.api_gateway.enums.Roles.ADMINISTRATION;
import static ru.tpu.hostel.api_gateway.enums.Roles.FLOOR_SUPERVISOR;
import static ru.tpu.hostel.api_gateway.enums.Roles.HOSTEL_SUPERVISOR;
import static ru.tpu.hostel.api_gateway.enums.Roles.RESPONSIBLE_GYM;
import static ru.tpu.hostel.api_gateway.enums.Roles.RESPONSIBLE_KITCHEN;
import static ru.tpu.hostel.api_gateway.enums.Roles.STUDENT;
import static ru.tpu.hostel.api_gateway.enums.Roles.WORKER_GYM;

class RolesTest {

    @Test
    void getRoleName() {
        assertThat(ADMINISTRATION.getRoleName()).isEqualTo("Администрация");
        assertThat(STUDENT.getRoleName()).isEqualTo("Студент");
    }

    @Test
    void canBeAssignedToResourceTypeWhenRoleCanManage() {
        assertThat(Roles.canBeAssignedToResourceType(List.of(RESPONSIBLE_GYM), Roles.ResourceType.GYM)).isTrue();
    }

    @Test
    void canBeAssignedToResourceTypeWhenRoleCannotManage() {
        assertThat(Roles.canBeAssignedToResourceType(List.of(STUDENT), Roles.ResourceType.GYM)).isFalse();
    }

    @Test
    void canBeAssignedToResourceTypeWhenRolesAreEmpty() {
        assertThat(Roles.canBeAssignedToResourceType(Collections.emptyList(), Roles.ResourceType.GYM)).isFalse();
    }

    @Test
    void canBeAssignedToResourceTypeWhenRolesAreNull() {
        assertThat(Roles.canBeAssignedToResourceType((List<Roles>) null, Roles.ResourceType.GYM)).isFalse();
    }

    @Test
    void canBeAssignedToResourceTypeWhenResourceIsNull() {
        assertThat(Roles.canBeAssignedToResourceType(List.of(RESPONSIBLE_GYM), (Roles.ResourceType) null)).isFalse();
    }

    @Test
    void canBeAssignedToResourceTypeWithArray() {
        assertThat(Roles.canBeAssignedToResourceType(new Roles[]{RESPONSIBLE_GYM}, Roles.ResourceType.GYM)).isTrue();
    }

    @Test
    void canBeAssignedToResourceTypeWithArrayAndGenericEnum() {
        assertThat(Roles.canBeAssignedToResourceType(new Roles[]{RESPONSIBLE_GYM}, (Enum<?>) EventType.GYM)).isTrue();
    }

    @Test
    void canBeAssignedToResourceTypeWithCollectionAndGenericEnum() {
        assertThat(Roles.canBeAssignedToResourceType(List.of(RESPONSIBLE_GYM), (Enum<?>) EventType.GYM)).isTrue();
    }

    @Test
    void getAllInheritedRolesWhenRoleHasChildren() {
        Set<Roles> result = Roles.getAllInheritedRoles(List.of(ADMINISTRATION));

        assertThat(result).contains(ADMINISTRATION, HOSTEL_SUPERVISOR, FLOOR_SUPERVISOR);
    }

    @Test
    void getAllInheritedRolesWhenRoleIsStudent() {
        assertThat(Roles.getAllInheritedRoles(List.of(STUDENT))).isEmpty();
    }

    @Test
    void getAllInheritedRolesWhenRolesAreEmpty() {
        assertThat(Roles.getAllInheritedRoles(Collections.emptyList())).isEmpty();
    }

    @Test
    void getAllInheritedRolesWhenRolesAreNull() {
        assertThat(Roles.getAllInheritedRoles((List<Roles>) null)).isEmpty();
    }

    @Test
    void getAllInheritedRolesWithArray() {
        Set<Roles> result = Roles.getAllInheritedRoles(new Roles[]{ADMINISTRATION});

        assertThat(result).contains(ADMINISTRATION, HOSTEL_SUPERVISOR);
    }

    @Test
    void getAllParentRolesWhenRoleHasParents() {
        Set<Roles> result = Roles.getAllParentRoles(List.of(FLOOR_SUPERVISOR));

        assertThat(result).contains(FLOOR_SUPERVISOR, HOSTEL_SUPERVISOR, ADMINISTRATION);
    }

    @Test
    void getAllParentRolesWhenRolesAreEmpty() {
        assertThat(Roles.getAllParentRoles(Collections.emptyList())).isEmpty();
    }

    @Test
    void getAllParentRolesWhenRolesAreNull() {
        assertThat(Roles.getAllParentRoles((List<Roles>) null)).isEmpty();
    }

    @Test
    void getAllParentRolesWithArray() {
        Set<Roles> result = Roles.getAllParentRoles(new Roles[]{FLOOR_SUPERVISOR});

        assertThat(result).contains(ADMINISTRATION);
    }

    @Test
    void hasPermissionToManageRoleWhenSeniorManagesTarget() {
        assertThat(Roles.hasPermissionToManageRole(List.of(ADMINISTRATION), HOSTEL_SUPERVISOR)).isTrue();
    }

    @Test
    void hasPermissionToManageRoleWhenJuniorCannotManageSenior() {
        assertThat(Roles.hasPermissionToManageRole(List.of(FLOOR_SUPERVISOR), ADMINISTRATION)).isFalse();
    }

    @Test
    void hasPermissionToManageRoleWhenRolesAreEmpty() {
        assertThat(Roles.hasPermissionToManageRole(Collections.emptyList(), ADMINISTRATION)).isFalse();
    }

    @Test
    void hasPermissionToManageRoleWhenRolesAreNull() {
        assertThat(Roles.hasPermissionToManageRole((List<Roles>) null, ADMINISTRATION)).isFalse();
    }

    @Test
    void hasPermissionToManageRoleWhenTargetIsNull() {
        assertThat(Roles.hasPermissionToManageRole(List.of(ADMINISTRATION), null)).isFalse();
    }

    @Test
    void hasPermissionToManageRoleWithArray() {
        assertThat(Roles.hasPermissionToManageRole(new Roles[]{ADMINISTRATION}, HOSTEL_SUPERVISOR)).isTrue();
    }

    @Test
    void hasPermissionToManageResourceTypeWhenRoleManages() {
        assertThat(Roles.hasPermissionToManageResourceType(List.of(WORKER_GYM), Roles.ResourceType.GYM)).isTrue();
    }

    @Test
    void hasPermissionToManageResourceTypeWhenRoleDoesNotManage() {
        assertThat(Roles.hasPermissionToManageResourceType(List.of(STUDENT), Roles.ResourceType.GYM)).isFalse();
    }

    @Test
    void hasPermissionToManageResourceTypeWhenRolesAreEmpty() {
        assertThat(Roles.hasPermissionToManageResourceType(Collections.emptyList(), Roles.ResourceType.GYM)).isFalse();
    }

    @Test
    void hasPermissionToManageResourceTypeWhenRolesAreNull() {
        assertThat(Roles.hasPermissionToManageResourceType((List<Roles>) null, Roles.ResourceType.GYM)).isFalse();
    }

    @Test
    void hasPermissionToManageResourceTypeWhenResourceIsNull() {
        assertThat(Roles.hasPermissionToManageResourceType(List.of(WORKER_GYM), (Roles.ResourceType) null)).isFalse();
    }

    @Test
    void hasPermissionToManageResourceTypeWithArray() {
        assertThat(Roles.hasPermissionToManageResourceType(new Roles[]{WORKER_GYM}, Roles.ResourceType.GYM)).isTrue();
    }

    @Test
    void hasPermissionToManageResourceTypeWithArrayAndGenericEnum() {
        assertThat(Roles.hasPermissionToManageResourceType(new Roles[]{WORKER_GYM}, (Enum<?>) EventType.GYM)).isTrue();
    }

    @Test
    void hasPermissionToManageResourceTypeWithCollectionAndGenericEnum() {
        assertThat(Roles.hasPermissionToManageResourceType(List.of(WORKER_GYM), (Enum<?>) EventType.GYM)).isTrue();
    }

    @Test
    void seniorRoleIsAdministrationWhenMixedWithStudent() {
        assertThat(Roles.getAllInheritedRoles(List.of(STUDENT, ADMINISTRATION)))
                .contains(ADMINISTRATION);
    }

    @Test
    void resourceTypeCastWhenValueIsResourceType() {
        assertThat(Roles.ResourceType.cast(Roles.ResourceType.KITCHEN)).isEqualTo(Roles.ResourceType.KITCHEN);
    }

    @Test
    void resourceTypeCastWhenValueIsMatchingEnum() {
        assertThat(Roles.ResourceType.cast(EventType.KITCHEN)).isEqualTo(Roles.ResourceType.KITCHEN);
    }

    @Test
    void resourceTypeCastWhenValueIsNull() {
        assertThat(Roles.ResourceType.cast(null)).isNull();
    }

    @Test
    void resourceTypeCastWhenValueHasNoMatch() {
        assertThat(Roles.ResourceType.cast(BookingStatus.BOOKED)).isNull();
    }

    @Test
    void resourceTypeGetResourceTypeName() {
        assertThat(Roles.ResourceType.KITCHEN.getResourceTypeName()).isEqualTo("Кухня");
    }

    @Test
    void hasPermissionToManageRoleWhenKitchenResponsibleManagesNothingAbove() {
        assertThat(Roles.hasPermissionToManageRole(List.of(RESPONSIBLE_KITCHEN), ADMINISTRATION)).isFalse();
    }
}
