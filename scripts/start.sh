#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/app"
BUILD_LIBS="$PROJECT_ROOT/build/libs"
TARGET_JAR="$PROJECT_ROOT/spring-webapp.jar"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

# build 파일 복사
echo "$TIME_NOW > $BUILD_LIBS 에서 $TARGET_JAR 파일로 복사" >> $DEPLOY_LOG
cp $BUILD_LIBS/server-0.0.1-SNAPSHOT.jar $TARGET_JAR

# 기존에 실행 중인 애플리케이션 종료
CURRENT_PID=$(pgrep -f $TARGET_JAR)
if [ -n "$CURRENT_PID" ]; then
  echo "$TIME_NOW > 실행 중인 애플리케이션 종료: $CURRENT_PID" >> $DEPLOY_LOG
  kill -15 $CURRENT_PID
fi

# jar 파일 실행
echo "$TIME_NOW > $TARGET_JAR 파일 실행" >> $DEPLOY_LOG
nohup java -jar -Duser.timezone=Asia/Seoul $TARGET_JAR > $APP_LOG 2> $ERROR_LOG &

CURRENT_PID=$(pgrep -f $TARGET_JAR)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG