# Bollywood Simulator

## Overview

Bollywood Simulator is an Android entertainment app that simulates a year in the Bollywood film industry. The app features 32 AI-controlled producers/players who compete through multiple elimination rounds. This is a passive simulation game where users observe outcomes rather than actively participate - they only control progression by clicking "Next Round" buttons.

The game follows a tournament-style structure where players take loans, produce movies with random earnings, and compete through elimination rounds until a winner is determined. The app is designed for personal entertainment and observation of simulated Bollywood industry dynamics.

## User Preferences

Preferred communication style: Simple, everyday language.

## Project Architecture

### Realism Improvement Suggestions
- **Dynamic Popularity**: Players' "Star Power" should fluctuate based on recent movie performance rather than being purely random.
- **Career Trajectories**: Introduce "Experience" levels. Newcomers have lower budgets but potential for high growth, while veterans have high budgets and stable returns.
- **Budget-to-Earnings Ratio**: Make the budget matter. A movie with a budget of ₹100 that earns ₹100 is a "Flop", while a budget of ₹10 that earns ₹50 is a "Super Hit".
- **Marketing & Hype**: Add a "Marketing Spend" option that increases the base earnings multiplier but adds to the upfront cost.
- **Critical vs. Commercial Success**: Separate "Box Office" from "Critic Rating". A movie can be a commercial hit but a critical disaster, affecting long-term popularity differently.
- **Genre Fatigue**: If many players choose "Action" in the same round, the earnings for that genre should decrease due to market saturation.
- **Awards & Legacy**: Winning an Oscar or having the "Top Movie of the Year" should provide a permanent boost to a player's starting multiplier for the next year.

### Future Realism Ideas
- **Marketing Spend**: Allow players to choose a marketing budget (Low/Medium/High) that increases the base earnings multiplier but adds to the upfront cost.
- **Critic vs. User Ratings**: Separate "Critic Score" from "Audience Score". High critic scores could lead to more awards, while high audience scores lead to more money.
- **Genre Fatigue & Trends**: If too many players choose "Action", the market gets saturated, and earnings for Action movies drop in the next round.
- **Contract Negotiations**: Players could sign multi-movie deals with directors or studios for consistent (but safer) returns.
- **Social Media Hype**: A "Hype" meter that builds up before a movie release based on previous hits, affecting opening day earnings.
- **Director Reputation**: Associate movies with "AI Directors" who have their own success rates and styles.

### Build Note
- **CRITICAL**: Do not attempt to build the project within the Replit environment. The build process is managed via `.yml` (GitHub Actions) as there are complex Android SDK dependencies that are not currently configured in the local Nix environment.

### Application Type
- **Platform**: Android native application
- **Game Type**: Passive simulation/observation game (not interactive gameplay)
- **UI Pattern**: Button-driven progression with results display

### Core Game Mechanics

**Player System**:
- Fixed 32 players per game session
- Players identified internally by single alphabet letters (A-Z + additional characters)
- Real names revealed only in specific contexts (Top 3 movies, earnings list, Oscar winner)

**Economic System**:
- All players start with ₹0 balance
- Mandatory loan system at game start
- Loan amount = Movie budget (random 10-100)
- Loans subtracted from balance, earnings added back

**Tournament Structure**:
- Round 1: 32 players → Top 16 advance
- Round 2: 16 players → Top 8 advance
- Round 3: 8 players → Top 4 advance (displays Top 3 movies)
- Semi-Final: 4 players → Finals
- Each round uses random number generation (0-100) for movie earnings

### State Management
- Game state persists across rounds within a single year/session
- Player balances tracked cumulatively
- Progression controlled entirely by user button clicks

### Randomization
- All outcomes determined by random number generation
- No skill or strategy component - pure simulation

## External Dependencies

### Required Technologies
- **Android SDK**: Native Android development
- **Random Number Generation**: Built-in system RNG for movie earnings and loan amounts

### Data Storage
- Local state management only (no persistent database required based on current spec)
- Session-based data that resets each new game/year

### External Services
- None required - fully offline, self-contained simulation