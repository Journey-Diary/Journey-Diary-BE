#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/app"
BUILD_LIBS="$PROJECT_ROOT/build/libs"
TARGET_JAR="$PROJECT_ROOT/spring-webapp.jar"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

# build 파일 복사
echo "$TIME_NOW > $TARGET_JAR 파일 복사" >> $DEPLOY_LOG
cp $BUILD_LIBS/*.jar $TARGET_JAR

# jar 파일 실행
echo "$TIME_NOW > $TARGET_JAR 파일 실행" >> $DEPLOY_LOG
nohup java -jar -Duser.timezone=Asia/Seoul $TARGET_JAR > $APP_LOG 2> $ERROR_LOG &

CURRENT_PID=$(pgrep -f $TARGET_JAR)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG