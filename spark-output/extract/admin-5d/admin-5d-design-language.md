# Design Language: My Google AI Studio App

> Extracted from `http://shengtaiprd.pancosky.com/5dadmin/` on June 14, 2026
> 348 elements analyzed

This document describes the complete design language of the website. It is structured for AI/LLM consumption — use it to faithfully recreate the visual design in any framework.

## Color Palette

### Primary Colors

| Role | Hex | RGB | HSL | Usage Count |
|------|-----|-----|-----|-------------|
| Primary | `#e4e7eb` | rgb(228, 231, 235) | hsl(214, 15%, 91%) | 28 |
| Secondary | `#c12a00` | rgb(193, 42, 0) | hsl(13, 100%, 38%) | 19 |
| Accent | `#dae2ff` | rgb(218, 226, 255) | hsl(227, 100%, 93%) | 2 |

### Neutral Colors

| Hex | HSL | Usage Count |
|-----|-----|-------------|
| `#03060a` | hsl(214, 54%, 3%) | 280 |
| `#515b6d` | hsl(219, 15%, 37%) | 132 |
| `#252b39` | hsl(222, 21%, 18%) | 105 |
| `#ffffff` | hsl(0, 0%, 100%) | 44 |
| `#f2f4f7` | hsl(216, 24%, 96%) | 7 |
| `#a8a8b0` | hsl(240, 5%, 67%) | 3 |

### Background Colors

Used on large-area elements: `#f0f4f7`, `#ffffff`

### Text Colors

Text color palette: `#000000`, `#010205`, `#ffffff`, `#5d73ff`, `#a8a8b0`, `#030313`, `#91a4ff`, `#515b6d`, `#0e07ae`, `#140bec`

### Gradients

```css
background-image: linear-gradient(to right, oklch(0.208 0.042 265.755) 0%, oklch(0.257 0.09 281.288) 50%, oklch(0.208 0.042 265.755) 100%);
```

```css
background-image: linear-gradient(to right bottom, oklch(0.359 0.144 278.697) 0%, oklch(0.257 0.09 281.288) 50%, oklch(0.208 0.042 265.755) 100%);
```

```css
background-image: linear-gradient(to right top, oklab(0.969 0.0146488 0.00322665 / 0.1) 0%, rgba(0, 0, 0, 0) 100%);
```

### Full Color Inventory

| Hex | Contexts | Count |
|-----|----------|-------|
| `#03060a` | text, border | 280 |
| `#515b6d` | text, border | 132 |
| `#252b39` | text, border | 105 |
| `#ffffff` | text, border, background | 44 |
| `#140bec` | background, text, border | 34 |
| `#e4e7eb` | border, background | 28 |
| `#d7000d` | text, border | 20 |
| `#c12a00` | background, text, border | 19 |
| `#5d73ff` | text, border | 14 |
| `#00a849` | text, border | 10 |
| `#005122` | text, border | 8 |
| `#f2f4f7` | background | 7 |
| `#1f1dff` | background, text, border | 7 |
| `#0e07ae` | text, border | 6 |
| `#91a4ff` | text, border | 4 |
| `#a8a8b0` | text, border | 3 |
| `#090d16` | text, border | 3 |
| `#475b7c` | text, border | 3 |
| `#fee0e2` | background | 3 |
| `#d5fae9` | background | 3 |
| `#343dff` | border | 2 |
| `#dae2ff` | background | 2 |
| `#008134` | background | 2 |
| `#f5060a` | text, border | 2 |
| `#de7000` | background | 2 |
| `#ffc5c9` | border | 2 |
| `#08063c` | border | 1 |
| `#beccff` | background | 1 |
| `#fff6d3` | background | 1 |
| `#fc5200` | background | 1 |

## Typography

### Font Families

- **Inter** — used for all (348 elements)

### Type Scale

