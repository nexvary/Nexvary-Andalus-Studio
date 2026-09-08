# Security Policy

## Supported branch

Security fixes target `main` and the current release candidate branch.

## Reporting a vulnerability

Please do not publish exploitable details in a public issue.

Report suspected vulnerabilities privately to:

- Email: info@nexvary.com

Include the affected component, reproduction conditions, expected impact and any non-sensitive logs that help reproduce the issue.

## Security boundaries

Nexvary Andalus Studio treats uploaded media, floorplans, BIM/CAD files, model weights and remote assets as untrusted input.

Production rules include:

- secure-import validation before parsing
- explicit Android HTTPS-only network policy
- architectural-lock validation around AI candidates
- immutable project revision fingerprints
- asset license and provenance records
- dependency review for new vulnerable packages
- SHA-256 plus GitHub/Sigstore provenance for APK builds

No secret, API token or private credential should be committed to this public repository.
