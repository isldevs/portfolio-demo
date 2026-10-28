#!/usr/bin/env python3
"""Generate local secrets once; never overwrite existing configuration."""
from pathlib import Path
from secrets import token_urlsafe

root = Path(__file__).resolve().parents[1]
config = root / '.env'
if config.exists():
    print('.env already exists. No changes made.')
else:
    config.write_text(
        'DB_NAME=portfolio\n'
        'DB_USERNAME=portfolio\n'
        f'DB_PASSWORD={token_urlsafe(32)}\n'
        'ADMIN_USERNAME=admin\n'
        f'ADMIN_PASSWORD={token_urlsafe(24)}\n'
        'DOMAIN=portfolio.example.com\n',
        encoding='utf-8'
    )
    config.chmod(0o600)
    print('Created .env with random DB and admin passwords.')
print('Keep .env private. Read it locally to find your admin password.')