| Size (px) | Size (rem) | Weight | Line Height | Letter Spacing | Used On |
|-----------|------------|--------|-------------|----------------|---------|
| 30px | 1.875rem | 700 | 36px | -0.75px | h3 |
| 18px | 1.125rem | 900 | 28px | 0.9px | div, h2, span |
| 16px | 1rem | 400 | 24px | normal | html, head, meta, title |
| 14px | 0.875rem | 700 | 20px | normal | h3 |
| 12px | 0.75rem | 900 | 16px | normal | div, span, button, svg |
| 11px | 0.6875rem | 400 | 16.5px | normal | div, p, button, text |
| 10.5px | 0.6563rem | 500 | 10.5px | normal | div, svg, path, span |
| 10px | 0.625rem | 600 | 15px | normal | p, button, span, div |
| 9px | 0.5625rem | 400 | 11.25px | normal | span |

### Heading Scale

```css
h3 { font-size: 30px; font-weight: 700; line-height: 36px; }
h2 { font-size: 18px; font-weight: 900; line-height: 28px; }
h1 { font-size: 16px; font-weight: 400; line-height: 24px; }
h3 { font-size: 14px; font-weight: 700; line-height: 20px; }
```

### Body Text

```css
body { font-size: 12px; font-weight: 900; line-height: 16px; }
```

### Font Weights in Use

`400` (195x), `600` (91x), `700` (36x), `500` (23x), `900` (2x), `800` (1x)

## Spacing

**Base unit:** 2px

| Token | Value | Rem |
|-------|-------|-----|
| spacing-2 | 2px | 0.125rem |
| spacing-16 | 16px | 1rem |
| spacing-20 | 20px | 1.25rem |
| spacing-24 | 24px | 1.5rem |
| spacing-40 | 40px | 2.5rem |

## Border Radii

| Label | Value | Count |
|-------|-------|-------|
| sm | 4px | 4 |
| md | 8px | 14 |
| lg | 12px | 22 |
| lg | 16px | 10 |

## Box Shadows

**sm** — blur: 0px
```css
box-shadow: rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0.1) 0px 4px 6px -1px, rgba(0, 0, 0, 0.1) 0px 2px 4px -2px;
```

**sm (inset)** — blur: 0px
```css
box-shadow: rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0.05) 0px 2px 4px 0px inset;
```

**sm** — blur: 0px
```css
box-shadow: rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0.1) 0px 1px 3px 0px, rgba(0, 0, 0, 0.1) 0px 1px 2px -1px;
```

## CSS Custom Properties

### Colors

```css
--color-zinc-100: oklch(96.7% .001 286.375);
--color-amber-400: oklch(82.8% .189 84.429);
--tw-ring-shadow: 0 0 #0000;
--color-indigo-400: oklch(67.3% .182 276.935);
--color-indigo-300: oklch(78.5% .115 274.713);
--color-rose-200: oklch(89.2% .058 10.001);
--color-slate-200: oklch(92.9% .013 255.508);
--color-yellow-600: oklch(68.1% .162 75.834);
--color-pink-500: oklch(65.6% .241 354.308);
--color-orange-600: oklch(64.6% .222 41.116);
--color-slate-700: oklch(37.2% .044 257.287);
--color-red-900: oklch(39.6% .141 25.723);
--color-blue-700: oklch(48.8% .243 264.376);
--color-amber-600: oklch(66.6% .179 58.318);
--color-slate-500: oklch(55.4% .046 257.417);
--color-slate-900: oklch(20.8% .042 265.755);
--color-green-100: oklch(96.2% .044 156.743);
--tw-inset-ring-shadow: 0 0 #0000;
--color-emerald-700: oklch(50.8% .118 165.612);
--color-orange-50: oklch(98% .016 73.684);
--color-red-200: oklch(88.5% .062 18.334);
--color-indigo-800: oklch(39.8% .195 277.366);
--color-gray-300: oklch(87.2% .01 258.338);
--color-slate-300: oklch(86.9% .022 252.894);
--color-gray-500: oklch(55.1% .027 264.364);
--color-zinc-50: oklch(98.5% 0 0);
--color-gray-50: oklch(98.5% .002 247.839);
--color-emerald-600: oklch(59.6% .145 163.225);
--color-purple-100: oklch(94.6% .033 307.174);
--tw-ring-offset-color: #fff;
--color-white: #fff;
--color-gray-400: oklch(70.7% .022 261.325);
--color-slate-400: oklch(70.4% .04 256.788);
--color-indigo-50: oklch(96.2% .018 272.314);
--color-rose-100: oklch(94.1% .03 12.58);
--color-gray-100: oklch(96.7% .003 264.542);
--color-emerald-100: oklch(95% .052 163.051);
--color-red-800: oklch(44.4% .177 26.899);
--color-red-500: oklch(63.7% .237 25.331);
--color-emerald-200: oklch(90.5% .093 164.15);
--tw-ring-offset-width: 0px;
--color-zinc-300: oklch(87.1% .006 286.286);
--color-indigo-600: oklch(51.1% .262 276.966);
--color-sky-50: oklch(97.7% .013 236.62);
--color-sky-700: oklch(50% .134 242.749);
--color-slate-50: oklch(98.4% .003 247.858);
--color-neutral-900: oklch(20.5% 0 0);
--color-yellow-100: oklch(97.3% .071 103.193);
--tw-ring-offset-shadow: 0 0 #0000;
--color-blue-100: oklch(93.2% .032 255.585);
--color-gray-800: oklch(27.8% .033 256.848);
--color-green-600: oklch(62.7% .194 149.214);
--color-zinc-900: oklch(21% .006 285.885);
--color-orange-700: oklch(55.3% .195 38.402);
--color-indigo-950: oklch(25.7% .09 281.288);
--color-sky-100: oklch(95.1% .026 236.824);
--color-rose-50: oklch(96.9% .015 12.422);
--color-emerald-950: oklch(26.2% .051 172.552);
--color-amber-500: oklch(76.9% .188 70.08);
--color-rose-500: oklch(64.5% .246 16.439);
--color-red-700: oklch(50.5% .213 27.518);
--color-rose-600: oklch(58.6% .253 17.585);
--color-slate-100: oklch(96.8% .007 247.896);
--color-indigo-200: oklch(87% .065 274.039);
--color-emerald-500: oklch(69.6% .17 162.48);
--color-gray-900: oklch(21% .034 264.665);
--color-yellow-50: oklch(98.7% .026 102.212);
--color-gray-950: oklch(13% .028 261.692);
--color-rose-700: oklch(51.4% .222 16.935);
--color-zinc-400: oklch(70.5% .015 286.067);
--color-amber-700: oklch(55.5% .163 48.998);
--color-emerald-50: oklch(97.9% .021 166.113);
--color-gray-700: oklch(37.3% .034 259.733);
--color-indigo-500: oklch(58.5% .233 277.117);
--color-gray-600: oklch(44.6% .03 256.802);
--color-orange-100: oklch(95.4% .038 75.164);
--color-purple-50: oklch(97.7% .014 308.299);
--color-red-100: oklch(93.6% .032 17.717);
--color-blue-600: oklch(54.6% .245 262.881);
--color-green-200: oklch(92.5% .084 155.995);
--tw-border-style: solid;
--color-red-600: oklch(57.7% .245 27.325);
--color-red-50: oklch(97.1% .013 17.38);
--color-blue-200: oklch(88.2% .059 254.128);
--color-blue-50: oklch(97% .014 254.604);
--color-gray-200: oklch(92.8% .006 264.531);
--color-indigo-700: oklch(45.7% .24 277.023);
--color-black: #000;
--color-red-950: oklch(25.8% .092 26.042);
--color-neutral-400: oklch(70.8% 0 0);
--color-emerald-400: oklch(76.5% .177 163.223);
--color-green-700: oklch(52.7% .154 150.069);
--color-indigo-900: oklch(35.9% .144 278.697);
--color-purple-700: oklch(49.6% .265 301.924);
--color-amber-100: oklch(96.2% .059 95.617);
--color-yellow-500: oklch(79.5% .184 86.047);
--color-amber-50: oklch(98.7% .022 95.277);
--color-indigo-100: oklch(93% .034 272.788);
--color-emerald-800: oklch(43.2% .095 166.913);
--color-amber-200: oklch(92.4% .12 95.746);
```

