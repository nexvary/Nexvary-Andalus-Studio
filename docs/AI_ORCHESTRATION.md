# AI Orchestration — Stage 625

The AI layer is deliberately separated from the deterministic geometry layer.

## Before generation

`compile_generation_plan()` converts source type + Architectural Locks into control requirements such as depth, edges, segmentation, opening masks and floor-plan geometry. It produces a request digest and marks the output as concept-only.

## After generation

`validate_locked_candidate()` compares the candidate's protected geometry fields against the baseline. A candidate that changes a locked field is rejected even if the image looks attractive.

## Provenance

When a real model is connected later, the provider, model ID, model license, source digest and generation-plan digest must be recorded. Model weights are not considered approved merely because the surrounding Python library is open source.
