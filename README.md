# CAPStone Android Practice (garyoung)

캡스톤 프로젝트 연습용 안드로이드 앱 저장소입니다.  
Jetpack Compose 기반으로 화면을 구성하고, 사용량/기록 관련 기능을 실험했습니다.

## 주요 기능
- 앱 사용량 수집 및 화면 표시
- 메모 기록(로컬 DB)
- 권한 안내/요청 흐름

## 기술 스택
- Kotlin, Jetpack Compose
- Room (로컬 DB)
- Gradle (Kotlin DSL)

## 실행 방법
1) Android Studio로 프로젝트 열기
2) `app` 모듈 선택
3) 에뮬레이터 또는 실제 기기에서 실행

## 권한 참고
사용량 기능은 다음 권한이 필요합니다.
- `PACKAGE_USAGE_STATS`
- `QUERY_ALL_PACKAGES`

## 프로젝트 구조
- `app/src/main/java/com/example/capstone_2`: 앱 코드
- `app/src/main/res`: 리소스