### Spacing

```css
--spacing: .25rem;
--tw-space-y-reverse: 0;
```

### Typography

```css
--text-2xl: 1.5rem;
--text-lg: 1.125rem;
--text-base--line-height: 1.5;
--tracking-wider: .05em;
--font-mono: "Inter", -apple-system, BlinkMacSystemFont, "PingFang SC", "Microsoft YaHei", ui-sans-serif, system-ui, sans-serif;
--text-lg--line-height: calc(1.75 / 1.125);
--font-sans: "Inter", -apple-system, BlinkMacSystemFont, "PingFang SC", "Microsoft YaHei", ui-sans-serif, system-ui, sans-serif;
--font-weight-bold: 700;
--text-xs--line-height: calc(1 / .75);
--default-font-family: "Inter", -apple-system, BlinkMacSystemFont, "PingFang SC", "Microsoft YaHei", ui-sans-serif, system-ui, sans-serif;
--text-xl: 1.25rem;
--leading-relaxed: 1.625;
--leading-snug: 1.375;
--font-weight-black: 900;
--text-2xl--line-height: calc(2 / 1.5);
--tracking-wide: .025em;
--text-xl--line-height: calc(1.75 / 1.25);
--font-weight-semibold: 600;
--text-sm: .875rem;
--leading-tight: 1.25;
--font-weight-extrabold: 800;
--text-sm--line-height: calc(1.25 / .875);
--text-3xl--line-height: 1.2;
--text-3xl: 1.875rem;
--text-xs: .75rem;
--font-weight-medium: 500;
--font-weight-normal: 400;
--text-base: 1rem;
--default-mono-font-family: "Inter", -apple-system, BlinkMacSystemFont, "PingFang SC", "Microsoft YaHei", ui-sans-serif, system-ui, sans-serif;
--tracking-tight: -.025em;
```

### Shadows

```css
--tw-inset-shadow-alpha: 100%;
--tw-drop-shadow-alpha: 100%;
--tw-inset-shadow: 0 0 #0000;
--tw-shadow-alpha: 100%;
--tw-shadow: 0 0 #0000;
```

### Radii

```css
--radius-2xl: 1rem;
--radius-md: .375rem;
--radius-lg: .5rem;
--radius-3xl: 1.5rem;
--radius-xl: .75rem;
```

### Other

```css
--container-md: 28rem;
--animate-ping: ping 1s cubic-bezier(0, 0, .2, 1) infinite;
--tw-gradient-from: rgba(0, 0, 0, 0);
--tw-gradient-to: rgba(0, 0, 0, 0);
--tw-scale-z: 1;
--blur-2xl: 40px;
--container-sm: 24rem;
--tw-gradient-via-position: 50%;
--container-lg: 32rem;
--tw-gradient-to-position: 100%;
--default-transition-duration: .15s;
--animate-pulse: pulse 2s cubic-bezier(.4, 0, .6, 1) infinite;
--tw-gradient-from-position: 0%;
--default-transition-timing-function: cubic-bezier(.4, 0, .2, 1);
--tw-translate-z: 0;
--tw-gradient-via: rgba(0, 0, 0, 0);
--tw-scale-y: 1;
--container-3xl: 48rem;
--blur-xs: 4px;
--tw-translate-y: 0;
--animate-spin: spin 1s linear infinite;
--container-4xl: 56rem;
--blur-md: 12px;
--tw-divide-y-reverse: 0;
--container-2xl: 42rem;
--tw-translate-x: 0;
--tw-scale-x: 1;
--animate-bounce: bounce 1s infinite;
--blur-sm: 8px;
```

### Semantic

```css
success: [object Object];
warning: [object Object];
error: [object Object];
info: [object Object];
```

