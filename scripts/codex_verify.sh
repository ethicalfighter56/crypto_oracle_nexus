#!/usr/bin/env bash
set -euo pipefail

ALLOWED_FILE=".codex/allowed_files_runtime_ux_001.txt"

echo "== TITAN Codex Verification =="
git diff --check
git diff --stat

echo "Changed files:"
git diff --name-only

unauthorized=0
if [[ -f "$ALLOWED_FILE" ]]; then
  while IFS= read -r changed; do
    [[ -z "$changed" ]] && continue
    if ! grep -Fxq "$changed" "$ALLOWED_FILE"; then
      echo "ERROR: Unauthorized changed file: $changed" >&2
      unauthorized=1
    fi
  done < <(git diff --name-only)
fi

if [[ "$unauthorized" -ne 0 ]]; then
  exit 1
fi

echo "Verification OK"
