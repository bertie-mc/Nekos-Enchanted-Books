# Neko's Enchanted Books (NeoForge)

Gives enchanted books a unique texture per enchantment. Independent NeoForge 1.21.1 port of **Neko's Enchanted Books** by Infernal Studios, reworked to wrap the enchanted-book model via NeoForge ModelEvents instead of Forge JS coremods. Client-side only.

- **Minecraft:** 1.21.1
- **Loader:** NeoForge
- **Mod ID:** `nebs`

## Install

Download the latest JAR from the [Releases page](../../releases) and put it in your `mods/` folder. Requires NeoForge for Minecraft 1.21.1.

## Credits / Integration

Port of **Neko's Enchanted Books** by Infernal Studios (Nekomaster, CGessinger, Jonathing). This is an independent NeoForge reimplementation, published as a public GitHub fork with attribution; the original mod and its assets belong to their authors.

## Development

The shared Bertie Nix environment supplies JDK 21, Gradle 8.14.4, and the test tooling;
this repository intentionally has no Gradle wrapper or host-installed toolchain setup:

```bash
nix develop 'github:bertie-mc/bertie?ref=bertie-ci/v5.0.0'
```

Build and test are separate operations:

```bash
gradle assemble
gradle test
```

The releaseable JAR is written to `build/libs/`.

## Tests

`gradle test` verifies that every enchantment mapping resolves to a valid model and
texture. The `model-contract` CI suite builds a test-only mod and verifies the
enchanted-book model replacement in a headless client. Test-mod classes are excluded
from release JARs.

CI behavior is declared in `bertie-ci.toml` and executed by the reusable workflows from
the Bertie monorepo. This fork continues to release with its existing `vX.Y.Z` tags;
namespaced monorepo tags do not apply here.

## License

This is an independent NeoForge port/reimplementation of a third-party mod. The upstream project has no explicit open-source license, so this port is shared publicly as an attributed fork rather than under a declared license. All original code and assets belong to their respective authors (see attribution above).
