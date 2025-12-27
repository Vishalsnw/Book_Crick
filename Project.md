Build an Android app called “Bollywood Simulator” meant purely for personal entertainment and observation.
This is a simulation game, not interactive gameplay. The user only observes outcomes and controls progression using buttons.
🧠 Core Concept
The game simulates 32 Bollywood producers/players.
Each game represents 1 Year of Bollywood.
All actions are automatic and random.
The user only:
Starts the game
Clicks Next Round buttons
Observes results
👤 Players Setup
There are exactly 32 players.
Players are identified internally using single alphabet letters (A–Z + extra letters).
No real names initially.
Real player names are revealed only in:
Top 3 movies display
Player earnings list
Oscar winner section
💰 Movie Budget & Loan System (Round 1 Start)
At the start of the year:
Every player has ₹0 balance
Every player must take a loan
Loan amount is decided by:
A random number between 10 and 100
This number is:
The movie budget
Also the loan amount
Loan is subtracted from the player’s balance
🎥 Rounds Structure (Button-Based Progression)
Each round happens only when user clicks a button.
🟡 Round 1
32 players release movies
Each generates a random number 0–100
This number = movie earnings
Top 16 players move to next round
Earnings are added to balance
🟡 Round 2
16 players generate new random numbers
Top 8 players move forward
Earnings added
🟡 Round 3
8 players generate numbers
Top 4 players move forward
From these 8:
Display Top 3 Movies on screen
Show:
Movie rank
Player name
Earnings number
🟡 Semi-Final
4 players compete
Top 2 players advance
🟡 Final Round
Final 2 players compete
Higher number wins
Winner receives:
Oscar Award
Special visual highlight
🧮 Earnings & Balance Logic
Every number generated in every round is added to total earnings
Final Balance =
Total Earnings – Loan Taken
Balance can be positive or negative
📊 Live Rankings Screen (Very Important)
Always show a sorted list of all players:
Sorted by current balance (highest first)
Each row shows:
Player Name
Loan Taken
Total Earnings
Current Balance
This list updates after every round
🏆 Special Sections
🎖 Oscar Winners Section
Maintain a separate list
Shows:
Year
Winning Player Name
Final Earnings
Persist across games
💸 Highest Grossing Movies Section
Shows:
Top movies across all years
Player name
Earnings number
Sorted descending
🕰 History System
Each completed game = 1 Year
History must be saved automatically
User can:
View past years
Tap a year to see:
Winner
Top 3 movies
Final leaderboard
🎨 Visual & UI Style
Filmy Bollywood theme
Dark background with:
Gold highlights
Spotlight effects
Award-show vibes
Oscar winner screen should feel:
Celebratory
Cinematic
Use cards, animations, transitions
App should feel like:
A movie award night
A behind-the-scenes Bollywood simulation
🎮 User Controls
Buttons only:
Start New Year
Next Round
View Leaderboard
View Oscar Winners
View History
No manual input during gameplay.

🎯 Final Goal
The app should feel like:
Watching Bollywood careers unfold automatically
A fun, dramatic simulation
Something the user can replay again and again just to observe outcomes

players name 
Golu", "Amit Bagle", "Mangesh", "Vasim", "Amit Randhe", "Khushi", "Ajinkya", "Vinay",
    "Aashish", "Ashok Singh", "Sandip Basra", "Gokul", "Ritesh", "Bipin", "Ajit Bonde", "Amol Patil",
    "Hemant", "Ravi Patil", "Sachin Pardesi", "Sachin Patil", "Vishal", "Nitin", "Dipak Trivedi",
    "Sunil", "Charu", "Bhavesh Chaudhari", "Dipak R", "Mayur", "Nilesh", "Dipak BH", "Sunil"
