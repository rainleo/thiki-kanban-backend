package org.thiki.kanban.assignment;

import com.jayway.restassured.http.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thiki.kanban.TestBase;
import org.thiki.kanban.foundation.annotations.Scene;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by xubt on 6/16/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AssignmentControllerTest extends TestBase {

    @Scene("成功创建一条分配记录")
    @Test
    public void assign_shouldReturn201WhenAssigningSuccessfully() {
        given().header("userId", "11222")
                .body("{\"assignee\":\"assigneeId\",\"assigner\":\"assignerId\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/entries/1/tasks/fooId/assignments")
                .then()
                .statusCode(201)
                .body("id", equalTo("fooId"))
                .body("assignee", equalTo("assigneeId"))
                .body("assigner", equalTo("assignerId"))
                .body("reporter", equalTo("11222"))
                .body("_links.task.href", equalTo("http://localhost:8007/entries/1/tasks/fooId"))
                .body("_links.assignments.href", equalTo("http://localhost:8007/entries/1/tasks/fooId/assignments"))
                .body("_links.self.href", equalTo("http://localhost:8007/entries/1/tasks/fooId/assignments/fooId"));
    }

    @Scene("当用户根据ID查找分配记录时,如果该记录存在则将其返回")
    @Test
    public void findById_shouldReturnAssignmentSuccessfully() {
        jdbcTemplate.execute("INSERT INTO  kb_user_profile (id,name,email) VALUES ('assigneeId-foo','徐濤','766191920@qq.com')");
        jdbcTemplate.execute("INSERT INTO  kb_task_assignment (id,task_id,assignee,assigner,reporter) VALUES ('fooId','taskId-foo','assigneeId-foo','assignerId-foo','reporterId-foo')");
        given().header("userId", "reporterId-foo")
                .when()
                .get("/entries/1/tasks/fooId/assignments/fooId")
                .then()
                .statusCode(200)
                .body("id", equalTo("fooId"))
                .body("assignee", equalTo("assigneeId-foo"))
                .body("assigner", equalTo("assignerId-foo"))
                .body("name", equalTo("徐濤"))
                .body("reporter", equalTo("reporterId-foo"))
                .body("_links.task.href", equalTo("http://localhost:8007/entries/1/tasks/fooId"))
                .body("_links.assignments.href", equalTo("http://localhost:8007/entries/1/tasks/fooId/assignments"))
                .body("_links.self.href", equalTo("http://localhost:8007/entries/1/tasks/fooId/assignments/fooId"));
    }
    @Scene("当用户根据taskID获取分配记录时,如果指定的任务存在,则返回分配记录集合")
    @Test
    public void findByTaskId_shouldReturnAssignmentsSuccessfully() {
        jdbcTemplate.execute("INSERT INTO  kb_user_profile (id,name,email) VALUES ('assigneeId-foo','徐濤','766191920@qq.com')");
        jdbcTemplate.execute("INSERT INTO  kb_task (id,summary,content,reporter,entry_id) VALUES ('taskId-foo','this is the task summary.','play badminton',1,'fooId')");
        jdbcTemplate.execute("INSERT INTO  kb_task_assignment (id,task_id,assignee,assigner,reporter) VALUES ('fooId','taskId-foo','assigneeId-foo','assignerId-foo','reporterId-foo')");
        given().header("userId", "reporterId-foo")
                .body("{\"assignee\":\"assigneeId\",\"assigner\":\"assignerId\"}")
                .contentType(ContentType.JSON)
                .when()
                .get("/entries/1/tasks/taskId-foo/assignments")
                .then()
                .statusCode(200)
                .body("[0].id", equalTo("fooId"))
                .body("[0].assignee", equalTo("assigneeId-foo"))
                .body("[0].assigner", equalTo("assignerId-foo"))
                .body("[0].name", equalTo("徐濤"))
                .body("[0].reporter", equalTo("reporterId-foo"))
                .body("[0]._links.task.href", equalTo("http://localhost:8007/entries/1/tasks/taskId-foo"))
                .body("[0]._links.assignments.href", equalTo("http://localhost:8007/entries/1/tasks/taskId-foo/assignments"))
                .body("[0]._links.self.href", equalTo("http://localhost:8007/entries/1/tasks/taskId-foo/assignments/fooId"));
    }
    @Scene("当用户根据taskID获取分配记录时,如果指定的任务并不存在,则返回404客户端错误")
    @Test
    public void findByTaskId_shouldReturnErrorWhenTaskIsNotExist() {
        jdbcTemplate.execute("INSERT INTO  kb_task_assignment (id,task_id,assignee,assigner,reporter) VALUES ('fooId','taskId-foo','assigneeId-foo','assignerId-foo','reporterId-foo')");
        given().header("userId", "reporterId-foo")
                .body("{\"assignee\":\"assigneeId\",\"assigner\":\"assignerId\"}")
                .contentType(ContentType.JSON)
                .when()
                .get("/entries/1/tasks/taskId-foo/assignments")
                .then()
                .statusCode(400)
                .body("code", equalTo(400))
                .body("message", equalTo("task[taskId-foo] is not found."));
    }
    @Scene("当用户想取消某个分配时,如果指定的分配记录存在,则成功将其取消")
    @Test
    public void delete_shouldReturnSuccessfully() {
        jdbcTemplate.execute("INSERT INTO  kb_task_assignment (id,task_id,assignee,assigner,reporter) VALUES ('fooId','taskId-foo','assigneeId-foo','assignerId-foo','reporterId-foo')");
        given().header("userId", "reporterId-foo")
                .when()
                .delete("/entries/1/tasks/fooId/assignments/fooId")
                .then()
                .statusCode(200)
                .body("_links.task.href", equalTo("http://localhost:8007/entries/1/tasks/fooId"))
                .body("_links.assignments.href", equalTo("http://localhost:8007/entries/1/tasks/fooId/assignments"));
    }

    @Scene("当用户想取消某个分配时,如果指定的分配记录并不存在,则返回404客户端错误")
    @Test
    public void delete_shouldReturnErrorWhenAssignmentIsNotExist() {
        given().header("userId", "reporterId-foo")
                .when()
                .delete("/entries/1/tasks/fooId/assignments/fooId")
                .then()
                .statusCode(404)
                .body("code", equalTo(404))
                .body("message", equalTo("assignment[fooId] is not found."));
    }
}