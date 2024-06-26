package com.ssssogong.issuemanager.service;

import com.ssssogong.issuemanager.domain.Issue;
import com.ssssogong.issuemanager.domain.IssueModification;
import com.ssssogong.issuemanager.domain.Project;
import com.ssssogong.issuemanager.domain.Roles;
import com.ssssogong.issuemanager.domain.UserProject;
import com.ssssogong.issuemanager.domain.account.User;
import com.ssssogong.issuemanager.domain.enumeration.Category;
import com.ssssogong.issuemanager.domain.enumeration.Priority;
import com.ssssogong.issuemanager.domain.enumeration.State;
import com.ssssogong.issuemanager.domain.role.Role;
import com.ssssogong.issuemanager.dto.*;
import com.ssssogong.issuemanager.repository.IssueModificationRepository;
import com.ssssogong.issuemanager.repository.IssueRepository;
import com.ssssogong.issuemanager.repository.ProjectRepository;
import com.ssssogong.issuemanager.repository.RoleRepository;
import com.ssssogong.issuemanager.repository.UserProjectRepository;
import com.ssssogong.issuemanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/truncate.sql")
public class IssueServiceTest {

    @Autowired
    private IssueService issueService;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private IssueModificationRepository issueModificationRepository;
    @Autowired
    private UserProjectRepository userProjectRepository;

    @BeforeEach
    void setUp() {
        final User user = User.builder()
                .id(1L)
                .accountId("tester")
                .username("Hyun")
                .build();
        final User dev = User.builder()
                .id(2L)
                .accountId("dev")
                .username("Woo")
                .build();
        final Project project = Project.builder()
                .id(1L)
                .name("야심찬 프로젝트")
                .subject("어쩌구 저쩌구 프로젝트입니다")
                .build();
        final Issue issue = Issue.builder()
                .id(1L)
                .title("엄청난 이슈")
                .description("엄청난 이슈입니다")
                .priority(Priority.MAJOR)
                .state(State.ASSIGNED)
                .category(Category.REFACTORING)
                .reporter(user)
                .assignee(dev)
                .project(project)
                .build();
        userRepository.save(user);
        userRepository.save(dev);
        projectRepository.save(project);
        issueRepository.save(issue);
    }

    @Test
    @WithMockUser(username = "tester", roles = {"ISSUE_REPORTABLE_1"})
    void tester_이슈_생성() throws IOException {
        // given
        final IssueSaveRequestDto issueSaveRequestDto = IssueSaveRequestDto.builder()
                .title("엄청난 이슈")
                .description("엄청난 이슈입니다")
                .category("REFACTORING")
                .priority("MAJOR")
                .build();
        final Long projectId = 1L;

        // when
        IssueIdResponseDto issueIdResponseDto = issueService.save(projectId, issueSaveRequestDto);
        Issue findIssue = issueRepository.findById(issueIdResponseDto.getIssueId()).orElse(null);

        // then
        assertThat(findIssue).isNotNull();
        assertAll(
                () -> assertThat(findIssue.getTitle()).isEqualTo("엄청난 이슈"),
                () -> assertThat(findIssue.getDescription()).isEqualTo("엄청난 이슈입니다"),
                () -> assertThat(findIssue.getPriority()).isEqualTo(Priority.MAJOR),
                () -> assertThat(findIssue.getState()).isEqualTo(State.NEW),
                () -> assertThat(findIssue.getCategory()).isEqualTo(Category.REFACTORING)
        );
    }

    @Test
    void 이슈_확인() {
        // given
        final Long projectId = 1L;
        final Long issueId = 1L;

        // when
        IssueShowResponseDto issueShowResponseDto = issueService.show(projectId, issueId);

        // then
        assertAll(
                () -> assertThat(issueShowResponseDto.getTitle()).isEqualTo("엄청난 이슈"),
                () -> assertThat(issueShowResponseDto.getDescription()).isEqualTo("엄청난 이슈입니다"),
                () -> assertThat(issueShowResponseDto.getPriority()).isEqualTo("MAJOR"),
                () -> assertThat(issueShowResponseDto.getState()).isEqualTo("ASSIGNED"),
                () -> assertThat(issueShowResponseDto.getCategory()).isEqualTo("REFACTORING")
        );
    }

