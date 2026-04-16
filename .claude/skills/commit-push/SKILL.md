---
name: commit-push
description: 변경 사항을 커밋하고 원격 저장소에 푸시합니다. 개인 공부용 간단 워크플로우 — PR은 만들지 않습니다.
---

# commit-push

현재 변경 사항을 분석해 커밋 메시지를 작성하고, 커밋 후 푸시합니다.

## 사용자 입력

- 인자 없이 실행: 변경 내용을 분석해 자동으로 커밋 메시지 작성
- 인자 있음: 사용자가 넘긴 문자열을 커밋 메시지로 사용 (예: `/commit-push 헤더 컴포넌트 추가`)

## 실행 순서

### 1. 현재 상태 파악 (병렬 실행)

다음을 **병렬로** 실행해 현재 상태를 파악합니다:

- `git status` — 변경된 파일 확인
- `git diff` — unstaged 변경
- `git diff --staged` — staged 변경
- `git log -5 --oneline` — 최근 커밋 스타일 참고
- `git branch --show-current` — 현재 브랜치
- `git remote -v` — 원격 저장소 확인

### 2. 변경 사항 분석 및 커밋 메시지 작성

- 사용자 인자가 없으면 diff를 분석해 **한글로 간결하게** 작성
- 형식: `<type>: 동작 대상 + 내용` — 변경 성격에 맞는 Conventional Commits 접두어를 **반드시** 붙인다
  - `feat:` 새 기능/엔티티/API/화면 추가
  - `fix:` 버그 수정
  - `refactor:` 동작 변경 없는 구조 개선
  - `style:` 포맷/세미콜론/공백 등 스타일 변경
  - `docs:` 문서/주석 변경
  - `test:` 테스트 추가/수정
  - `chore:` 빌드/설정/gitignore/의존성 등 기타 변경
  - `perf:` 성능 개선
  - 예: `feat: 회원 엔티티 추가`, `fix: 숙소 검색 페이징 오류 수정`, `chore: gitignore에 Claude 로컬 설정 제외`
- 사용자 인자가 접두어 없이 들어와도 성격을 판단해 접두어를 붙인다
- **WHAT이 아닌 WHY**에 초점 (단, 공부용 프로젝트라 과하지 않게)
- 한 줄로 60자 이내 선호, 필요 시 본문 추가

### 3. 민감 파일 점검

커밋 전에 다음을 **반드시** 확인합니다:

- `.env`, `*.key`, `*.pem`, `credentials.*`, `*-secret.*` 등이 스테이징되어 있으면 **중단하고 사용자에게 경고**
- `application-secret.yml` 같은 민감 설정 파일도 점검

### 4. 커밋

- 변경된 파일만 명시적으로 `git add <file1> <file2>` 로 추가 (`git add -A`/`git add .` 금지)
- 미추적(`??`) 파일 중 커밋 대상이 맞는 것만 포함
- HEREDOC으로 커밋 메시지 전달:

```bash
git commit -m "$(cat <<'EOF'
<커밋 메시지>

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>
EOF
)"
```

- 훅 실패 시: 문제 원인을 파악해 수정 후 **새 커밋** 생성 (--amend 금지)
- 훅 우회(`--no-verify`) 금지

### 5. 푸시

- 현재 브랜치가 upstream 추적 중인지 확인
  - 추적 중: `git push`
  - 미추적: `git push -u origin <current-branch>`
- **main/master 브랜치에 푸시하기 전에 사용자에게 확인**
  - 단, 현재 브랜치가 이미 origin/main을 추적 중이고 사용자가 평소에 main으로 작업하는 경우(공부용 단독 프로젝트)엔 그대로 진행
- `--force` / `--force-with-lease` 는 사용자가 명시적으로 요청하지 않는 한 사용 금지

### 6. 완료 보고

다음을 한 번에 간단히 보고:

- 커밋 해시 및 메시지 한 줄
- 푸시된 브랜치와 원격
- 변경 파일 수

예시:
```
✅ 커밋 & 푸시 완료
  - abc1234 회원 엔티티 추가
  - origin/main (파일 3개)
```

## 금지 사항

- 커밋할 변경이 없으면 빈 커밋 만들지 말고 사용자에게 알림
- `git add -A`, `git add .` 사용 금지 — 항상 파일명 명시
- `--no-verify`, `--amend`, `--force` 사용 금지 (사용자가 명시 요청 시에만)
- PR 생성하지 않음 (PR이 필요하면 `/commit-push-pr` 사용)
