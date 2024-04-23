# Connection diagrams

## Login

```mermaid
sequenceDiagram
    actor Client
    Client ->> Server: Ping
    Server ->> Client: Pong
    alt if lobby is unlocked
        alt if is the first
            Server ->> Client: First ()
            Client ->> Server: Players (number of players)
        else if is not the first
            alt if the lobby is not created yet
                Server ->> Client:  Wait(lobby not created yet) 
            end   
        end
        Server ->> Client: Ok, answer/admitted
        alt if admitted
            loop until login successful
                Client ->> Server: Login (username)
                    alt username already taken
                        Server ->> Client: Error (username already taken)
                    end
            end
              loop until colour is accepted
                  Server ->> Client: Ok (available colour pins)
                  Client ->> Server: Colour (chosen colour)
                  alt colour already taken
                    Server ->> Client: Error (colour already taken) 
                  end
            end
        else if not admitted
            Server ->> Client: Error (lobby is full)
        end
        Server ->> Client: Ok (number of players in the lobby, name)
        Server ->> Client: Game starts()
    else if lobby is locked
        Server ->> Client: Error (lobby is locked)
    end

```

## Game Start

```mermaid
sequenceDiagram
    actor Client
    Server ->> Client: Info (cards on the table but no objectives)
    Server ->> Client: Place (starting card)
    Client ->> Server: Place (up or down)
    Server ->> Client: Placed Card
    Server ->> Client: Info (common objectives)
    Server ->> Client: Choose (objective cards)
    Client ->> Server: Choose (objective card)
    Server ->> Client: Info (cards in the player's hand)
    Server ->> Client: Info (player turn order)
```

## Game Flow

```mermaid
sequenceDiagram
    actor Client
    Server ->> Client: Info (player's turn starts now)
    loop until place is accepted
        loop until card is valid 
        Client ->> Server: Place (card, position, ...)
        alt card error
            Server ->> Client: Error (type of error)
        end
        end
        Server ->> Client: Ok (points, resources, card position)
        loop until draw is valid 
            Client ->> Server: Draw (from what place)
            alt draw error
                Server ->> Client: Error (type of error)
            end
        end
            Server ->> Client: Give (card just drawn)
        Server ->> Client: Info (table cards status)
    end 
```

## End Game

During the end game, the turns are the same as in the game flow, just the server must notify the players of the fact that the game is about to end.

```mermaid
sequenceDiagram
    actor Client
    Server ->> Client: Info (extra points)
    Server ->> Client: Info (who won)
```
