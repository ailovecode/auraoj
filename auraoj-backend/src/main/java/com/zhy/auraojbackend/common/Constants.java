package com.zhy.auraojbackend.common;

import lombok.Getter;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/29
 */
public class Constants {

    // 判题服务
    public static final String JUDGE_SERVER_URL = "http://127.0.0.1:5050";

    /**
     * @Description 账户相关常量
     * @Date 2026/3/29
     */
    @Getter
    public enum Account {
        CODE_CHANGE_PASSWORD_FAIL("change-password-fail:"),
        CODE_CHANGE_PASSWORD_LOCK("change-password-lock:"),
        CODE_ACCOUNT_LOCK("account-lock:"),
        CODE_CHANGE_EMAIL_FAIL("change-email-fail:"),
        CODE_CHANGE_EMAIL_LOCK("change-email-lock:"),

        TRY_LOGIN_NUM("try-login-num:"),

        ACM_RANK_CACHE("acm_rank_cache"),
        OI_RANK_CACHE("oi_rank_cache"),

        GROUP_RANK_CACHE("group_rank_cache"),

        SUPER_ADMIN_UID_LIST_CACHE("super_admin_uid_list_case"),

        SUBMIT_NON_CONTEST_LOCK("submit_non_contest_lock:"),
        TEST_JUDGE_LOCK("test_judge_lock:"),
        SUBMIT_CONTEST_LOCK("submit_contest_lock:"),
        DISCUSSION_ADD_NUM_LOCK("discussion_add_num_lock:"),
        GROUP_ADD_NUM_LOCK("group_add_num_lock"),
        CONTEST_ADD_PRINT_LOCK("contest_add_print_lock:"),

        REMOTE_JUDGE_CF_ACCOUNT_NUM("remote_judge_cf_account:");

        private final String code;

        Account(String code) {
            this.code = code;
        }

    }

    /**
     * 等待判题的redis队列
     *
     * @Since 2021/12/22
     */

    @Getter
    public enum Queue {
        CONTEST_JUDGE_WAITING("Contest_Waiting_Handle_Queue"),
        GENERAL_JUDGE_WAITING("General_Waiting_Handle_Queue"),
        TEST_JUDGE_WAITING("Test_Judge_Waiting_Handle_Queue"),
        CONTEST_REMOTE_JUDGE_WAITING_HANDLE("Contest_Remote_Waiting_Handle_Queue"),
        GENERAL_REMOTE_JUDGE_WAITING_HANDLE("General_Remote_Waiting_Handle_Queue");

        private Queue(String name) {
            this.name = name;
        }

        private final String name;

    }

    public enum TaskType {
        /**
         * 自身评测
         */
        JUDGE("/judge"),
        /**
         * 远程评测
         */
        REMOTE_JUDGE("/remote-judge"),

        /**
         * 在线调试
         */
        TEST_JUDGE("/test-judge"),
        /**
         * 编译特判程序
         */
        COMPILE_SPJ("/compile-spj"),

        /**
         * 编译交互程序
         */
        COMPILE_INTERACTIVE("/compile-interactive");

        private final String path;

        TaskType(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

    }
}
