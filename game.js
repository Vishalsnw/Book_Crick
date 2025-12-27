const PLAYER_NAMES = [
    "Golu", "Amit Bagle", "Mangesh", "Vasim", "Amit Randhe", "Khushi", "Ajinkya", "Vinay",
    "Aashish", "Ashok Singh", "Sandip Basra", "Gokul", "Ritesh", "Bipin", "Ajit Bonde", "Amol Patil",
    "Hemant", "Ravi Patil", "Sachin Pardesi", "Sachin Patil", "Vishal", "Nitin", "Dipak Trivedi",
    "Sunil", "Charu", "Bhavesh Chaudhari", "Dipak R", "Mayur", "Nilesh", "Dipak BH", "Akshit", "Rajesh"
];

let players = [];
let oscarWinners = [];
let currentYear = 1;
let gameState = "START";

const elements = {
    yearBadge: document.getElementById('yearBadge'),
    actionButton: document.getElementById('actionButton'),
    topMoviesSection: document.getElementById('topMoviesSection'),
    topMoviesTitle: document.getElementById('topMoviesTitle'),
    topMoviesText: document.getElementById('topMoviesText'),
    statsText: document.getElementById('statsText'),
    oscarList: document.getElementById('oscarList')
};

function init() {
    loadData();
    elements.actionButton.addEventListener('click', handleButtonClick);
    updateUI();
}

function handleButtonClick() {
    switch (gameState) {
        case "START": startNewYear(); break;
        case "ROUND1": playRound(16, "ROUND 2"); break;
        case "ROUND 2": playRound(8, "ROUND 3"); break;
        case "ROUND 3": playRound(4, "SEMI-FINAL"); break;
        case "SEMI-FINAL": playRound(2, "FINAL"); break;
        case "FINAL": playRound(1, "WINNER"); break;
        case "WINNER": gameState = "START"; updateUI(); break;
    }
}

function startNewYear() {
    players = PLAYER_NAMES.map(name => {
        const loan = Math.floor(Math.random() * 91) + 10;
        return {
            name: name,
            loan: loan,
            earnings: 0,
            balance: -loan,
            lastEarnings: 0,
            active: true
        };
    });
    gameState = "ROUND1";
    elements.topMoviesSection.style.display = 'none';
    updateUI();
}

function playRound(advanceCount, nextState) {
    const activePlayers = players.filter(p => p.active);
    const roundMovies = [];

    activePlayers.forEach(p => {
        const earnings = Math.floor(Math.random() * 101);
        p.earnings += earnings;
        p.balance = p.earnings - p.loan;
        p.lastEarnings = earnings;
        roundMovies.push({ playerName: p.name, earnings: earnings });
    });

    activePlayers.sort((a, b) => b.lastEarnings - a.lastEarnings);
    activePlayers.forEach((p, i) => {
        p.active = (i < advanceCount);
    });

    if (gameState === "ROUND 2") {
        showTopMovies(roundMovies, 5, "Top 5 Movies");
    }

    if (gameState === "ROUND 3") {
        showTopMovies(roundMovies, 3, "Top 3 Movies");
    }

    if (nextState === "WINNER") {
        const winner = activePlayers[0];
        oscarWinners.push(`Year ${currentYear}: ${winner.name} (₹${winner.earnings})`);
        currentYear++;
        saveData();
    }

    gameState = nextState;
    updateUI();
}

function showTopMovies(movies, count, title) {
    movies.sort((a, b) => b.earnings - a.earnings);
    let html = '';
    movies.slice(0, count).forEach((m, i) => {
        html += `<div>#${i + 1} ${m.playerName}: ₹${m.earnings}</div>`;
    });
    elements.topMoviesTitle.innerText = `🎬 ${title}`;
    elements.topMoviesText.innerHTML = html;
    elements.topMoviesSection.style.display = 'block';
}

function updateUI() {
    elements.yearBadge.innerText = `Year ${currentYear}`;
    
    if (gameState === "WINNER") {
        const winner = players.find(p => p.active);
        elements.statsText.innerHTML = `🏆 OSCAR WINNER: ${winner ? winner.name : "N/A"}<br>Final Balance: ₹${winner ? winner.balance : 0}`;
        elements.actionButton.innerText = "Start Next Year";
        return;
    }

    elements.actionButton.innerText = gameState === "START" ? "Start Game" : `Next Round (${gameState})`;

    let table = `<table style="width:100%; text-align:left;">
        <tr>
            <th>Name</th>
            <th>Loan</th>
            <th>Earn</th>
            <th>Bal</th>
        </tr>`;
    
    const sorted = [...players].sort((a, b) => b.balance - a.balance);
    sorted.forEach(p => {
        table += `<tr style="${p.active ? 'color:#FFD700;' : 'color:#888;'}">
            <td>${p.name}${p.active ? ' ★' : ''}</td>
            <td>${p.loan}</td>
            <td>${p.earnings}</td>
            <td>${p.balance}</td>
        </tr>`;
    });
    table += '</table>';

    elements.statsText.innerHTML = table;

    if (oscarWinners.length > 0) {
        let oscarHtml = '';
        [...oscarWinners].reverse().forEach(winner => {
            oscarHtml += `<div>${winner}</div>`;
        });
        elements.oscarList.innerHTML = oscarHtml;
    }
}

function saveData() {
    localStorage.setItem('BollywoodOscars', JSON.stringify(oscarWinners));
    localStorage.setItem('BollywoodYear', currentYear);
}

function loadData() {
    const savedOscars = localStorage.getItem('BollywoodOscars');
    if (savedOscars) oscarWinners = JSON.parse(savedOscars);
    
    const savedYear = localStorage.getItem('BollywoodYear');
    if (savedYear) currentYear = parseInt(savedYear);
}

init();
