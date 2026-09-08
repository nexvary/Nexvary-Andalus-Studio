# Third-Party Licenses

Nexvary Andalus Studio keeps permissive/open-source dependencies separated from proprietary project logic. Every dependency must be reviewed before production use.

## Yjs

- Package: `yjs`
- Version range used by Web Studio: `^13.6.32`
- License: MIT
- Purpose: CRDT/local-first collaboration foundation, shared document state, conflict-free merging and undo/redo.
- Upstream: `yjs/yjs`

MIT License

Copyright (c) 2023 Kevin Jahns <kevin.jahns@protonmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Policy

- MIT / Apache-2.0 / BSD / CC0 components may be integrated after source and license review.
- LGPL components remain isolated where practical and must preserve their license obligations.
- GPL/AGPL tools must not be linked into the proprietary core unless distribution obligations are explicitly accepted; prefer external-process/service boundaries.
- Research-only or non-commercial code/models are not accepted into production manifests.
- Model weights and media assets are licensed independently from the libraries that load them.
