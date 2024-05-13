package com.ssssogong.issuemanager.domain.role;


import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class ProjectLeader extends Role {
    {
        this.allowedPrivileges = List.of( new Privilege[]{
                Privilege.ISSUE_ASSIGNABLE,
                Privilege.ISSUE_CLOSABLE,
        });
    }
}