## Transitions & Animations

**Easing functions:** `[object Object]`

**Durations:** `0.15s`, `0.2s`, `0.5s`

### Common Transitions

```css
transition: all;
transition: color 0.15s cubic-bezier(0.4, 0, 0.2, 1), background-color 0.15s cubic-bezier(0.4, 0, 0.2, 1), border-color 0.15s cubic-bezier(0.4, 0, 0.2, 1), outline-color 0.15s cubic-bezier(0.4, 0, 0.2, 1), text-decoration-color 0.15s cubic-bezier(0.4, 0, 0.2, 1), fill 0.15s cubic-bezier(0.4, 0, 0.2, 1), stroke 0.15s cubic-bezier(0.4, 0, 0.2, 1), --tw-gradient-from 0.15s cubic-bezier(0.4, 0, 0.2, 1), --tw-gradient-via 0.15s cubic-bezier(0.4, 0, 0.2, 1), --tw-gradient-to 0.15s cubic-bezier(0.4, 0, 0.2, 1);
transition: 0.2s cubic-bezier(0.4, 0, 0.2, 1);
transition: 0.5s cubic-bezier(0.4, 0, 0.2, 1);
transition: 0.15s cubic-bezier(0.4, 0, 0.2, 1);
```

### Keyframe Animations

**spin**
```css
@keyframes spin {
  100% { transform: rotate(360deg); }
}
```

**ping**
```css
@keyframes ping {
  75%, 100% { opacity: 0; transform: scale(2); }
}
```

**pulse**
```css
@keyframes pulse {
  50% { opacity: 0.5; }
}
```

**bounce**
```css
@keyframes bounce {
  0%, 100% { animation-timing-function: cubic-bezier(0.8, 0, 1, 1); transform: translateY(-25%); }
  50% { animation-timing-function: cubic-bezier(0, 0, 0.2, 1); transform: none; }
}
```

## Component Patterns

Detected UI component patterns and their most common styles:

### Buttons (26 instances)

```css
.button {
  background-color: oklch(0.967 0.003 264.542);
  color: oklch(0.551 0.027 264.364);
  font-size: 11px;
  font-weight: 700;
  padding-top: 10px;
  padding-right: 10px;
  border-radius: 8px;
}
```

### Cards (6 instances)

```css
.card {
  background-color: rgb(255, 255, 255);
  border-radius: 16px;
  box-shadow: rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0.1) 0px 1px 3px 0px, rgba(0, 0, 0, 0.1) 0px 1px 2px -1px;
  padding-top: 20px;
  padding-right: 20px;
}
```

### Navigation (1 instances)

```css
.navigatio {
  color: oklch(0.21 0.034 264.665);
  padding-top: 0px;
  padding-bottom: 0px;
  padding-left: 0px;
  padding-right: 0px;
  position: static;
}
```

## Component Clusters

Reusable component instances grouped by DOM structure and style similarity:

### Button — 4 instances, 2 variants

**Variant 1** (1 instance)

```css
  background: oklch(0.666 0.179 58.318);
  color: rgb(255, 255, 255);
  padding: 4px 10px 4px 10px;
  border-radius: 8px;
  border: 0px solid rgb(255, 255, 255);
  font-size: 10px;
  font-weight: 700;
```

**Variant 2** (3 instances)

```css
  background: rgba(0, 0, 0, 0);
  color: oklch(0.585 0.233 277.117);
  padding: 0px 0px 0px 0px;
  border-radius: 0px;
  border: 0px solid oklch(0.585 0.233 277.117);
  font-size: 11px;
  font-weight: 500;
```

### Button — 9 instances, 1 variant

**Variant 1** (9 instances)

```css
  background: oklch(0.962 0.018 272.314);
  color: oklch(0.457 0.24 277.023);
  padding: 10px 10px 10px 10px;
  border-radius: 12px;
  border: 0px 0px 0px 4px solid oklch(0.457 0.24 277.023);
  font-size: 12px;
  font-weight: 600;
```

