---
name: "\U0001F4AC PR template"
about: A template that structures and creates PR for function development and bug
  correction.
title: ''
labels: ''
assignees: ''

---

## 📌 PR 제목
- [기능] 로그인 화면 구현
- [버그] 댓글 작성 시 앱 크래시 수정
- [리팩토링] Order 모듈 구조 개선

---

## 🔖 PR 타입

해당 PR의 성격에 맞게 체크해주세요.

- [ ] feat (새로운 기능 추가)
- [ ] fix (버그 수정)
- [ ] chore (빌드/설정/배포 등 비즈니스 로직 외 작업)
- [ ] refactor (리팩토링, 로직 변화 없음)
- [ ] docs (문서 작업)
- [ ] style (포맷팅, 세미콜론 누락 등)
- [ ] test (테스트 추가 또는 수정)

---

## 📄 주요 변경 사항
변경한 핵심 내용을 bullet 형식으로 정리해주세요.

- 로그인 화면 UI 및 ViewModel 연동
- 이메일/비밀번호 유효성 검사 로직 추가
- 클릭 시 키보드 자동 숨김 처리

---

## 🧪 테스트 내용
직접 확인한 동작 또는 QA 기준 테스트 목록을 작성해주세요.

- [x] 유효한 정보 입력 시 로그인 성공
- [x] 잘못된 정보 입력 시 오류 토스트 출력
- [ ] 다크모드 UI 정상 렌더링 확인

---

## 📱 스크린샷 / 영상 (선택)

| Before | After |
|--------|-------|
| ![before](url) | ![after](url) |

---

## 🧩 관련 이슈 / 작업 항목

- Resolved: #12, #18
- 관련 작업: 로그인 API 연동, SharedPreferences 저장

---

## ✅ 체크리스트

- [ ] CI/CD 통과 확인
- [ ] 커밋 메시지 컨벤션 (`feat:`, `fix:` 등) 적용
- [ ] main 브랜치로 바로 PR 올리지 않음 (ex. dev → main은 릴리즈 시에만)
- [ ] 불필요한 로그 / 디버깅 코드 제거

---

## 👀 리뷰어 참고사항 (중요)

- [ ] ViewModel의 상태 처리 방식이 적절한지 봐주세요
- [ ] 이메일 정규식 유효성 검사 방식에 개선 여지가 있는지 확인 부탁드립니다

---

## 🧼 임시 코드 또는 추후 개선 예정

- 토스트 메시지 디자인은 추후 UI 파트와 협의 예정
- 서버 연동은 현재 Mock API 기반이며 실제 API 반영 시 일부 수정 필요
