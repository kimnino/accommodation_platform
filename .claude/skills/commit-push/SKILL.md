---
name: commit-push
description: 변경 사항을 커밋하고 원격 저장소에 푸시합니다. 개인 공부용 간단 워크플로우 — PR은 만들지 않습니다.
---

# commit-push

변경 사항을 분석해 커밋 후 푸시. 커밋 메시지 규칙(접두어·한글·WHY 중심)은 `CLAUDE.md` 참고.

## 실행 순서

1. **상태 파악 (병렬)** — `git status`, `git diff`, `git diff --staged`, `git log -5 --oneline`, `git branch --show-current`, `git remote -v`
2. **메시지 작성** — 인자 없으면 diff로 추론, 있으면 그대로 사용(접두어 누락 시 성격 판단해 추가). `CLAUDE.md`의 Conventional Commits 규칙 준수, 60자 이내 선호.
3. **민감 파일 점검** — `.env`, `*.key`, `*.pem`, `credentials.*`, `*-secret.*`, `application-secret.yml` 스테이징 시 **중단하고 사용자에게 경고**.
4. **커밋**
   - 파일명 명시해 `git add <file>...` (❌ `git add .`, `git add -A`)
   - 미추적 파일은 커밋 대상인 것만
   - HEREDOC으로 메시지 전달:
     ```bash
     git commit -m "$(cat <<'EOF'
     <메시지>

     Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>
     EOF
     )"
     ```
   - 훅 실패 시 원인 수정 후 **새 커밋** (❌ `--amend`, ❌ `--no-verify`)
5. **푸시** — upstream 추적 시 `git push`, 미추적 시 `git push -u origin <branch>`. 단독 공부 프로젝트라 main 그대로 진행. `--force` 금지.
6. **보고**
   ```
   ✅ 커밋 & 푸시 완료
     - abc1234 feat: 회원 엔티티 추가
     - origin/main (파일 3개)
   ```

## 금지

- 변경 없으면 빈 커밋 금지 — 사용자에게 알림
- `git add -A`/`git add .`, `--no-verify`, `--amend`, `--force` 금지
- PR 생성 금지 (필요 시 `/commit-push-pr`)
