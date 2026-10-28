#!/usr/bin/env python3
"""Checks homepage, project route, and contact API through the public frontend."""
import json
import sys
import urllib.request
import urllib.error

base = (sys.argv[1] if len(sys.argv) > 1 else 'http://localhost:3000').rstrip('/')
for path in ('/', '/projects/nexus-banking-platform', '/robots.txt', '/sitemap.xml'):
    with urllib.request.urlopen(base + path, timeout=12) as response:
        assert response.status == 200, (path, response.status)
        print(f'PASS {path}: HTTP {response.status}')

# Invalid contact must be rejected by Spring validation, with no DB record written.
request = urllib.request.Request(
    base + '/api/contact',
    data=json.dumps({'name':'','email':'bad','subject':'','message':'short','website':''}).encode(),
    headers={'Content-Type':'application/json', 'Origin':base},
    method='POST')
try:
    urllib.request.urlopen(request, timeout=12)
    raise AssertionError('Invalid contact unexpectedly accepted')
except urllib.error.HTTPError as response:
    assert response.code == 400, f'Expected 400, got {response.code}'
    print('PASS /api/contact rejects invalid submissions: HTTP 400')
print('All smoke checks passed.')
