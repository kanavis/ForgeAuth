
# ForgeAuth
Minecraft Forge local auth module.
This module grants an ability to authorize players without online server mode.

Currently, only whitelist strategy is available, adding users via creating files.

**How to use:**
> Module creates forgelogin_data/auth directory inside minecraft working dir.

> To add user, create a file \<username\>.data inisde this directory.

> If file is empty, user may set his password with **/password \<password\>** command.

> If file is not empty, it's contents are treated as password's sha256 hash.

> To login, write **/login \<password\>**

> That's all.