    @Transactional
    @Test
    @WithMockUser(username = "tester", roles = {"ISSUE_UPDATABLE_1"})
    void tester_admin_이슈_수정() throws IOException {
        // given
        final IssueUpdateRequestDto issueUpdateRequestDto = IssueUpdateRequestDto.builder()
                .title("대단한 이슈")
                .description("대단한 이슈입니다")
                .priority("CRITICAL")
                .build();
        final Long projectId = 1L;
        final Long issueId = 1L;

        // when
        IssueIdResponseDto issueIdResponseDto = issueService.update(projectId, issueId, issueUpdateRequestDto);
        Issue updatedIssue = issueRepository.findById(issueIdResponseDto.getIssueId()).orElse(null);

        // then
        assertThat(updatedIssue).isNotNull();
        assertAll(
                () -> assertThat(updatedIssue.getTitle()).isEqualTo(issueUpdateRequestDto.getTitle()),
                () -> assertThat(updatedIssue.getDescription()).isEqualTo(issueUpdateRequestDto.getDescription()),
                () -> assertThat(updatedIssue.getPriority()).isEqualTo(Priority.valueOf(issueUpdateRequestDto.getPriority()))
        );
    }

    @Test
    @WithMockUser(username = "tester", roles = {"ISSUE_DELETABLE_1"})
    void 이슈_삭제() {
        // given
        final Long projectId = 1L;
        final Long issueId = 1L;

        // when
        issueService.delete(projectId, issueId);
        Issue deletedIssue = issueRepository.findById(issueId).orElse(null);

        // then
        assertThat(deletedIssue).isNull();
    }

    @Transactional
    @Test
    @WithMockUser(username = "dev", roles = {"ISSUE_FIXABLE_1"})
    void 이슈_상태_변경() {
        // given
        final Long projectId = 1L;
        final Long issueId = 1L;
        final String newState = "FIXED";
        final IssueStateUpdateRequestDto issueStateUpdateRequestDto = IssueStateUpdateRequestDto.builder()
                .state(newState)
                .build();


        // when
        IssueIdResponseDto responseDto = issueService.stateUpdate(projectId, issueId, issueStateUpdateRequestDto);
        Issue updatedIssue = issueRepository.findById(responseDto.getIssueId()).orElse(null);
        List<IssueModification> issueModifications = issueModificationRepository.findByIssueId(issueId);

        // then
        assertThat(updatedIssue).isNotNull();
        assertThat(issueModifications).isNotEmpty();
        assertAll(
                () -> assertThat(updatedIssue.getState()).isEqualTo(State.valueOf(newState)),
                () -> assertThat(issueModifications).hasSize(1),
                () -> assertThat(issueModifications.get(0).getIssue()).isEqualTo(updatedIssue)
        );
    }

    @Test
    void 프로젝트에_속한_이슈_목록_검색() {
        // given
        final Long projectId = 1L;
        final Integer issueCount = 3;
        final Project project = projectRepository.findById(projectId).orElse(null);
        final User user2 = User.builder()
                .id(2L)
                .accountId("tester2")
                .username("Woo")
                .build();
        final User user3 = User.builder()
                .id(3L)
                .accountId("dev1")
                .username("Jin")
                .build();
        final User user4 = User.builder()
                .id(4L)
                .accountId("dev2")
                .username("Lee")
                .build();
        final Issue issue1 = Issue.builder()
                .title("이슈1")
                .description("이슈1 설명")
                .priority(Priority.MAJOR)
                .state(State.NEW)
                .category(Category.REFACTORING)
                .reporter(user2)
                .fixer(user3)
                .assignee(user3)
                .project(project)
                .build();
        final Issue issue2 = Issue.builder()
                .title("이슈2")
                .description("이슈2 설명")
                .priority(Priority.MINOR)
                .state(State.ASSIGNED)
                .category(Category.BUG_REPORT)
                .reporter(user2)
                .fixer(user3)
                .assignee(user3)
                .project(project)
                .build();
        final Issue issue3 = Issue.builder()
                .title("이슈3")
                .description("이슈3 설명")
                .priority(Priority.CRITICAL)
                .state(State.RESOLVED)
                .category(Category.EXTRA)
                .reporter(user2)
                .fixer(user4)
                .assignee(user4)
                .project(project)
                .build();
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        issueRepository.save(issue1);
        issueRepository.save(issue2);
        issueRepository.save(issue3);

        // when
        List<IssueProjectResponseDto> issues = issueService.findIssuesInProject(projectId, null,null,null,null,null,null,null,null);
        List<IssueProjectResponseDto> majorIssues = issueService.findIssuesInProject(projectId, null, "MAJOR", null, null, null, null, null, null);
        List<IssueProjectResponseDto> refactoringIssues = issueService.findIssuesInProject(projectId, null, null, null, "REFACTORING", null, null, null, null);
        List<IssueProjectResponseDto> newIssues = issueService.findIssuesInProject(projectId, null, null, "NEW", null, null, null, null, null);
        List<IssueProjectResponseDto> reportedByUser2 = issueService.findIssuesInProject(projectId, null, null, null, null, "tester2", null, null, null);
        List<IssueProjectResponseDto> fixedByUser3 = issueService.findIssuesInProject(projectId, null, null, null, null, null, "dev1", null, null);
        List<IssueProjectResponseDto> assignedToUser4 = issueService.findIssuesInProject(projectId, null, null, null, null, null, null, "dev2", null);

        // then
        assertThat(issues).hasSize(4);

        assertThat(majorIssues).hasSize(2);
        assertThat(majorIssues.get(0).getTitle()).isEqualTo("이슈1");

        assertThat(refactoringIssues).hasSize(2);
        assertThat(refactoringIssues.get(0).getTitle()).isEqualTo("이슈1");

        assertThat(newIssues).hasSize(1);
        assertThat(newIssues.get(0).getTitle()).isEqualTo("이슈1");

        assertThat(reportedByUser2).hasSize(3);
        assertThat(reportedByUser2.get(0).getTitle()).isEqualTo("이슈3");
        assertThat(reportedByUser2.get(1).getTitle()).isEqualTo("이슈2");
        assertThat(reportedByUser2.get(2).getTitle()).isEqualTo("이슈1");

        assertThat(fixedByUser3).hasSize(2);
        assertThat(fixedByUser3.get(0).getTitle()).isEqualTo("이슈2");
        assertThat(fixedByUser3.get(1).getTitle()).isEqualTo("이슈1");

        assertThat(assignedToUser4).hasSize(1);
        assertThat(assignedToUser4.get(0).getTitle()).isEqualTo("이슈3");
    }

