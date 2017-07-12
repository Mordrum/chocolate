# chocolate

This is the official repository for Mordrum's modded Minecraft server.

## Contribution Guidelines

When submitting a pull request, please adhere to the code guidelines:

- Java code is legacy and is being replaced with Kotlin, avoid touching it if possible
- Constructors must not contain side effects
- All visible methods and classes must have Javadoc comments
- Comment your code as much as possible while remaining terse about it (remember to comment why, not what)
- Ensure that both client and server compile and run without crashing
- Only work on one feature at a time
- Avoid updating multiple mods in that same pull request / feature branch
