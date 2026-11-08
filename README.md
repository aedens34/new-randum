# Random Loot (TaCZ compatible)

Forge 1.20.1 mod. Randomizes what blocks drop and what mobs drop. If TaCZ is
installed, TaCZ guns (base mod and every addon) can show up as random drops too.

## Build with GitHub
1. Create a new GitHub repository and upload everything in this folder
   (including the hidden `.github` folder).
2. Open the Actions tab and wait for the green check.
3. Download the `randomloot-jar` artifact, extract it, and put the .jar in your mods folder.

## Config
After the first world load, edit `serverconfig/randomloot-server.toml` inside the world folder.
- randomizeBlocks / randomizeMobs / randomizeOther
- chaosMode: false = each item always maps to the same replacement in a world; true = fresh roll every drop
- seedOffset: change it for a different mapping in the same world
- includeTaczGuns, taczGunPercent: chance a replacement is a TaCZ gun
- excludedItems: items that can never be a replacement

## In-game menu (v1.1)

Press **Escape** in a world: a **Random** panel appears on the right side of the pause menu with three labelled buttons:
- **Random Blocks** - blocks drop random items
- **Random Mobs** - mobs drop random items
- **Random Random** - every drop rolls fresh, so the same block gives something different each time.
  The same item can come up again, but never back-to-back (the last 12 results are remembered) and never twice in one drop.
  When OFF, each item maps to the same replacement for the whole world.

On a server only operators can change the buttons. Settings are saved to the world's config.
Replacements come from every item of every installed mod, plus every gun from TaCZ and all TaCZ addons.
