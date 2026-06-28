#!/usr/bin/env bash
set -euo pipefail

echo "== TITAN Codex Preflight =="
git status --short
echo "Branch: $(git branch --show-current)"
echo "Changed files:"
git diff --name-only

BRANCH="$(git branch --show-current)"
if [[ "$BRANCH" != "pre-backend-test-codex-runtime-ux-001" ]]; then
  echo "ERROR: Wrong branch. Expected pre-backend-test-codex-runtime-ux-001" >&2
  exit 1
fi

echo "Preflight OK"
