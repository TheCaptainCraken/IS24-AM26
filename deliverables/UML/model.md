# Model UML

## Cose da chiedere

- Dobbiamo fare partite consecutive?
- Va bene usare solo `GameMaster` per interfacciarsi con il controller?
- Si può vedere la mano degli avversari? Solo un lato?
- Come gestiamo il mandare in giro il grafo con le carte piazzate?
- Lobby?
- Gigantesco switch o implementiamo un interfaccia per le enumerazioni??
- Gerarchia di enumerazioni?

```mermaid
classDiagram

    class Kingdom {
        <<enumeration>>
        MUSHROOM
        ANIMAL
        PLANT
        INSECT
    }

    class Sign {
        <<enumeration>>
        MUSHROOM
        LEAF
        BUTTERFLY
        WOLF
        QUILL        
        INKWELL
        SCROLL
        EMPTY
    }

    class Color {
        RED
        BLUE
        YELLOW
        GREEN
    }

    class Card {
        <<abstract>>
        - id: int
        - kingdom: Kingdom
        + getId() int
        + getKingdom() Kingdom
    }

    Card --* Kingdom : is from

    class ObjectiveType {
        STAIR
        L_FORMATION
        FREE_RESOURCES
        TWO_QUILLS
        TWO_INKS
        TWO_SCROLLS
        TRIS

    }

    class ObjectiveCard {
        - type: ObjectiveType
        - multiplier: int

        + countPoints(Table table) int
        + getType() ObjectiveType
        + getMultiplier() int
    }

    ObjectiveCard --* ObjectiveType : is of type 

    class Corner {
        <<enumeration>>
        TOP_LEFT
        TOP_RIGHT
        BOTTOM_LEFT
        BOTTOM_RIGHT
    }

    class PlayableCard {
        <<abstract>>
        - corners: HashTable~Corner, Sign~

        + GetCorners() HashTable~Corner, Sign~
        + 
    }

    PlayableCard --* Corner
    PlayableCard --* Sign : requires at a corner

    class StartingCard {
        - backSideCorners: HashTable~Corner, Sign~
        - bonusResources: Set~Sign~

        + getBonusResources() Set~Sign~
        + getBackSideCorners() HashTable~Corner, Sign~
    }

    StartingCard --* Sign
    StartingCard --* Corner


    class ResourceCard {
        - points: int

        + getPoints() int
    }

    class GoldCard {
        - requirements: HashTable~Kingdom, int~

        + getRequirements() HashTable~Kingdom, int~
    }

    GoldCard --* Sign : requires to play
    GoldCard --* Kingdom

    class SpecialGoldCard {
        - thingToCount: Countable
        - getCountable() Countable

        + pointsToAdd(PlayedCard root) int
    }

    SpecialGoldCard --* Countable : counts

    Card <|-- ObjectiveCard
    Card <|-- PlayableCard
    PlayableCard <|-- StartingCard
    PlayableCard <|-- ResourceCard
    ResourceCard <|-- GoldCard
    GoldCard <|-- SpecialGoldCard

    class Deck {
        - cards: ArrayList~Card~
        - shuffle() void
        + draw() Card
        + reset() void
        + seeFirst() Kingdom
    }

    Deck --* Card : contains

    class Player {
        - name: String
        - points: int
        - hand: ArrayList~Card~
        - color: Color
        - rootCard: StartingCard 
        - secretObjective: ObjectiveCard
        - resources: HashMap~Sign, int~

        + getHand() ArrayList~Card~ 
        + getResources() HashMap~Sign, int~
        + getPoints() int
        + getColor() Color
        + getRootCard() PlayedCard
        + getSecretObjective() ObjectiveCard
        + getName() String
    }

    Player --* Card : has in their hand 
    Player --* Color : has color
    Player --* ObjectiveCard

    class GameState {
        PLAY
        END
        DRAWING_PHASE
        PLACING_PHASE
        ENDGAME
    }

    class GameMaster {
        - players: HashTable~String, Player~
        - turnNumber: int
        - currentPlayer: String
        - resourceDeck: Deck
        - goldDeck: Deck
        - objectivesDeck: Deck
        - staringDeck: Deck
        - gameState: enum
        - publicObjectiveCards: Array~ObjectiveCard~
        - publicResourceCards: Array~ResourceCard~
        - publicGoldCards: Array~GoldCards~

        + gameSetup() void
        + placeCard(String player, int cardId, Point position) void
        + getPlayerPoints(String player) int
        + getPlayerHand(String player) ArrayList~Card~
        + drawPhase(String player) void
        + endGame() String
        + getGameState() GameState
        + getTurnNumber() int
        + getCurrentPlayer() String
    }

    GameMaster --* Player : manages
    GameMaster --* Deck : manages
    GameMaster --* GameState : is in state
    GameMaster --* PlayableCard : shows
    GameMaster --* ResourceCard
    GameMaster --* ObjectiveCard
    GameMaster --* PlayedCard


    class Countable {
        INKWELL
        QUILL
        SCROLL
        CORNER
    }

    class PlayedCard {
        - card: PlayableCard
        - isFacingUp: bool
        - attachmentCorners: HashMap~Corner, PlayedCard~
        - countedForObjective: bool %% chiedere al prof
        - turnOfPositioning: int
        - position: Point~int, int~

        + getCard() Card
        + isFacingUp() bool
        + attachCard(PlayedCard p) void
        + setCountedForObjective() void
        + getAttached(Corner corner) PlayedCard
    }

    PlayedCard --* PlayableCard
    PlayedCard --* Corner
```
