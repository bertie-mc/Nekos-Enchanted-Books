# AGENTS.md

Instructions for agents working on the NeoForge branch of this retained upstream fork.

## Project boundaries

This repository is intentionally separate from the `bertie-mc/bertie` monorepo. It is
an attributed NeoForge port of Neko's Enchanted Books and continues to track upstream
history. Do not move it into the monorepo or rewrite upstream commits.

The upstream project has no explicit open-source licence. Preserve `NOTICE`, attribution,
and the existing all-rights-reserved metadata. Do not add third-party code or assets
without establishing permission and recording their provenance.

## Toolchain and dependencies

- Minecraft 1.21.1, NeoForge 21.1.217, ModDevGradle 2.0.134, JDK 21, Gradle 8.14.4.
- Use the pinned Nix environment from the Bertie monorepo. Do not add a Gradle wrapper,
  Foojay resolver, `mavenLocal()`, vendored JAR, or sibling-checkout dependency.
- Resolve dependencies from explicit public repositories so a fresh checkout builds.
- Minecraft ecosystem version strings often encode a Minecraft version; do not treat
  them as ordinary semantic versions.

Build and test are separate operations. Use `gradle assemble` for the releaseable JAR,
`gradle test` for JVM tests, and the suites declared in `bertie-ci.toml` for integration
coverage. Test fixtures and assertions belong in this repository.

## Git and releases

The `neoforge` branch on GitHub is the source of truth. Use Conventional Commits, rebase
rather than merge when updating the branch, never force-push, and leave a clean tree with
one worktree. Do not discard concurrent changes.

Unlike monorepo components, this retained fork keeps its existing bare `vX.Y.Z` release
tags. Release only after the exact commit's build and test workflows pass. Never copy a
local JAR into the pack; pack updates consume published release assets.
