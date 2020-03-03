# GUI
GUI is a custom GUI plugin made by me in order to more easily create custom GUIs that are extremely customizable and configurable.

**Permissions**<br>
Permissions are currently being worked on.

### GUI Configuration
#### GUI
A GUI contains **only** pages. Example:
```yaml
ServerSelector:
# This is a GUI. The GUI's name is ServerSelector
```

#### Page
A Page contains its **Name**, **Size**, **Items**, and (if applicable), **functions**. Example:
```yaml
ServerSelector: # This is the GUI
  Pages:
    MainPage:
      Name: '&6&lServer Selector'
      Size: 27 # 9 Rows
      Creative:
        Icon: 'DIAMOND_BLOCK'
        Name: '&a&lCreative Server'
        Slot: 0
        Lore:
        - ''
        - '&7Build, forge, and craft whatever'
        - '&7your imagine desires in this creative'
        - '&7landscape!'
```

#### Item
An item declares the item's **Icon** and **Slot**. Optional values are Lore, Name, ItemFlags, Enchantments, and Functions. Example:
```yaml
BroadcastItem:
  Icon: PAPER
  Name: '&c&lBroadcast Server Shutdown'
  Slot: 8
  Lore:
  - ''
  - '&7Broadcast a server shutdown message.'
  Functions:
    bc:
      Type: COMMAND
      Value:
      - 'CONSOLE'
      - 'say &c&lThe server is going to shutdown in 1 minute!'
```


### Functions
#### Item Functions

Item Functions are declared per-item when creating GUIs.

| ID | Values | Description | Example | Additional Info |
| --- | --- | --- | --- | --- |
| BUY | Cost (-1 to use prices.yml), Items (List) | Purchase the items listed for price listed in the first argument | Type: BUY<br>Value:<br>- '50'<br>- 'DIAMOND_SWORD'
| GOTO | Page Name | Opens a page within the same GUI from ID | Type: GOTO<br> Value: 'DefaultPage'
| OPEN_GUI | GUI Name, Page Name | Opens a page from the specified GUI | Type: OPEN_GUI<br>Value:<br>- 'WarpMenu'<br>- 'OverworldPage' |
| PLAYSOUND | Sound Name, Volume, Pitch | Plays the specified sound at the specified volume and pitch | Type: PLAYSOUND<br>Value:<br>- 'UI_BUTTON_CLICK'<br>- '1'<br>- '2'
| COMMAND | Command source, Command(s) | Executes a command. Source can be CONSOLE, PLAYER, or OP (as if player is opped). | Type: COMMAND<br>Value:<br>- 'PLAYER'<br>- 'say My name is %name%<br>- 'say My UUID is %uuid%' | Placeholders:<br>%uuid% - Player's UUID<br>%name% - Player's Name<br>%world% - World's Name |
| MESSAGE | Messages to send | Sends a message to the player | Type: MESSAGE<br>Value:<br>- 'HELLO %NAME%' | Same placeholders as COMMAND |
#### Page Functions

Page Functions are declared per-page when creating GUIs. These are less configurable as they are meant to be more situationally specific such as updating a Sell GUI for values.

| ID | Description |
| --- | --- |
| ShopFunction | Treats the current GUI as a shop (Items put into it will be given a price tag) |