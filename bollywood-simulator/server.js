const express = require('express');
const path = require('path');
const sqlite3 = require('sqlite3').verbose();
const app = express();

const db = new sqlite3.Database(':memory:');

db.serialize(() => {
  db.run(`CREATE TABLE IF NOT EXISTS history (
    id INTEGER PRIMARY KEY,
    year INTEGER,
    winner TEXT,
    topMovies TEXT,
    leaderboard TEXT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
  )`);
  
  db.run(`CREATE TABLE IF NOT EXISTS oscar_winners (
    id INTEGER PRIMARY KEY,
    year INTEGER,
    winnerName TEXT,
    finalEarnings INTEGER
  )`);
});

app.use(express.static('public'));
app.use(express.json());

app.get('/api/history', (req, res) => {
  db.all('SELECT * FROM history ORDER BY year DESC', (err, rows) => {
    res.json(rows || []);
  });
});

app.get('/api/history/:year', (req, res) => {
  const year = req.params.year;
  db.get('SELECT * FROM history WHERE year = ?', [year], (err, row) => {
    res.json(row || {});
  });
});

app.get('/api/oscar-winners', (req, res) => {
  db.all('SELECT * FROM oscar_winners ORDER BY year DESC', (err, rows) => {
    res.json(rows || []);
  });
});

app.post('/api/save-game', (req, res) => {
  const { year, winner, topMovies, leaderboard } = req.body;
  db.run(
    'INSERT INTO history (year, winner, topMovies, leaderboard) VALUES (?, ?, ?, ?)',
    [year, winner, JSON.stringify(topMovies), JSON.stringify(leaderboard)],
    function(err) {
      if (!err) {
        db.run(
          'INSERT INTO oscar_winners (year, winnerName, finalEarnings) VALUES (?, ?, ?)',
          [year, winner.name, winner.finalEarnings]
        );
      }
      res.json({ success: !err });
    }
  );
});

const PORT = 5000;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`Bollywood Simulator running on http://0.0.0.0:${PORT}`);
});
