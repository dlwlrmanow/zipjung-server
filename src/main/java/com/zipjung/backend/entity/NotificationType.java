package com.zipjung.backend.entity;

public enum NotificationType {
    // TODOS 관련
    NEW_TODO, // save todos
    DELETE_TODO,
    DONE_TODO, // 완료한 할 일

    // reminder
    REMINDER, // 로그인 시 오늘 할 일이 총 n개 있어요 띄우기용

    // focused time
    DELETE_FOCUSED,
    NEW_FOCUSED,

    // focus log 관련
    ADD_LOCATION,

}