### Button — 2 instances, 1 variant

**Variant 1** (2 instances)

```css
  background: rgba(0, 0, 0, 0);
  color: oklch(0.551 0.027 264.364);
  padding: 10px 10px 10px 10px;
  border-radius: 12px;
  border: 0px solid oklch(0.551 0.027 264.364);
  font-size: 12px;
  font-weight: 600;
```

## Layout System

**4 grid containers** and **73 flex containers** detected.

### Grid Column Patterns

| Columns | Usage Count |
|---------|-------------|
| 3-column | 2x |
| 4-column | 1x |
| 2-column | 1x |

### Grid Templates

```css
grid-template-columns: 314.656px 314.656px 314.656px;
gap: 24px;
grid-template-columns: 314.656px 314.672px 314.656px;
gap: 24px;
grid-template-columns: 236px 236px 236px 236px;
gap: 16px;
grid-template-columns: 130.328px 130.328px;
gap: 12px;
```

### Flex Patterns

| Direction/Wrap | Count |
|----------------|-------|
| column/nowrap | 14x |
| row/nowrap | 59x |

**Gap values:** `12px`, `16px`, `24px`, `4px`, `6px`, `8px`

## Interaction States

### Button States

**"还原预设"**
```css
/* Hover */
background-color: oklch(0.666 0.179 58.318) → oklab(0.558469 0.106538 0.12393);
```
```css
/* Focus */
background-color: oklch(0.666 0.179 58.318) → oklch(0.555 0.163 48.998);
outline: rgb(255, 255, 255) none 3px → rgb(35, 35, 35) auto 1px;
```

**"分析仪表盘"**
```css
/* Focus */
outline: oklch(0.457 0.24 277.023) none 3px → rgb(28, 23, 61) auto 1px;
```

**"用户管理"**
```css
/* Hover */
color: oklch(0.551 0.027 264.364) → oklab(0.28645 -0.00304701 -0.0322871);
background-color: rgba(0, 0, 0, 0) → oklab(0.985 -0.00075442 -0.00185226 / 0.543065);
border-color: oklch(0.551 0.027 264.364) → oklab(0.28645 -0.00304701 -0.0322871);
outline: oklch(0.551 0.027 264.364) none 3px → oklab(0.28645 -0.00304701 -0.0322871) none 3px;
```
```css
/* Focus */
color: oklch(0.551 0.027 264.364) → oklch(0.21 0.034 264.665);
background-color: rgba(0, 0, 0, 0) → oklab(0.985 -0.00075442 -0.00185226 / 0.7);
border-color: oklch(0.551 0.027 264.364) → oklch(0.21 0.034 264.665);
outline: oklch(0.551 0.027 264.364) none 3px → rgb(16, 18, 21) auto 1px;
```

## Accessibility (WCAG 2.1)

**Overall Score: 94%** — 17 passing, 1 failing color pairs

### Failing Color Pairs

| Foreground | Background | Ratio | Level | Used On |
|------------|------------|-------|-------|---------|
| `#f5060a` | `#fde3e3` | 3.52:1 | FAIL | span (1x) |

### Passing Color Pairs

| Foreground | Background | Ratio | Level |
|------------|------------|-------|-------|
| `#252b39` | `#e4e7eb` | 11.41:1 | AAA |
| `#005122` | `#d5fae9` | 8.49:1 | AAA |
| `#515b6d` | `#e4e7eb` | 5.52:1 | AA |
| `#ffffff` | `#c12a00` | 5.84:1 | AA |
| `#ffffff` | `#140bec` | 9.17:1 | AAA |

## Design System Score

**Overall: 90/100 (Grade: A)**

| Category | Score |
|----------|-------|
| Color Discipline | 80/100 |
| Typography Consistency | 80/100 |
| Spacing System | 100/100 |
| Shadow Consistency | 100/100 |
| Border Radius Consistency | 100/100 |
| Accessibility | 94/100 |
| CSS Tokenization | 100/100 |

