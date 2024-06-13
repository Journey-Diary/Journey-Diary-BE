package com.ppyongppyong.server.common.exception;

import com.ppyongppyong.server.common.exception.massage.ErrorMsg;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException{
    private final ErrorMsg errorMsg;

}
