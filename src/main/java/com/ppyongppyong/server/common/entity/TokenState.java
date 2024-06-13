package com.ppyongppyong.server.common.entity;

import lombok.Getter;

@Getter
public enum TokenState {
    VALID,
    EXPIRED,
    INVALID
}
