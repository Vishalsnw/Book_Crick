const PLAYER_NAMES = [
  'Anil Ambani', 'Bhushan Kumar', 'Chitransh Singh', 'Dhawal Patel',
  'Ekta Kapoor', 'Farhan Akhtar', 'Gunjan Malhotra', 'Harsh Varrdhan',
  'Indrajit Verma', 'Jackky Bhagnani', 'Karan Johar', 'Laxman Pandey',
  'Manish Malhotra', 'Nikhil Dwivedi', 'Om Prakash', 'Priyanka Chopra',
  'Qayum Khan', 'Rajesh Roshan', 'Sanjay Singh', 'Tarun Mansukhani',
  'Uday Chopra', 'Vicky Kaushal', 'Waqar Hussein', 'Xerxes Desai',
  'Yogendra Singh', 'Zoya Akhtar', 'Aman Verma', 'Bhavna Singh',
  'Charan Nair', 'Deepak Malhotra', 'Esha Deol', 'Fawad Khan'
];

class GameState {
  constructor() {
    this.year = 0;
    this.round = 1;
    this.players = [];
    this.activePlayers = [];
    this.topMoviesRound3 = [];
    this.gameActive = false;
    this.history = [];
    this.oscarWinners = [];
    this.loadHistory();
  }

  initializePlayers() {
    this.players = PLAYER_NAMES.map((name, idx) => ({
      id: idx,
      name: name,
      letter: String.fromCharCode(65 + (idx % 26)) + (idx >= 26 ? (idx - 25) : ''),
      loanTaken: Math.floor(Math.random() * 91) + 10,
      balance: 0,
      totalEarnings: 0,
      lastRoundEarning: 0,
      eliminated: false
    }));

    this.players.forEach(player => {
      player.balance = -player.loanTaken;
    });

    this.activePlayers = [...this.players];
  }

  startGame() {
    this.year++;
    this.round = 1;
    this.gameActive = true;
    this.initializePlayers();
    this.topMoviesRound3 = [];
    showScreen('gameScreen');
    this.updateGameUI();
  }

  nextRound() {
    if (!this.gameActive) return;

    let nextPlayerCount;
    let roundDescription;
    
    switch(this.round) {
      case 1: // 32 -> 16
        nextPlayerCount = 16;
        roundDescription = '16 players move to Round 2';
        break;
      case 2: // 16 -> 8
        nextPlayerCount = 8;
        roundDescription = '8 players move to Round 3';
        break;
      case 3: // 8 -> 4 (and capture top 3)
        nextPlayerCount = 4;
        roundDescription = '4 players move to Semi-Final';
        this.captureTop3Movies();
        break;
      case 4: // 4 -> 2
        nextPlayerCount = 2;
        roundDescription = '2 players move to Final';
        break;
      case 5: // Finals
        this.declareWinner();
        return;
      default:
        return;
    }

    this.executeRound(nextPlayerCount);
    this.round++;
    this.updateGameUI();
  }

  executeRound(nextPlayerCount) {
    this.activePlayers.forEach(player => {
      const earning = Math.floor(Math.random() * 101);
      player.lastRoundEarning = earning;
      player.totalEarnings += earning;
      player.balance += earning;
    });

    this.activePlayers.sort((a, b) => b.balance - a.balance);
    this.activePlayers = this.activePlayers.slice(0, nextPlayerCount);
  }

  captureTop3Movies() {
    const sorted = [...this.activePlayers].sort((a, b) => b.lastRoundEarning - a.lastRoundEarning);
    this.topMoviesRound3 = sorted.slice(0, 3).map(p => ({
      rank: this.topMoviesRound3.length + 1,
      name: p.name,
      earnings: p.lastRoundEarning
    }));
  }

  declareWinner() {
    const final2 = [...this.activePlayers].sort((a, b) => b.balance - a.balance);
    const winner = final2[0];
    
    this.oscarWinners.push({
      year: this.year,
      name: winner.name,
      finalEarnings: winner.totalEarnings,
      finalBalance: winner.balance
    });

    this.gameActive = false;

    this.saveGameToHistory({
      year: this.year,
      winner: winner.name,
      finalBalance: winner.balance,
      topMovies: this.topMoviesRound3,
      leaderboard: this.activePlayers.slice(0, 5).map(p => ({
        name: p.name,
        balance: p.balance,
        earnings: p.totalEarnings
      }))
    });

    document.getElementById('winnerName').textContent = winner.name;
    document.getElementById('winnerEarnings').textContent = '₹' + winner.totalEarnings;
    document.getElementById('winnerBalance').textContent = '₹' + winner.balance;

    showScreen('winnerScreen');
  }