**Strengths:** Well-defined spacing scale, Clean elevation system, Consistent border radii, Strong accessibility compliance, Good CSS variable tokenization

**Issues:**
- 1 WCAG contrast failures
- 559 duplicate CSS declarations

## Gradients

**3 unique gradients** detected.

| Type | Direction | Stops | Classification |
|------|-----------|-------|----------------|
| linear | to right | 3 | bold |
| linear | to right bottom | 3 | bold |
| linear | to right top | 2 | brand |

```css
background: linear-gradient(to right, oklch(0.208 0.042 265.755) 0%, oklch(0.257 0.09 281.288) 50%, oklch(0.208 0.042 265.755) 100%);
background: linear-gradient(to right bottom, oklch(0.359 0.144 278.697) 0%, oklch(0.257 0.09 281.288) 50%, oklch(0.208 0.042 265.755) 100%);
background: linear-gradient(to right top, oklab(0.969 0.0146488 0.00322665 / 0.1) 0%, rgba(0, 0, 0, 0) 100%);
```

## SVG Icons

**20 unique SVG icons** detected. Dominant style: **outlined**.

| Size Class | Count |
|------------|-------|
| xs | 5 |
| sm | 11 |
| md | 3 |
| xl | 1 |

**Icon colors:** `currentColor`, `#f1f5f9`, `#4f46e5`, `#10b981`, `#f59e0b`, `#ec4899`, `rgb(0, 0, 0)`

## Motion Language

**Feel:** mixed · **Scroll-linked:** yes

### Duration Tokens

| name | value | ms |
|---|---|---|
| `xs` | `150ms` | 150 |
| `sm` | `200ms` | 200 |
| `lg` | `500ms` | 500 |

### Easing Families

- **custom** (31 uses) — `cubic-bezier(0.4, 0, 0.2, 1)`

### Keyframes In Use

| name | kind | properties | uses |
|---|---|---|---|
| `ping` | reveal | opacity, transform | 1 |
| `pulse` | fade | opacity | 3 |

## Component Anatomy

### button — 15 instances

**Slots:** label
**Sizes:** lg · xl · medium

## Brand Voice

**Tone:** neutral · **Pronoun:** third-person · **Headings:** all-lowercase (tight)

### Top CTA Verbs

- **d** (1)

### Button Copy Patterns

- "前往处理" (3×)
- "还原预设" (1×)
- "分析仪表盘" (1×)
- "用户管理" (1×)
- "车行管理" (1×)
- "车行注册审核
3" (1×)
- "5d 车源管理" (1×)
- "求购管理" (1×)
- "交易管理
1" (1×)
- "保证金现金流" (1×)

### Sample Headings

> 5D Auto Operation Desk
> 5D Auto 数据全景透视
> ￥928,500
> 3 台
> ￥7,000
> 3 件

## Page Intent

**Type:** `unknown` (confidence 0)

## Section Roles

Reading order (top→bottom): nav → sidebar → content → nav

| # | Role | Heading | Confidence |
|---|------|---------|------------|
| 0 | nav | 5D Auto Operation Desk | 0.4 |
| 1 | sidebar | — | 0.4 |
| 2 | nav | — | 0.9 |
| 3 | content | 5D Auto 数据全景透视 | 0.3 |

## Material Language

**Label:** `flat` (confidence 0)

| Metric | Value |
|--------|-------|
| Avg saturation | 0.593 |
| Shadow profile | soft |
| Avg shadow blur | 0px |
| Max radius | 16px |
| backdrop-filter in use | no |
| Gradients | 3 |

## Component Library

**Detected:** `tailwindcss` (confidence 0.976)

Evidence:
- tailwind-like class density 91%

## Quick Start

To recreate this design in a new project:

1. **Install fonts:** Add `Inter` from Google Fonts or your font provider
2. **Import CSS variables:** Copy `variables.css` into your project
3. **Tailwind users:** Use the generated `tailwind.config.js` to extend your theme
4. **Design tokens:** Import `design-tokens.json` for tooling integration
