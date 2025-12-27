# Bollywood Simulator

## Overview

Bollywood Simulator is an Android entertainment app that simulates a year in the Bollywood film industry. The app features 32 AI-controlled producers/players who compete through multiple elimination rounds. This is a passive simulation game where users observe outcomes rather than actively participate - they only control progression by clicking "Next Round" buttons.

The game follows a tournament-style structure where players take loans, produce movies with random earnings, and compete through elimination rounds until a winner is determined. The app is designed for personal entertainment and observation of simulated Bollywood industry dynamics.

## User Preferences

Preferred communication style: Simple, everyday language.

## System Architecture

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