package org.thiki.kanban.sprint;

import org.thiki.kanban.foundation.application.DomainOrder;

/**
 * Created by xubt on 04/02/2017.
 */
public enum SprintCodes {
    START_TIME_IS_AFTER_END_TIME("001", "迭代开始日期晚于结束日期。"),
    UNARCHIVE_SPRINT_EXIST("002", "存在尚未归档的迭代，不允许创建新的迭代。"),
    ACTIVE_SPRINT_IS_NOT_FOUND("003", "当前不存在激活的迭代。"),
    SPRINT_IS_NOT_EXIST("004", "迭代不存在。"),
    SPRINT_ALREADY_ARCHIVED("005", "迭代此前已经归档。"),
    SPRINT_NAME_ALREADY_EXISTS("006", "当前看板下，已经存在相同名称的迭代。");
    public static final Integer SPRINT_IN_PROGRESS = 1;
    public static final Integer SPRINT_COMPLETED = 2;
    public static final String startTimeIsRequired = "迭代开始时间不可以为空。";
    public static final String endTimeIsRequired = "迭代结束时间不可以为空。";
    public static final String sprintNameIsRequired = "迭代名称不可以为空。";
    private String code;

    private String message;

    SprintCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return Integer.parseInt(DomainOrder.SPRINT + "" + code);
    }

    public String message() {
        return message;
    }
}
