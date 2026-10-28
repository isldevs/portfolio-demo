#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${1:-http://localhost:8080}"
echo "Checking service at $BASE_URL"
for i in $(seq 1 45); do
  if curl --fail --silent --show-error "$BASE_URL/actuator/health" > /tmp/vaadin-portfolio-health.json 2>/dev/null; then
    break
  fi
  sleep 2
done
cat /tmp/vaadin-portfolio-health.json
python3 - <<'PY'
import json
with open('/tmp/vaadin-portfolio-health.json', encoding='utf-8') as f:
    assert json.load(f).get('status') == 'UP', 'Expected healthy service'
PY
curl -fsS "$BASE_URL/" > /dev/null
echo "Health endpoint and public home route returned successfully."
echo "Still manually test admin login, editing, contact form, and mobile layout."
