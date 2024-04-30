# Connection diagrams

## Login
Login phase illustrates the interaction between the client and server before the creation of the game.
The client sends a ping to the server to establish a connection. The server checks:

1. Lobby is locked, so the game already started
2. The player is first, so he/she can create the game. 

While the first player decide the number of players, other clients are put in waiting state.
When the lobby is created, the server must notify the clients to choose username and colour. And when the last player 
confirms username and colour the game starts.

```mermaid
sequenceDiagram
    actor Client
    participant Server
    participant Controller

   loop until name is correct 
      rect rgba(0, 255, 0, 0.2)
         Client ->> Server: Login (username)
         Server ->> Controller: Login (username)
         Controller ->> Model: AddPlayer(username)

         alt username already taken
            rect rgba(255, 0, 0, 0.6)
               Model ->> Controller: Error(username already taken)
               Controller ->> Server: Error(username already taken)
               Server ->> Client: Error(username already taken)
            end
         end
      end
   end
    alt if lobby is unlocked
        alt if is the first
            rect rgba(0, 255, 0, 0.2)
            Model ->> Controller: First()
            Controller ->> Server: First()
            Server ->> Client: First ()
            Client ->> Server: Players (number of players)
            Server ->> Controller: CreateLobby()
            Controller  ->> Model: Lobby(number of players)
            end
        else if is not the first
            rect rgba(255, 165, 0, 0.7)
            alt if the lobby is not created yet
                Model ->> Controller: Wait()
                Controller ->> Server: Wait()
                Server ->> Client:  Wait(lobby not created yet) 
            end  
            end
        end

        rect rgba(0, 255, 0, 0.2)
        Model ->> Controller: Lobby created()
        Controller ->> Server: Lobby created()
        Server ->> Client: Ok, answer
        end
        alt if admitted
              loop until colour is accepted
              rect rgba(0, 255, 0, 0.2)
                  Model ->> Controller: ChooseColour (available colour pins)
                  Controller ->> Server: RefreshUsers (available colour pins)
                  Server ->> Client: RefreshUsers (available colour pins)
                  Client ->> Server: Colour (chosen colour)
                  Server ->> Controller: Colour (chosen colour)
                  Controller ->> Model: SetColour(nickname, colour)
                  alt colour already taken
                    rect rgba(255, 0, 0, 0.6)
                    Model ->> Controller: Error (colour already taken)  
                    Controller ->> Server: Error (colour already taken)
                    Server ->> Client: Error (colour already taken) 
                    end
                  end
                  end
            end
        else if not admitted
            rect rgba(255, 0, 0, 0.6)
            Model ->> Controller: Error (lobby is full)
            Controller ->> Server: Error (lobby is full)
            Server ->> Client: Error (lobby is full)
            end
        end
        rect rgba(0, 255, 0, 0.2)
        Model ->> Controller: Ok(number of players, name)
        Controller ->> Server: Ok(number of players, name)
        Server ->> Client: Ok (number of players in the lobby, name)
        
        Model ->> Controller: GameStart()
        Controller ->> Server: GameStart()
        Server ->> Client: GameStart()
        
        end
        
    else else if lobby is locked
        rect rgba(255, 0, 0, 0.6)
        Controller ->> Server: Error (lobby is locked, a game already started
        Server ->> Client: Error (lobby is locked, a game already started)
        end
    end

```

## Game Start
When the last player joins the lobby, the server must notify the clients that the game is about to start. 
This transaction is not figured here for simplicity, but is present in the implementation. 
The server must notify the clients of the starting card and the common objectives. 
The server must also notify the clients of the cards in the player's hand and the player turn order.

The green box represents the choose of starting card while the blue one is about choosing objective card.
```mermaid
sequenceDiagram
    actor Client
    participant Server
    participant Controller
    participant Model

    rect rgba(0, 255, 0, 0.2)
    Model ->> Controller: Get Info()
    Controller ->> Server: Info(cards on table)
    
    Server ->> Client: Info (cards on the table)
    Model ->> Controller: getStartingCard()
    Controller ->> Server: getStartingCard()
    Server ->> Client: Place (starting card)
    
    loop until card is valid
    Client ->> Server: PlaceStartingCard(side)
    Server ->> Controller: PlaceStartingCard(side) 
    alt Side input not allowed
        rect rgba(255, 0, 0, 0.6)
        Controller ->> Server: Error (side input not allowed)
        Server ->> Client: Error (side input not allowed)
        end
    end
    end 
    Controller ->> Model: PlaceStartingCard(side)
    
    Model ->> Controller: PlacedCard(points, position, resources)   
    Controller ->> Server: PlacedCard(points, position, resources)
    Server ->> Client: Placed Card(points, position, resources)
    end

    rect rgba(0, 255, 255, 0.3)
    Model ->> Controller: GetCommonObjectives()
    Controller ->> Server: GetCommonObjectives()
    Server ->> Client: Info (common objectives)
    
    Model ->> Controller: GetObjectiveCard()
    Controller ->> Server: GetObjectiveCard()
    Server ->> Client: Choose (objective cards)
        
    loop until card is valid
    Client ->> Server: ChooseObjectiveCard(whichOne)
    Server ->> Controller: ChooseObjectiveCard(whichOne)
    alt whichOne not allowed
        rect rgba(255, 0, 0, 0.6)
        Controller ->> Server: Error (whichOne not allowed)
        Server ->> Client: Error (whichOne not allowed)
        end
    end
    end
    Controller ->> Model: ChooseObjectiveCard(whichOne)
    
    Model ->> Controller: GetPlayerHand()
    Controller ->> Server: GetPlayerHand()
    Server ->> Client: Info (cards in the player's hand)
    
    Model ->> Controller: GetFirstPlayer()
    Controller ->> Server: GetFirstPlayer()
    Server ->> Client: Info (player turn order)
    end

```

