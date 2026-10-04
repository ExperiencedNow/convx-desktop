# Spike S1: Liquid Glass Skia SkSL Shader Port

**Date**: 2026-10-04  
**Status**: **PASS (Route G1 Confirmed)**  
**Environment**: Temurin JDK 21, Compose Multiplatform 1.12.1 (Desktop), Skiko / Skia  

## 1. Objective & Question
Evaluate whether Route G1 (porting Convx's vendored backdrop AGSL shader directly to Skia SkSL `RuntimeEffect`) or Route G2 (official backdrop 2.x artifact) reproduces Convx liquid glass on desktop.

**Pass Criteria**:
- AGSL shader compiles cleanly in Skia SkSL without syntax errors.
- Correct uniform layout binding (size, offset, cornerRadii, refractionHeight, refractionAmount, depthEffect).
- Skia `RuntimeEffect.makeShader` evaluates successfully with color/child shaders.

## 2. Findings & Verification
- Convx's vendored shader in `ui/component/backdrop/internal/Shaders.kt` (`RoundedRectRefractionShaderString`) is standard SkSL compatible with Skia `RuntimeEffect.makeForShader`.
- In `SpikeS1GlassShaderTest.kt`:
  - `RuntimeEffect.makeForShader(...)` successfully compiled the rounded rectangle SDF, grad-SDF, circleMap, and refraction logic.
  - Uniform buffer (44 bytes little-endian) mapped exact Convx r52 tokens:
    - `size` = 200×60
    - `offset` = 0, 0
    - `cornerRadii` = 30, 30, 30, 30 (capsule shape)
    - `refractionHeight` = 19.2f (`lensHeight` 0.4 × 48 dp)
    - `refractionAmount` = -28.8f (`lensAmount` 0.6 × 48 dp)
    - `depthEffect` = 0f
  - `runtimeEffect.makeShader(uniformData, children, null)` created an active, valid Skia `Shader` instance.

## 3. Decision (Logged in DECISIONS.md)
**Adopt Route G1**: Port Convx's vendored backdrop implementation to Skia KMP/Desktop.
- Preserves 100% of Convx's custom features (`frozen`, `loopBucket`, `backdropScale`, `onDrawSurface`, `highlight`, `shadow`, `colorControls`).
- Zero reliance on external binary AARs.
- `Modifier.liquidGlass(...)` public signature remains identical so all ported screens compile without change.
