# Task 23: Vibe Coding and Prompt Engineering Framework

## Overview
A comprehensive technical laboratory analyzing **Vibe Coding** methodologies and **Prompt Engineering Techniques** for enterprise software development using Generative AI. Evaluates naive vs. structured prompt design patterns across 5 mission-critical business engineering scenarios: Spring Security 6 stateless JWT filters, Terraform cloud infrastructure as code, atomic Redis rate limiters, Flyway database index migrations, and production Kubernetes manifests with health probes.

---

## The Vibe Coding Engineering Methodology

```
+-------------------------------------------------------+
| 1. Architecture Framing (Human Architect)             |
|    - Define bounded contexts, data contracts, SLIs   |
+-------------------------------------------------------+
                           |
                           v
+-------------------------------------------------------+
| 2. Structured Prompt Generation (Prompt Engineering)  |
|    - System persona, input/output schemas, guardrails |
+-------------------------------------------------------+
                           |
                           v
+-------------------------------------------------------+
| 3. AI Code Synthesis (Generative AI Tool)             |
|    - Produces clean code, IaC, and test slices        |
+-------------------------------------------------------+
                           |
                           v
+-------------------------------------------------------+
| 4. Automated Verification & Critique (Human-in-Loop)  |
|    - Compiler, linter, tests, and security audits     |
+-------------------------------------------------------+
```

---

## Artifacts in this Directory

- `PROMPT_ENGINEERING_LAB.md`: Comprehensive enterprise lab manual containing 5 end-to-end business case studies, side-by-side prompt comparisons (Naive vs Engineered), generated code analysis, security auditing, and rubric definitions.
- `evaluate_prompts.py`: Python CLI tool computing weighted performance scores comparing Naive vs Engineered prompt generation across Correctness, Security, Maintainability, Performance, and Efficiency.
- `Run.txt`: Run commands.

---

## Running the Benchmark Evaluation

```bash
cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 23 - Vibe Coding and Prompt Engineering"
python evaluate_prompts.py
```

### Verified Benchmark Results
```text
======================================================================
 TASK 23: PROMPT ENGINEERING & VIBE CODING EVALUATION MATRIX
======================================================================
Business Scenario                   | Naive        | Engineered   | Gain
----------------------------------------------------------------------
Spring Security 6 JWT Filter        |   50.0 / 100 |   95.7 / 100 | +45.7%
Terraform AWS ECS Infrastructure    |   49.8 / 100 |   92.6 / 100 | +42.8%
Atomic Redis Rate Limiter           |   49.8 / 100 |   95.3 / 100 | +45.5%
Flyway DB Migration with Indexes    |   56.2 / 100 |   94.0 / 100 | +37.8%
Production Kubernetes Manifests     |   48.0 / 100 |   93.8 / 100 | +45.8%
----------------------------------------------------------------------
OVERALL WEIGHTED AVERAGE            |   50.7 / 100 |   94.3 / 100 | +43.5%
======================================================================
Core Finding: Prompt engineering eliminates 90%+ security vulnerabilities
and prevents production runtime crashes across distributed systems.
======================================================================
```