## Game Flow
The game flow is the main part of the game. 
The player can place and draw cards. The server must notify the clients of the player's turn and the table cards status.
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
                rect rgba(255, 0, 0, 0.5)
                Model ->> Controller: Error(CardPositionException or NotEnoughResourcesException)
                Controller ->> Server: Error(CardPositionException or NotEnoughResourcesException)
                Server ->> Client: Error(CardPositionException or NotEnoughResourcesException)
                end 
            end
        end
        
        end
        Model ->> Controller: Ok (points, resources, card position)
        Controller ->> Server: Ok (points, resources, card position)
        Server ->> Client: Ok (points, resources, card position)
        loop until draw is valid
        rect rgba(0, 255, 0, 0.2)
        Client ->> Server: Draw (from what place)
            Server ->> Controller: Draw (from what place)
            Controller ->> Model: Draw (from what place)
            alt draw error
                rect rgba(255, 0, 0, 0.5)
                Model ->> Controller: Error (Place does not have a card)
                Controller ->> Server: Error (Place does not have a card)
                Server ->> Client: Error (Place does not have a card)
                end
            end
        end
        end
        Model ->> Controller: Give (card just drawn, new card on table)
        Controller ->> Server: Give (card just drawn, new card on table)
        Server ->> Client: Info (card just drawn, new card on table)

```

## End Game

During the end game, the turns are the same as in the game flow, just the server must notify the players of the fact that the game is about to end.
This can happen when the deck is empty or someone has reached more than 20 points. Our implementations checks it after each turn:
    
1. After each turn when someone draws a card
2. When deck is empty, we check this after the place phase

The two case are represented in the green box. Note: in a game just one of two can happen.

The blue part is the end game phase. 
The server must notify the clients of the extra points(objective card's points) and the winner.
```mermaid
sequenceDiagram
    actor Client
    participant Server
    participant Controller
    participant Model
    
    alt someone has more than 20 points
    rect rgba(0, 255, 0, 0.3)
    Client ->> Server: DrawCard(from what place)
    Server ->> Controller: DrawCard(from what place)
    Controller ->> Model: DrawCard(from what place)
    end
    else deck is empty
    rect rgba(0, 255, 0, 0.3)
    Client ->> Server: PlaceCard(card, position, side)
    Server ->> Controller: PlaceCard(card, position, side)
    Controller ->> Model: PlaceCard(card, position, side)
    end
    end
    
    rect rgba(0, 255, 255, 0.4)
    Model->>Controller: GameEnd()
    Controller->>Server: GameEnd()
    Server ->> Client: Info (extra points)
    
    Model ->> Controller: CalculateWinner()
    Controller ->> Server: CalculateWinner()
    Server ->> Client: Info (Winner)
    end
```
## Connection Lost
We implement the FA: "Resilienza alle disconessioni", so the game can continue even if a player loses the connection.
Here is our implementation of the connection lost:
1. If a client loses the connection during pregame, before choosing the colour, the server must notify the 
other clients that the player has disconnected. The game is then closed.
```mermaid
sequenceDiagram
    actor Client
    participant Server
    
    Client ->> Server: Connect(name)
    note over Server, Client: Timeout for choosing number of player, name or colour
    note right of Client: To all others clients connected we send a message that the player has disconnected
    Server ->> Client: Error (connection lost)
        
```
2. If client loses the connection during the game, the server randomly chooses what happens to the player status.
There are four cases based on when a client disconnects:
    1. chooseStartingCard
    2. chooseObjectiveCard
    3. placeCard and consequently after "drawCard"
    4. drawCard 

In all of these cases, the server can modify autonomously the player status, by simply choosing which card to play/draw and where, in the model. When the player reconnects, 
the server must notify the client of the changes that transpired in the game status. 
```mermaid
sequenceDiagram
 actor Client
 participant Server
 participant Controller
 participant Model
 
 Client ->> Server: Connect(name)
 Server ->> Controller: GetNewStatus()
 alt if player's name is incorrect
    rect rgba(255, 0, 0, 0.6)
        Controller ->> Server: Error(name doesn't exist)
        Server ->> Client: Error(name doesn't exist)
    end 
 end
 
 Controller ->> Model: GetNewStatus()
 Model ->> Controller: NewStatus()
 Controller ->> Server: NewStatus()
 Server ->> Client: NewStatus()
 
    
```