  updateGameUI() {
    document.getElementById('yearDisplay').textContent = this.year;
    document.getElementById('roundDisplay').textContent = this.round;
    document.getElementById('activePlayersCount').textContent = this.activePlayers.length;
    
    const topEarning = this.activePlayers[0]?.balance || 0;
    document.getElementById('topEarning').textContent = '₹' + topEarning;

    const roundTitles = [
      'Round 1: 32 Movies Release',
      'Round 2: 16 Players Compete',
      'Round 3: 8 Movies Premiere',
      'Semi-Final: 4 Players',
      'Grand Final: The Winner'
    ];
    document.getElementById('roundTitle').textContent = roundTitles[this.round - 1];

    const roundDescs = [
      '32 players release movies. Top 16 advance.',
      '16 players generate earnings. Top 8 advance.',
      '8 players compete. Top 4 advance. Top 3 movies displayed!',
      '4 players compete. Top 2 advance to finals.',
      'Final battle! Highest earning wins the Oscar!'
    ];
    document.getElementById('roundDescription').textContent = roundDescs[this.round - 1];

    if (this.round === 3) {
      document.getElementById('topMoviesContainer').style.display = 'block';
      const html = this.topMoviesRound3.map(m => 
        `<div class="movie-card"><span>#${m.rank}</span><span>${m.name}</span><span>₹${m.earnings}</span></div>`
      ).join('');
      document.getElementById('topMoviesList').innerHTML = html;
    } else {
      document.getElementById('topMoviesContainer').style.display = 'none';
    }

    this.updateLeaderboard();
  }

  updateLeaderboard() {
    const sorted = [...this.activePlayers].sort((a, b) => b.balance - a.balance);
    const html = sorted.map((p, idx) => 
      `<tr>
        <td>${idx + 1}</td>
        <td>${p.name}</td>
        <td>₹${p.loanTaken}</td>
        <td>₹${p.totalEarnings}</td>
        <td class="${p.balance >= 0 ? 'positive' : 'negative'}">₹${p.balance}</td>
      </tr>`
    ).join('');
    document.getElementById('leaderboardBody').innerHTML = html;
  }

  captureTop3Movies() {
    if (this.round === 3) {
      const sorted = [...this.activePlayers].sort((a, b) => b.lastRoundEarning - a.lastRoundEarning);
      this.topMoviesRound3 = sorted.slice(0, 3).map((p, idx) => ({
        rank: idx + 1,
        name: p.name,
        earnings: p.lastRoundEarning
      }));
    }
  }

  saveGameToHistory(gameData) {
    fetch('/api/save-game', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(gameData)
    });
    this.history.push(gameData);
  }

  loadHistory() {
    fetch('/api/history')
      .then(r => r.json())
      .then(data => {
        this.history = data;
        this.updateHistoryUI();
      });
    
    fetch('/api/oscar-winners')
      .then(r => r.json())
      .then(data => {
        this.oscarWinners = data;
        this.updateOscarUI();
      });
  }

  updateLeaderboardScreen() {
    if (this.activePlayers.length === 0) {
      document.getElementById('noLeaderboard').style.display = 'block';
      return;
    }
    document.getElementById('noLeaderboard').style.display = 'none';
    this.updateLeaderboard();
  }

  updateOscarUI() {
    const container = document.getElementById('oscarWinnersList');
    const noWinners = document.getElementById('noOscars');
    
    if (this.oscarWinners.length === 0) {
      noWinners.style.display = 'block';
      container.innerHTML = '';
      return;
    }

    noWinners.style.display = 'none';
    const html = this.oscarWinners.map(w =>
      `<div class="oscar-card">
        <div class="award-icon">🏆</div>
        <h3>Year ${w.year}</h3>
        <p class="winner-name">${w.name}</p>
        <p class="winning-amount">Earnings: ₹${w.finalEarnings}</p>
      </div>`
    ).join('');
    container.innerHTML = html;
  }

  updateHistoryUI() {
    const container = document.getElementById('historyList');
    const noHistory = document.getElementById('noHistory');
    
    if (this.history.length === 0) {
      noHistory.style.display = 'block';
      container.innerHTML = '';
      return;
    }

    noHistory.style.display = 'none';
    const html = this.history.map(h =>
      `<div class="history-card">
        <h3>Year ${h.year}</h3>
        <p>Winner: ${h.winner}</p>
        <button class="btn-small" onclick="gameState.viewYearDetails(${h.year})">View Details</button>
      </div>`
    ).join('');
    container.innerHTML = html;
  }

  viewYearDetails(year) {
    alert(`Year ${year} details:\nWinner: ${this.history.find(h => h.year === year)?.winner}`);
  }
}

const gameState = new GameState();

function showScreen(screenId) {
  document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
  document.getElementById(screenId).classList.add('active');
  
  if (screenId === 'leaderboardScreen') {
    gameState.updateLeaderboardScreen();
  } else if (screenId === 'oscarScreen') {
    gameState.updateOscarUI();
  } else if (screenId === 'historyScreen') {
    gameState.updateHistoryUI();
  }
}

document.addEventListener('DOMContentLoaded', () => {
  gameState.loadHistory();
});
