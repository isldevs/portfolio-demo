CREATE TABLE contact_messages (
    id UUID PRIMARY KEY,
    sender_name VARCHAR(120) NOT NULL,
    sender_email VARCHAR(180) NOT NULL,
    subject VARCHAR(180) NOT NULL,
    message TEXT NOT NULL,
    client_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_contact_messages_recent_by_client ON contact_messages (client_hash, created_at DESC);
CREATE INDEX idx_contact_messages_created_at ON contact_messages (created_at DESC);
