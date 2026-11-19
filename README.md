# PhotoAI Lite

갤럭시용 Android 사진 분석 앱입니다.

- 갤러리 사진 선택
- 이미지 해상도 및 평균 RGB 색상 확인
- GitHub Actions에서 디버그 APK 자동 빌드

## APK 빌드
GitHub 저장소의 **Actions → Build Android APK**에서 빌드를 실행하거나 `main`에 푸시하면 자동 실행됩니다. 완료된 워크플로에서 `PhotoAI-debug-apk` 아티팩트를 다운로드한 뒤 압축을 해제하면 `app-debug.apk`가 있습니다.

현재 버전은 **로컬 이미지 통계 분석**만 수행하며 생성형 AI 서비스 연동은 포함하지 않습니다.