    @Test
    void assignee_추천(@Autowired RoleRepository roleRepository) {
        // given
        // 유저 생성
        final User user1 = User.builder().accountId("dev1").username("name1").build();
        final User user2 = User.builder().accountId("dev2").username("name2").build();
        final User user3 = User.builder().accountId("dev3").username("name3").build();
        final User user4 = User.builder().accountId("dev4").username("name4").build();
        final User user5 = User.builder().accountId("dev5").username("name5").build();
        userRepository.saveAll(List.of(user1, user2, user3, user4, user5));

        // 프로젝트 생성
        final Project project = projectRepository.save(Project.builder().build());

        // 프로젝트에 유저 추가
        final Role developer = Roles.builder().roles(roleRepository.findAll()).build().findRole("Developer");
        final UserProject userProject1 = UserProject.builder().project(project).user(user1)
                .role(developer).build();
        final UserProject userProject2 = UserProject.builder().project(project).user(user2)
                .role(developer).build();
        final UserProject userProject3 = UserProject.builder().project(project).user(user3)
                .role(developer).build();
        final UserProject userProject4 = UserProject.builder().project(project).user(user4)
                .role(developer).build();
        final UserProject userProject5 = UserProject.builder().project(project).user(user5)
                .role(developer).build();
        userProjectRepository.saveAll(List.of(userProject1, userProject2, userProject3, userProject4, userProject5));

        // 지난 이슈들 생성
        final Issue issue1 = Issue.builder()
                .project(project)
                .priority(Priority.TRIVIAL) //1
                .category(Category.FEATURE_REQUEST) //5
                .fixer(user3)
                .state(State.RESOLVED)
                .build();
        final IssueModification modification1 = IssueModification.builder()
                .issue(issue1)
                .from(State.FIXED)
                .to(State.RESOLVED)
                .build();
        final Issue issue2 = Issue.builder()
                .project(project)
                .priority(Priority.TRIVIAL) //1
                .category(Category.REFACTORING)
                .fixer(user3)
                .state(State.CLOSED)
                .build();
        final IssueModification modification2 = IssueModification.builder()
                .issue(issue2)
                .from(State.FIXED)
                .to(State.RESOLVED)
                .build();
        final Issue issue3 = Issue.builder()
                .project(project)
                .priority(Priority.MAJOR) //3
                .category(Category.FEATURE_REQUEST) //5
                .fixer(user1)
                .state(State.RESOLVED)
                .build();
        final IssueModification modification3 = IssueModification.builder()
                .issue(issue3)
                .from(State.FIXED)
                .to(State.RESOLVED)
                .build();
        final Issue issue4 = Issue.builder()
                .project(project)
                .priority(Priority.CRITICAL)
                .category(Category.FEATURE_REQUEST)
                .fixer(user2)
                .state(State.NEW)
                .build();
        issueRepository.saveAll(List.of(issue1, issue2, issue3, issue4));
        issueModificationRepository.saveAll(List.of(modification1, modification2, modification3));

        // when
        final Issue issue = issueRepository.save(Issue.builder()
                .project(project)
                .category(Category.FEATURE_REQUEST)
                .build());
        final List<UserResponseDto> response = issueService.suggestAssignee(project.getId(), issue.getId());

        // then
        assertThat(response).hasSize(3);
        assertThat(response.get(0).getAccountId()).isEqualTo("dev1"); //가중치 최고
        assertThat(response.get(1).getAccountId()).isEqualTo("dev3"); //가중치 위에서 두번째
    }
}
