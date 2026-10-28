#!/usr/bin/env python3
"""One-time generation of local secrets. Uses only the Python standard library."""
from pathlib import Path
import secrets

root = Path(__file__).resolve().parent.parent
source = root / '.env.example'
target = root / '.env'
if target.exists():
    raise SystemExit('.env already exists; leaving your existing configuration untouched.')
content = source.read_text(encoding='utf-8')
for placeholder in (
    'GENERATE_RANDOM_POSTGRES_PASSWORD',
    'GENERATE_RANDOM_INTERNAL_API_KEY',
    'GENERATE_RANDOM_HASH_SALT',
):
    content = content.replace(placeholder, secrets.token_hex(32))
target.write_text(content, encoding='utf-8')
try:
    target.chmod(0o600)
except OSError:
    pass
print('Created .env with strong random secrets. Do not commit it to Git.')
print('For production, change SITE_URL and DOMAIN in .env before building.')
