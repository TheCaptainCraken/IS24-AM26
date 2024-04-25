# Connection diagrams

## Login
Login phase illustrates the interaction between the client and the server when a player wants to join a lobby. The server must check if the lobby is unlocked and if the player is the first to join. If the lobby is unlocked and the player is the first, the server must create the lobby and wait for the other players to join. If the lobby is unlocked and the player is not the first, the server must wait for the lobby to be created. If the lobby is locked, the server must notify the client that the lobby is locked. If the player is admitted to the lobby, the server must wait for the player to log in and choose a colour. If the player is not admitted to the lobby, the server must notify the client that the lobby is full.
```mermaid
sequenceDiagram
    actor Client
    participant Server
    participant Controller

    Client ->> Server: Ping
    Server ->> Client: Pong //perchè avere il pong??
    alt if lobby is unlocked
        alt if is the first
            Server ->> Client: First ()
            Client ->> Server: Players (number of players)
            Server ->> Controller: ???
        else if is not the first
            alt if the lobby is not created yet
                Server ->> Client:  Wait(lobby not created yet) 
            end   
        end
        Server ->> Client: Ok, answer/admitted
        alt if admitted
            loop until login successful
                Client ->> Server: Login (username)
                Server ->> Controller: ??
                    alt username already taken
                        Server ->> Client: Error(username already taken)
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
    participant Server
    participant Controller
    participant Model
    Model ->> Controller: Get Info
    Controller ->> Server: Info (cards on the table but no objectives)
    
    Server ->> Client: Info (cards on the table but no objectives)
    Model ->> Controller: get (StartingCard)
    Controller ->> Server: get (starting card)
    Server ->> Client: Place (starting card)
    
    Client ->> Server: Place (up or down)
    Server ->> Controller: Nome ufficiale //TODO
    Controller ->> Model: Nome ufficiale //TODO
    
    Model ->> Controller: OK
    Controller ->> Server: OK
    Server ->> Client: Placed Card
    
    Server ->> Client: Info (common objectives)
    Server ->> Client: Choose (objective cards)
    Client ->> Server: Choose (objective card)
    Server ->> Client: Info (cards in the player's hand)
    Server ->> Client: Info (player turn order)
```
//ricontrollare game start

## Game Flow

```mermaid
sequenceDiagram
    actor Client
    participant Server
    participant Controller
    participant Model
    Server ->> Client: Info (player's turn starts now)
   
        loop until card is valid
        rect rgba(0, 255, 0, 0.2)
        Client ->> Server: PlaceCard(card, position, side)
        Server ->> Controller: PlaceCard(card, position, side)
        Controller ->> Model: PlaceCard(card, position, side)
            alt card error
                rect rgba(0, 255, 0, 0.2)
                Server ->> Client: Error (type of error)
                end 
            end
        end
        
        end
        Model ->> Controller: Ok (points, resources, card position)
        Controller ->> Server: Ok (points, resources, card position)
        Server ->> Client: Ok (points, resources, card position)
        loop until draw is valid 
            Client ->> Server: Draw (from what place)
            Server ->> Controller: Draw (from what place)
            Controller ->> Model: Draw (from what place)
            alt draw error
                Server ->> Client: Error (type of error)
            end
        Model ->> Controller: Give (card just drawn)
        Controller ->> Server: Give (card just drawn)
        Server ->> Client: Give (card just drawn)
        Server ->> Client: Info (table cards status)
        end
     
```

## End Game

During the end game, the turns are the same as in the game flow, just the server must notify the players of the fact that the game is about to end.

```mermaid
sequenceDiagram
    actor Client
    participant Server
    participant Controller
    participant Model
    Client ->> Server: DrawCard(from what place)
    Note over Server,Client: Same interaction as Game Flow,
    Note over Server,Client:   when condition is met, game end start
    Server ->> Client: Info (extra points)
    Server ->> Client: Info (who won)
```
