# Connection diagrams

## Login

```mermaid
sequenceDiagram
    actor Client
    Client ->> Server: Login (username)
    alt client is the first
        Server ->> Client: First ()
        Client ->> Server: Players (number of players)
    end
    Server ->> Client: Ok (available color pins)
    Client ->> Server: Color (color from previous message)
    Server ->> Client: Ok (number of players in the lobby)

```

## Game Start

```mermaid
sequenceDiagram
    actor Client
    Server ->> Client: Info (cards on the table but no objectives)
    Server ->> Client: Place (starting card)
    Client ->> Server: Place (up or down)
    Server ->> Client: Info (common objectives)
    Server ->> Client: Choose (objective cards)
    Client ->> Server: Choose (objective card)
    Server ->> Client: Info (cards in the player's hand)
    Server ->> Client: Info (player turn order)
```

## Game Flow

This is a loop.

```mermaid
sequenceDiagram
    actor Client
    Server ->> Client: Info (player's turn starts now)
    Client ->> Server: Place (card, position, ...)
    Server ->> Client: Ok (points, resources, card position)
    Client ->> Server: Draw (from what place)
    Server ->> Client: Give (card just drawn)
    Server ->> Client: Info (table cards status)
```

## End Game

During the end game, the turns are the same as in the game flow, just the server must notify the players of the fact that the game is about to end.

```mermaid
sequenceDiagram
    actor Client
    Server ->> Client: Info (extra points)
    Server ->> Client: Info (who won)
    Client --x Server: HASTA LA VISTA, BABY
```
